package com.example.criapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.ForumRepositoryImpl
import com.example.criapp.databinding.ActivityCreateForumBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.CreateForumModel
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.forum.ForumViewModel
import com.example.criapp.presentation.forum.ForumViewModelFactory

class CreateForumActivity : AppCompatActivity() {

    private val viewModel: ForumViewModel by viewModels {
        ForumViewModelFactory(ForumRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: CreateForumModel
    private lateinit var mBinding: ActivityCreateForumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCreateForumBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)

        mBinding.textViewErrors.visibility = View.GONE

        mBinding.labelEditTextName.markRequired()
        mBinding.labelEditTextDescription.markRequired()

        model = CreateForumModel(mBinding.editTextName, mBinding.editTextDescription)

        mBinding.buttonCreate.setOnClickListener {
            if (model.validate(mBinding.textViewErrors, this)) {
                mBinding.textViewErrors.visibility = View.GONE
                viewModel.createForum(model.toRequest())
            } else {
                mBinding.textViewErrors.visibility = View.VISIBLE
            }
        }

        viewModel.createForumState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.forum_created_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.error_save_forum), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }
}
