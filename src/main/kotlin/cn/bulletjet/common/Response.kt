package cn.bulletjet.common

import java.io.Serializable

data class Response(
        val ID: Int,
        val result: Any,
        val exception: Exception?
): Serializable
