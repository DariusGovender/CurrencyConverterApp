package com.example.currencyconverterapp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    // Get list of supported currencies
    @GET("/v2/currency/list")
    fun getCurrencies(
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json"
    ): Call<CurrenciesResponse>

    // Convert currency
    @GET("/v2/currency/convert")
    fun convertCurrency(
        @Query("api_key") apiKey: String,
        @Query("from") fromCurrency: String,
        @Query("to") toCurrency: String,
        @Query("amount") amount: Double,
        @Query("format") format: String = "json"
    ): Call<ConversionResponse>
}