package br.com.pintos.coletordroid

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ColetorService {
  @GET("coletor/viewmodel/{id}")
  fun viewModel(@Path("id") id : String): Call<Result>

  @GET("coletor/leitura/{value}/{id}")
  fun processaLeitura(@Path("value") value: String, @Path("id") id : String): Call<Result>
}

data class Result(val id: String?, val viewModel: ColetorVO, val messages: Messages)

data class ColetorVO(
    val lblInventario: String?,
    val lblUsuario: String?,
    val lblLote: String?,
    val itens: List<String>,
    val lblLeitura: String
                    )

data class Messages(var msgWarning: String = "", var msgError: String = "", var msgInfo: String = "")