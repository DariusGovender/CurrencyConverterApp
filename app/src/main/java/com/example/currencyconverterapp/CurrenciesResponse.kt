package com.example.currencyconverterapp

data class CurrenciesResponse(
    val status: String,
    val currencies: Map<String, String>
)
