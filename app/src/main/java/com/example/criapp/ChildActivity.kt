package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.ChildRepositoryImpl
import com.example.criapp.helpers.ViewFactory
import com.example.criapp.presentation.child.ChildViewModel
import com.example.criapp.presentation.child.ChildViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory

class ChildActivity : AppCompatActivity() {

    private val viewModel: ChildViewModel by viewModels {
        ChildViewModelFactory(ChildRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)

        aLoadingDialog = ALoadingDialog(this)

        viewModel.childrenState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    val view = findViewById<LinearLayout>(R.id.children)
                    val children = state.data

                    if (children.isEmpty()) {
                        view.addView(ViewFactory.generateEmptyTextMessage(this, resources))
                    }

                    for (child in children) {
                        view.addView(ViewFactory.generateButton(this, resources, child.name, child.toString(), UpdateChildActivity::class.java, "child"))
                    }

                    view.addView(ViewFactory.generateAddButton(this, resources, "Añadir niño(a)", CreateChildActivity::class.java))
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }

        viewModel.loadChildren()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}
