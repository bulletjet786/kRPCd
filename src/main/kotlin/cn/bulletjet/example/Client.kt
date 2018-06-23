package cn.bulletjet.example

import cn.bulletjet.client.getClient

fun main(args: Array<String>) {
    val client = getClient("cn.yhl.echo", EchoService::class.java)
    println(client.sayHello("Hello"))
}