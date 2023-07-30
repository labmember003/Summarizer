package com.example.summarizer.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Spacer(modifier = Modifier
            .size(100.dp))
        Text(
            text = "Select Your Preferred Language",
            fontSize = 18.sp
        )
        Text(
            text = "Can Be Changed Anytime From Settings",
            color = Color.Gray,
            fontSize = 12.sp
        )
        LottieAnimation(animationID = R.raw.translate_animation)
        LanguagePicker(languages = languages)
        Spacer(modifier = Modifier
            .size(20.dp))
        Button(onClick = {
            navController.navigate("main_screen")
            val editor = sharedPreferences.edit()
            editor.putBoolean(INITIAL_LAUCH, false)
            editor.apply()
        },colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            ) {
            Text(
                text = "NEXT",
            )
        }
    }
}
