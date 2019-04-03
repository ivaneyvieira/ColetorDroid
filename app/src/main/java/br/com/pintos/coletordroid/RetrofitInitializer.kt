package br.com.pintos.coletordroid

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {
  val retrofit = Retrofit.Builder()
      .baseUrl("http://10.1.10.100:8090/")
      .addConverterFactory(GsonConverterFactory.create())
      .validateEagerly(true)
      .build()

  fun coletorService() = retrofit.create(ColetorService::class.java)
}