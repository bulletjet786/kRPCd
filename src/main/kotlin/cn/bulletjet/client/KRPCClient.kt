package cn.bulletjet.client

import cn.bulletjet.common.*
import com.google.gson.GsonBuilder
import java.io.*
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.Socket
import java.util.*


fun <T> getClient(name:String, clazz: Class<T>): T {
    return Proxy.newProxyInstance(clazz.classLoader, Array(1) { clazz }, RPCInvocationHandler(name)) as T
}

class RPCInvocationHandler(private val name:String,
                           registerCenter: RegisterCenter = RedisRegisterCenter("127.0.0.1", 6379, "bullet")) : InvocationHandler {

    private val service : ServiceInfo = registerCenter.getService(name)
    private val random = Random()
    private val socket = Socket(service.ip, service.port)

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        val request = Request(random.nextInt(), name, method!!.name, method.parameterTypes, args as Array<Any>)

        val bufferBaos = ByteArrayOutputStream()
        val bufferOs = ObjectOutputStream(bufferBaos)
        bufferOs.writeObject(request)
        val requestByteArray = bufferBaos.toByteArray()

        val os = DataOutputStream(socket.getOutputStream())
        os.writeInt(requestByteArray.size)
        os.write(requestByteArray)

        val ins = ObjectInputStream(socket.getInputStream())
        val response = ins.readObject() as Response
        return response.result
    }

}
