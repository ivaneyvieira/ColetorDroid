package br.com.pintos.coletordroid

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ArrayAdapter
import android.widget.Toast


class MainActivity : AppCompatActivity() {
  val service = RetrofitInitializer().coletorService()
  var idSession: String = "NULO"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    title = "Coleta de Inventário"

    val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
    listView.adapter = adapter

    val text = edtLeitura
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(text.windowToken, 0)

    text.setOnKeyListener { v, keyCode, event ->
      if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
        val leitura: String = text.text.toString()
        text.setText("")
        text.requestFocus()

        val call = service.processaLeitura(leitura, idSession)
        call.enqueue(object : Callback<Result?> {
          override fun onFailure(call: Call<Result?>?, t: Throwable?) {
            Log.e("onFailure error", t?.message)
            t?.let { showErro("Erro de conexão: ${t.message}") }
          }

          override fun onResponse(call: Call<Result?>, response: Response<Result?>?) {
            response?.body()
                ?.let { result ->
                  updateView(result)
                }
          }
        })
        true
      } else false
    }

    cardLote.setOnClickListener {
      showConfirma("Fecha o Lote?") {
        service.fechaLote(idSession).enqueue(object : Callback<Result?> {
          override fun onFailure(call: Call<Result?>?, t: Throwable?) {
            Log.e("onFailure error", t?.message)
            t?.let { showErro("Erro de conexão: ${t.message}") }
          }

          override fun onResponse(call: Call<Result?>, response: Response<Result?>?) {
            response?.body()
                ?.let { result ->
                  updateView(result)
                }
          }
        })
      }
    }

    cardUsuario.setOnClickListener {
      showConfirma("Fecha o usuário?") {
        service.fechaUsuario(idSession).enqueue(object : Callback<Result?> {
          override fun onFailure(call: Call<Result?>?, t: Throwable?) {
            Log.e("onFailure error", t?.message)
            t?.let { showErro("Erro de conexão: ${t.message}") }
          }

          override fun onResponse(call: Call<Result?>, response: Response<Result?>?) {
            response?.body()
                ?.let { result ->
                  updateView(result)
                }
          }
        })
      }
    }
  }

  override fun onResume() {
    super.onResume()
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    val call = service.viewModel(idSession)
    call.enqueue(object : Callback<Result?> {
      override fun onFailure(call: Call<Result?>?, t: Throwable?) {
        Log.e("onFailure error", t?.message)
        t?.let { showErro("Erro de conexão: ${t.message}") }
      }

      override fun onResponse(call: Call<Result?>, response: Response<Result?>?) {
        response?.body()
            ?.let { result ->
              updateView(result)
            }
      }
    })
  }

  fun updateView(result: Result) {
    idSession = result.id ?: ""
    valueInventario.text = result.viewModel.lblInventario
    cardInventario.visibility = if (result.viewModel.lblInventario == null) INVISIBLE else VISIBLE
    valueUsuario.text = result.viewModel.lblUsuario
    cardUsuario.visibility = if (result.viewModel.lblUsuario == null) INVISIBLE else VISIBLE
    valueLote.text = result.viewModel.lblLote
    cardLote.visibility = if (result.viewModel.lblLote == null) INVISIBLE else VISIBLE
    //
    lblLeitura.text = result.viewModel.lblLeitura

    val adapter = listView.adapter as? ArrayAdapter<String>

    adapter?.clear()
    adapter?.addAll(result.viewModel.itens)

    if (result.messages.msgError != "") {
      showErro(result.messages.msgError)
    }
  }

  fun showErro(msg: String) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Erro")
    builder.setMessage(msg)
    builder.create()
        .show()
  }

  fun showConfirma(msg: String, execConfirma: () -> Unit) {
    AlertDialog.Builder(this)
        .setMessage(msg)
        .setNegativeButton("Não") { dialogInterface, i -> }
        .setPositiveButton("Sim") { dialogInterface, i -> execConfirma()}
        .create()
        .show()
  }

}
