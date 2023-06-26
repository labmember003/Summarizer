package com.example.summarizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.summarizer.DataManager.currentPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            when (currentPage.value) {
                PAGES.WALKTHROUGH -> {
                    WalkThroughScreen()
                }
                else -> {}
            }
        }
    }
}

enum class PAGES {
    LOGIN, USERS, WALKTHROUGH
}