package com.example.comparadortarifas

object FareStore {
    private val prices = mutableMapOf<String, String>()

    @Synchronized
    fun update(app: String, price: String) {
        prices[app] = price
    }

    @Synchronized
    fun snapshot(): Map<String, String> = prices.toMap()

    @Synchronized
    fun clear() {
        prices.clear()
    }
}
