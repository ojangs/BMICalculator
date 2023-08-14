package com.example.bmicalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.bmicalculator.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val myCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        // Menambahkan listener pada EditText
        binding.isiBerat.addTextChangedListener(textWatcher)
        binding.isiTinggi.addTextChangedListener(textWatcher)

        // Menjalankan validasi awal
        validateButton()

            binding.btnHitung.setOnClickListener() {
                if(binding.btnHitung.isEnabled){
                    myCoroutineScope.launch {
                        getBmiIndex()
                        weightCategoryBMI()
                    }
                }

            }

    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateButton()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Do nothing
        }
    }

    private fun validateButton() {
        val inputText1 = binding.isiBerat.text.toString().trim()
        val inputText2 = binding.isiTinggi.text.toString().trim()

        // Validasi apakah salah satu dari inputText kosong atau berisi "0"
        val isInputInvalid = inputText1.isEmpty() || inputText1 == "0" ||
                inputText2.isEmpty() || inputText2 == "0"

        // Mengaktifkan atau menonaktifkan tombol berdasarkan validasi
        binding.btnHitung.isEnabled = !isInputInvalid
    }

    private fun getBmiIndex(): Double{
        val berat = binding.isiBerat.text.toString().toFloatOrNull()
        val tinggi = binding.isiTinggi.text.toString().toFloatOrNull()
        var index = 0.0

        if (berat != null && tinggi != null){

            val hasilIndex = berat.toDouble()/(tinggi.toDouble()/100).pow(2)
            index = hasilIndex

        }else{
            binding.hasilHitung.text = "Invalid Input"
        }

        return index
    }

    private suspend fun weightCategoryBMI(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://body-mass-index-bmi-calculator.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-RapidAPI-Key", "b7d402f00emsh7908b93b00ecb52p1a10cdjsn47a07f924696")
                        .addHeader("X-RapidAPI-Host", "body-mass-index-bmi-calculator.p.rapidapi.com")
                        .build()
                    chain.proceed(request)
                }
                .build())
            .build()

        val apiService = retrofit.create(apiService::class.java)

// Melakukan permintaan dengan nilai BMI yang diinginkan
        val bmiValue = getBmiIndex()
        val response = apiService.getWeightCategory(bmiValue)

        if (response.isSuccessful) {
            val data = response.body()
            val kategori = data?.weightCategory
            val formattedBMI = String.format("%.2f",bmiValue)
            binding.hasilHitung.text = "BMI : ${formattedBMI}\nKategori : $kategori"
        } else {
            binding.hasilHitung.text = "Ada Kesalahan"
        }

    }


}