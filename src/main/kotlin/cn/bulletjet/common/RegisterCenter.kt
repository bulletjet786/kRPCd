package cn.bulletjet.common

interface RegisterCenter {

    fun getService(name:String): ServiceInfo
    fun register(service: ServiceInfo)
}