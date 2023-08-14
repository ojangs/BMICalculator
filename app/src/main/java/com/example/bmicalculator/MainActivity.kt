package com.example.bmicalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    val berat = binding.isiBerat.text.toString().toFloatOrNull()
    val tinggi = binding.isiTinggi.text.toString().toFloatOrNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (berat != null && tinggi != null) {
            binding.btnHitung.setOnClickListener() {
                myCoroutineScope.launch {
                    getBmiIndex()
                    weightCategoryBMI()
                }
            }
        } else
            binding.hasilHitung.text = "MOHON DIISI TERLEBIH DAHULU !"
    }

    private fun getBmiIndex(): Double{
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