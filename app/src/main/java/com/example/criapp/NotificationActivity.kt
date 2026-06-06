package com.example.criapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.criapp.services.DialogFactory
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.NotificationAdapter
import com.example.criapp.adapters.NotificationAdapter.FooterState
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.NotificationRepositoryImpl
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.notification.NotificationViewModel
import com.example.criapp.presentation.notification.NotificationViewModelFactory

class NotificationActivity : BaseActivity() {

    private val viewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory(NotificationRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtNoNotifications: TextView
    private var adapter: NotificationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        aLoadingDialog = ALoadingDialog(this)
        recyclerView = findViewById(R.id.recyclerView)
        txtNoNotifications = findViewById(R.id.txtNoNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    if (state.data.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        txtNoNotifications.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        txtNoNotifications.visibility = View.GONE
                        adapter = NotificationAdapter(
                            list = state.data,
                            onItemClick = { notification ->
                                if (!notification.read) viewModel.markAsRead(notification.id)
                            },
                            onLoadMoreClick = { viewModel.loadNextPage() }
                        )
                        recyclerView.adapter = adapter
                        adapter?.setFooterState(if (viewModel.isLastPage) FooterState.HIDDEN else FooterState.BUTTON)
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, getString(R.string.error_load_notifications)) { finish() }.show()
                }
                else -> Unit
            }
        }

        viewModel.loadMoreState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> adapter?.setFooterState(FooterState.LOADING)
                is UiState.Success -> {
                    adapter?.appendItems(state.data)
                    adapter?.setFooterState(if (viewModel.isLastPage) FooterState.HIDDEN else FooterState.BUTTON)
                }
                is UiState.Error -> {
                    adapter?.setFooterState(if (viewModel.isLastPage) FooterState.HIDDEN else FooterState.BUTTON)
                    Toast.makeText(this, getString(R.string.error_load_notifications), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.markReadState.observe(this) { state ->
            if (state is UiState.Success) {
                adapter?.updateItem(state.data)
                Toast.makeText(this, getString(R.string.notification_marked_as_read), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadHistory()
    }
}
