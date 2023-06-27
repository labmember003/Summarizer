package com.example.summarizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.summarizer.DataManager.currentPage

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            when (currentPage.value) {
                PAGES.WALKTHROUGH -> {
                    WalkThroughScreen()
                }
                PAGES.LOGIN -> {
                    LoginScreen()
                }
                PAGES.MAINPAGE -> {
                    MainScreen()
                }
            }
        }
    }
}

enum class PAGES {
    LOGIN, WALKTHROUGH, MAINPAGE
}

enum class SCREEN {
    LOGIN, SIGNUP
}

@Composable
fun LottieAnimation(animationID: Int) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(animationID))
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .size(400.dp)
    )
}


@Composable
fun OneDpHeightLightGreyLine() {
    Divider(
        modifier = Modifier
            .height(1.dp)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.LightGray, // Set the color to light grey
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx() // Convert 1dp to pixels
                )
            },
        color = Color.Transparent, // Make the divider transparent
        thickness = 1.dp // Set the thickness to 1dp
    )
}

@Composable
fun LineWithText() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Divider(
            modifier = Modifier
                .height(1.dp)
                .weight(1f),
            color = Color.LightGray
        )
        Text(
            text = "OR",
            color = colorResource(id = R.color.light_grey),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(
            modifier = Modifier
                .height(1.dp)
                .weight(1f),
            color = Color.LightGray
        )
    }
}

@Composable
fun EditText(text: String, visualTransformation: VisualTransformation = VisualTransformation.None) {
    val content = remember { mutableStateOf("") }
    OutlinedTextField(
        value = content.value,
        onValueChange = {
            content.value = it
        },
        label = { androidx.compose.material.Text(text) },
        modifier = Modifier.padding(16.dp),
        visualTransformation = visualTransformation
    )
}