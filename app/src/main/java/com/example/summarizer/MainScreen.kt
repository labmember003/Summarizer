package com.example.summarizer

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val bottomSheetOpen = remember{ mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.5f)
        ) {
            Text(
                text = "Upload Image Or E-Book",
                modifier = Modifier
                    .padding(top = 16.dp)
            )
            UploadAnimation(bottomSheetOpen)
            LineWithText()
        }
        val content = remember { mutableStateOf("") }
        OutlinedTextField(
            value = content.value,
            onValueChange = {
                content.value = it
            },
            label = { androidx.compose.material.Text("Input Text Here To Summarize") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(0.5F)
        )
    }
    BottomSheet(
        isOpen = bottomSheetOpen.value,
        onDismiss = { bottomSheetOpen.value = false },
        onOption1Clicked = { /* Handle option 1 click */ },
        onOption2Clicked = { /* Handle option 2 click */ }
//        onCancelButtonClicked = { bottomSheetOpen.value = false }
    )
}

@Composable
fun UploadAnimation(open: MutableState<Boolean>) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.upload))
    val context = LocalContext.current
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .size(350.dp)
            .clickable {
                open.value = true
            }
    )

}


//fun cat() {
//    val intent = Intent()
//    intent.action = Intent.ACTION_GET_CONTENT
//    intent.type = "application/pdf"
//    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/jpg", "application/pdf"))
//    val activity = context as? ComponentActivity
//    if (activity != null) {
//        startActivityForResult(activity, intent, 12, null)
//    }
//}
@Composable
fun BottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onOption1Clicked: () -> Unit,
    onOption2Clicked: () -> Unit,
//    onCancelButtonClicked: () -> Unit
) {
    if (isOpen) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Select an option",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    onOption1Clicked()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Option 1")
            }
            Button(
                onClick = {
                    onOption2Clicked()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Option 2")
            }
        }
    }
}
