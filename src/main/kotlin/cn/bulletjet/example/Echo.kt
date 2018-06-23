package cn.bulletjet.example

interface EchoService {
    fun sayHello(what:String): String
}

class EchoServiceImpl:EchoService {
    override fun sayHello(what: String): String {
        return what
    }

}