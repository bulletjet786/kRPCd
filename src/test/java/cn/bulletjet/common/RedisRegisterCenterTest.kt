package cn.bulletjet.common

import org.junit.Test

class RedisRegisterCenterTest {

    private val client = RedisRegisterCenter("127.0.0.1", 6379, "bullet")

    @Test
    fun registerTest() {
        val serviceInfo = ServiceInfo(name = "echo", address="127.0.0.1:8557")
        client.register(serviceInfo)
    }
}