package cn.bulletjet.server

import cn.bulletjet.common.*
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.MessageToByteEncoder
import java.net.Inet4Address
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class KRPCd(private val serviceInfo : ServiceInfo,
            private val service: Any,
            private val registerCenter:RegisterCenter = RedisRegisterCenter("127.0.0.1", 6379, "bullet")
) {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()

    fun build() {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(RPCHandler())
    }

    private fun register(registerCenter: RegisterCenter) {
        registerCenter.register(serviceInfo)
    }

    fun stop() {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
        // 从redis上卸载注册地址
    }

    fun start() {
        build()
        register(registerCenter)
        val cf = bootstrap.bind(Inet4Address.getByName(serviceInfo.ip), serviceInfo.port)
        cf.sync()
    }


    inner class RPCHandler : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel?) {
            // 入站
            ch!!.pipeline().addLast(LengthFieldBasedFrameDecoder(Int.MAX_VALUE, 0,4,0,4))
            ch.pipeline().addLast(RequestDecoder())

            // 出站
            // 添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
            ch.pipeline().addLast(ResponseEncoder())

            ch.pipeline().addLast(ServerHandler())

        }

    }

    inner class ResponseEncoder: MessageToByteEncoder<Response>() {
        override fun encode(ctx: ChannelHandlerContext?, msg: Response?, out: ByteBuf?) {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(msg)
            out!!.writeBytes(baos.toByteArray())
        }

    }

    inner class ServerHandler: SimpleChannelInboundHandler<Request>() {
        override fun channelRead0(ctx: ChannelHandlerContext?, msg: Request?) {
            println(msg)
            assert(msg!!.name != serviceInfo.name)
            val method = service.javaClass.getMethod(msg.method, *msg.paramsTypes)
            println(method)
            val result = method.invoke(service, *msg.params)
            val response = Response(msg.ID, result, null)
            println(response)
            ctx!!.writeAndFlush(response)
        }
    }

    inner class RequestDecoder: ByteToMessageDecoder() {
        override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf?, out: MutableList<Any>?) {
            val come = `in`
            val ba = ByteArray(come!!.readableBytes())
            come.readBytes(ba)
            val request = ObjectInputStream(ByteArrayInputStream(ba)).readObject()
            out!!.add(request)
            return
        }
    }
}

