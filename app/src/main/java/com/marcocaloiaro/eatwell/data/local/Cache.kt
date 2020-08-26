package com.marcocaloiaro.eatwell.data.local

interface Cache {

    val size: Int

    operator fun set(key: Any, value: Any)

    operator fun get(key: Any): Any?

    fun remove(key: Any): Any?

    fun clear()

    fun toList() : List<Any?>
}