package cn.bulletjet.example

import cn.bulletjet.common.ServiceInfo
import cn.bulletjet.server.KRPCd

fun main(args: Array<String>) {
    val echo:EchoService = EchoServiceImpl()
    val serviceInfo = ServiceInfo("cn.yhl.echo", "127.0.0.1", 8327)
    KRPCd(serviceInfo, echo).start()
}