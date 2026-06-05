package com.example.criapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.ForumRepositoryImpl
import com.example.criapp.databinding.ActivityUpdateForumBinding
import com.example.criapp.helpers.markRequired
import com.example.criapp.models.CreateForumModel
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.forum.ForumViewModel
import com.example.criapp.presentation.forum.ForumViewModelFactory
import com.google.gson.Gson

class UpdateForumActivity : BaseActivity() {

    private val viewModel: ForumViewModel by viewModels {
        ForumViewModelFactory(ForumRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private lateinit var forum: ForumResponse
    private val gson = Gson()
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: CreateForumModel
    private lateinit var mBinding: ActivityUpdateForumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUpdateForumBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        aLoadingDialog = ALoadingDialog(this)
        forum = gson.fromJson(intent.getStringExtra("forum"), ForumResponse::class.java)

        mBinding.labelEditTextName.markRequired()
        mBinding.labelEditTextDescription.markRequired()

        mBinding.editTextName.setText(forum.name)
        mBinding.editTextDescription.setText(forum.description)

        model = CreateForumModel(mBinding.editTextName, mBinding.editTextDescription)

        mBinding.buttonUpdate.setOnClickListener {
            if (model.validate(mBinding.textViewErrors, this)) {
                viewModel.updateForum(forum.id, model.toRequest())
            }
        }

        viewModel.updateForumState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.forum_updated_success), Toast.LENGTH_SHORT).show()
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
