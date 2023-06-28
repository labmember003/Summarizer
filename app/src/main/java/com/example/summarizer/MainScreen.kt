package com.example.summarizer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.IOException

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val bottomSheetOpen = remember{ mutableStateOf(false) }
    val context = LocalContext.current
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


        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
//            val item = result?.let { context.contentResolver.openInputStream(it) }
//            val bytes = item?.readBytes()
//            println(bytes)
//            Log.i("sexxxx", bytes.toString())
//            item?.close()
            if (uri != null) {
                val text = readPDFFile(uri, context)
                Log.i("sexxxx", text)
            }
        }
        BottomSheet(

            isOpen = bottomSheetOpen.value,
            onDismiss = { bottomSheetOpen.value = false },
            onPdfOptionClicked = {
                launcher.launch("application/pdf")
            },
            onImageOptionClicked = { /* Handle option 2 click */ }
        )
    }

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
    onPdfOptionClicked: () -> Unit,
    onImageOptionClicked: () -> Unit
) {
    if (isOpen) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp, 24.dp, 16.dp, 16.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Choose the file format:",
                    style = MaterialTheme.typography.subtitle1,
                )
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                        }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = painterResource(id = R.drawable.photo),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            onImageOptionClicked()
                            onDismiss()
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.pdffff),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            onPdfOptionClicked()
                            onDismiss()
                        }
                )
            }
        }
    }
}



private fun readPDFFile(uri: Uri, context: Context): String {
    val contentResolver = context.contentResolver
    var convertedText: String = ""
    try {
        val inputStream = contentResolver.openInputStream(uri)
        val document = PDDocument.load(inputStream)
        val stripper = PDFTextStripper()

        convertedText = stripper.getText(document)
        // Do something with the extracted text (e.g., display it)

        document.close()
        inputStream?.close()
    } catch (e: IOException) {
        // Handle any exceptions that occur during reading the PDF
        e.printStackTrace()
    }
    return convertedText
}


