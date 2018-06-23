package cn.bulletjet.common

import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import java.util.*

val logger = LoggerFactory.getLogger(RedisRegisterCenter::class.java)!!

class RedisRegisterCenter(address:String,port:Int, auth:String): RegisterCenter {

    private var client:Jedis?
    private val random = Random()

    init {
        logger.info("Connect to Redis Register Center on $address:$port ...")
        client = Jedis(address, port)
        if (client != null) {
            logger.info("Connect to Redis Register Center on $address:$port Success!")
        } else {
            logger.info("Connect to Redis Register Center on $address:$port Failure!")
        }
        val reply = client!!.auth(auth)
        if (reply != "OK") {
            logger.info("Jedis auth Failure!CodeReply is $reply")
        } else {
            logger.info("Jedis auth Success!CodeReply is $reply")
        }
    }


    override fun register(service: ServiceInfo) {
        logger.info("Register service $service ...")
        val gson = GsonBuilder().create()
        client!!.sadd(service.name, gson.toJson(service))
        logger.info("Register service $service Success!")
    }

    override fun getService(name: String): ServiceInfo {

        val serviceStr = client!!.srandmember(name)
        logger.info("Get Services: $serviceStr")
        val gson = GsonBuilder().create()
        return gson.fromJson(serviceStr, ServiceInfo::class.java)
    }
}