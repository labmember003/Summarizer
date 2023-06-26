package com.example.summarizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.summarizer.DataManager.currentPage
import com.example.summarizer.ui.theme.SummarizerTheme

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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SummarizerTheme {
        Greeting("Android")
    }
}