package com.example.criapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.AudiovisualResourceAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.ResourceRepositoryImpl
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.resource.ResourceViewModel
import com.example.criapp.presentation.resource.ResourceViewModelFactory

class AudioVisualResourcesActivity : AppCompatActivity() {

    private val viewModel: ResourceViewModel by viewModels {
        ResourceViewModelFactory(ResourceRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiovisual_resources)

        aLoadingDialog = ALoadingDialog(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    recyclerView.adapter = AudiovisualResourceAdapter(state.data.toTypedArray())
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Hubo un error al buscar los recursos audiovisuales, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.loadResources()
    }
}
