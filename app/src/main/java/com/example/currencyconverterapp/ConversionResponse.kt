package com.example.currencyconverterapp

data class ConversionResponse(
    val status: String,
    val updated_date: String,
    val base_currency_code: String,
    val base_currency_name: String,
    val amount: Double,
    val rates: Map<String, Rate>
)
