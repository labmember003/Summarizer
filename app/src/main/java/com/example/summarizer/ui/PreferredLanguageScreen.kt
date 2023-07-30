package com.example.summarizer.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.summarizer.LottieAnimation
import com.example.summarizer.Utils.INITIAL_LAUCH
import com.example.summarizer.settings.LanguagePicker
import com.falcon.summarizer.R

@Composable
fun PreferredLanguageScreen(languages: List<String>, navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Your Preferred Language"
        )
        Text(
            text = "You Can Change It Anytime From Settings"
        )
        LottieAnimation(animationID = R.raw.translate_animation)
        LanguagePicker(languages = languages)
        Button(onClick = {
            navController.navigate("main_screen")
            val editor = sharedPreferences.edit()
            editor.putBoolean(INITIAL_LAUCH, false)
            editor.apply()
        }) {
            Text(text = "NEXT")
        }
    }
}
