package com.marcocaloiaro.eatwell.data.local

class SimpleCache : Cache {
    private val cache = HashMap<Any, Any>()

    override val size: Int
        get() = cache.size

    override fun set(key: Any, value: Any) {
        this.cache[key] = value
    }

    override fun remove(key: Any) = this.cache.remove(key)

    override fun get(key: Any) = this.cache[key]

    override fun clear() = this.cache.clear()

    override fun toList() : List<Any?> {
        return cache.values.toList()
    }
}