package com.example.criapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import androidx.core.content.ContextCompat
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.services.NotificationScheduler

class MenuActivity : BaseActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            NotificationScheduler.schedule(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val name = PreferencesManager(this).getUserName().split(" ")[0]
        findViewById<TextView>(R.id.greeting).text = getString(R.string.greeting, name)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })

        scheduleNotifications()
    }

    private fun scheduleNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationScheduler.schedule(this)
            } else {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificationScheduler.schedule(this)
        }
    }

    fun onButtonChildClick(view: View?) = startActivity(Intent(this, ChildActivity::class.java))
    fun onButtonProfileClick(view: View?) = startActivity(Intent(this, ProfileActivity::class.java))
    fun onButtonQuestionsClick(view: View?) = startActivity(Intent(this, QuestionsActivity::class.java))
    fun onButtonFaqClick(view: View?) = startActivity(Intent(this, FaqActivity::class.java))
    fun onButtonAudioVisualResourcesClick(view: View?) = startActivity(Intent(this, AudioVisualResourcesActivity::class.java))
    fun onButtonForumClick(view: View?) = startActivity(Intent(this, ForumsActivity::class.java))
    fun onButtonNotificationsClick(view: View?) = startActivity(Intent(this, NotificationActivity::class.java))
    fun onButtonChildBookClick(view: View?) = startActivity(Intent(this, ChildBookListActivity::class.java))
}