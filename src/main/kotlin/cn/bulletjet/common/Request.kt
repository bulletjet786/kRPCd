package cn.bulletjet.common

import com.google.gson.*
import java.io.Serializable
import java.lang.reflect.Type
import java.util.*


data class Request (
        val ID:Int,
        val name:String,
        val method:String,
        val paramsTypes: Array<Class<*>>,
        val params:Array<Any>
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Request

        if (ID != other.ID) return false
        if (name != other.name) return false
        if (method != other.method) return false
        if (!Arrays.equals(paramsTypes, other.paramsTypes)) return false
        if (!Arrays.equals(params, other.params)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ID
        result = 31 * result + name.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + Arrays.hashCode(paramsTypes)
        result = 31 * result + Arrays.hashCode(params)
        return result
    }
}