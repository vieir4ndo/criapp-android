package com.example.criapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.FaqAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.FaqRepositoryImpl
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.faq.FaqViewModel
import com.example.criapp.presentation.faq.FaqViewModelFactory

class FaqActivity : AppCompatActivity() {

    private val viewModel: FaqViewModel by viewModels {
        FaqViewModelFactory(FaqRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        aLoadingDialog = ALoadingDialog(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    recyclerView.adapter = FaqAdapter(state.data.toTypedArray())
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Hubo un error al buscar las preguntas, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.loadFaqs()
    }
}
