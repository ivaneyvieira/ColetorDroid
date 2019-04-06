package br.com.pintos.coletordroid

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.xml.datatype.DatatypeConstants.SECONDS
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class RetrofitInitializer {
  val okHttpClient = OkHttpClient.Builder()
      .readTimeout(60, TimeUnit.SECONDS)
      .connectTimeout(60, TimeUnit.SECONDS)
      .build()

  val retrofit = Retrofit.Builder()
      .baseUrl("http://10.1.10.244:8090/")
      .addConverterFactory(GsonConverterFactory.create())
      .validateEagerly(true)
      .client(okHttpClient)
      .build()

  fun coletorService() = retrofit.create(ColetorService::class.java)
}