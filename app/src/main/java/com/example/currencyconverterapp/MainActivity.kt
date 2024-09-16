package com.example.currencyconverterapp

import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val apiKey= "c367b2679fa52f0c0398bcdf96e2488d3a5ac0ee"
    private val api = RetrofitClient.instance

    lateinit var fromCurrencySpinner: Spinner
    lateinit var toCurrencySpinner: Spinner
    lateinit var amountFromTextEdit: EditText
    lateinit var amountToTextView: EditText
    lateinit var convertButton: Button
    lateinit var swapButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)
        amountFromTextEdit = findViewById(R.id.amountFromTextEdit)
        amountToTextView = findViewById(R.id.amountToTextView)
        convertButton = findViewById(R.id.convertButton)
        swapButton = findViewById(R.id.swapButton)

        swapButton.setOnClickListener {
            swapCurrencies()
        }


        fetchCurrencies(api)

        convertButton.setOnClickListener {
            if (validateFields()) {
                val fromCurrency = fromCurrencySpinner.selectedItem.toString().split(" - ")[0]
                val toCurrency = toCurrencySpinner.selectedItem.toString().split(" - ")[0]
                val amount = amountFromTextEdit.text.toString().toDouble()

                // Convert currency
                convertCurrency(api, fromCurrency, toCurrency, amount)
            }
        }
    }


    private fun swapCurrencies() {
        // Swap the selected positions of the currency spinners
        val fromCurrencyPosition = fromCurrencySpinner.selectedItemPosition
        val toCurrencyPosition = toCurrencySpinner.selectedItemPosition

        fromCurrencySpinner.setSelection(toCurrencyPosition)
        toCurrencySpinner.setSelection(fromCurrencyPosition)

        if (validateFields()) {
            val fromCurrency = fromCurrencySpinner.selectedItem.toString().split(" - ")[0]
            val toCurrency = toCurrencySpinner.selectedItem.toString().split(" - ")[0]
            val amount = amountFromTextEdit.text.toString().toDouble()

            // Convert currency
            convertCurrency(api, fromCurrency, toCurrency, amount)
        }
    }

    private fun fetchCurrencies(api: CurrencyApi) {
        api.getCurrencies(apiKey, "json").enqueue(object : Callback<CurrenciesResponse> {
            override fun onResponse(call: Call<CurrenciesResponse>, response: Response<CurrenciesResponse>) {
                if (response.isSuccessful) {
                    val currencyList = response.body()?.currencies?.map { "${it.key} - ${it.value}" }?.toList() ?: listOf()

                    // Populate both spinners with currency data
                    populateSpinner(fromCurrencySpinner, currencyList)
                    populateSpinner(toCurrencySpinner, currencyList)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load currencies", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrenciesResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateSpinner(spinner: Spinner, currencyList: List<String>) {
        // Sort the currency list alphabetically and populate the spinner
        val sortedCurrencyList = currencyList.sorted()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortedCurrencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun validateFields(): Boolean {
        // Validate that all required fields are filled
        val fromCurrencySelected = fromCurrencySpinner.selectedItem != null
        val toCurrencySelected = toCurrencySpinner.selectedItem != null
        val amountText = amountFromTextEdit.text.toString()

        return when {
            !fromCurrencySelected -> {
                Toast.makeText(this, "Please select a currency to convert from.", Toast.LENGTH_SHORT).show()
                false
            }
            !toCurrencySelected -> {
                Toast.makeText(this, "Please select a currency to convert to.", Toast.LENGTH_SHORT).show()
                false
            }
            amountText.isEmpty() -> {
                Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show()
                false
            }
            amountText.toDoubleOrNull() == null -> {
                Toast.makeText(this, "Please enter a valid numeric amount.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun convertCurrency(api: CurrencyApi, from: String, to: String, amount: Double) {
        // Make API call to convert currency
        api.convertCurrency(apiKey, from, to, amount).enqueue(object : Callback<ConversionResponse> {
            override fun onResponse(call: Call<ConversionResponse>, response: Response<ConversionResponse>) {
                if (response.isSuccessful) {
                    val rate = response.body()?.rates?.get(to)?.rate_for_amount
                    amountToTextView.setText("$rate")
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ConversionResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
