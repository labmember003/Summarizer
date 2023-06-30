package com.example.summarizer

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.IOException

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
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
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(1f)
            ) {
                Text(
                    text = "Upload Image Or E-Book",
                    modifier = Modifier
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(id = R.color.light_grey))
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.token),
                        contentDescription = "Your Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                DataManager.currentPage.value = PAGES.BUYTOKEN
                            }
                    )
                    Text(
                        text = "100",
                    )
                }
            }
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
            if (uri != null) {
                val text = extractTextFromPdf(uri, context)
                Log.i("hapyhapyhapy", "text")
                Log.i("hapyhapyhapy", text)
            }
        }
        val launcher2 = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val bitmap: Bitmap = uriToBitmap(context, it)
                getTextFromImage(bitmap, recognizer, context)
            }
        }


        BottomSheet(

            isOpen = bottomSheetOpen.value,
            onDismiss = { bottomSheetOpen.value = false },
            onPdfOptionClicked = {
                launcher.launch("application/pdf")
            },
            onImageOptionClicked = {
                launcher2.launch("image/*")
            }
        )
    }

}



fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        // For older versions, you can use the deprecated `MediaStore` API
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    }
}

@Composable
fun UploadAnimation(open: MutableState<Boolean>) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.upload))
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

private fun extractTextFromPdf(uri: Uri, context: Context): String {
    PDFBoxResourceLoader.init(context)
    val contentResolver = context.contentResolver
    var text = ""
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = PDDocument.load(inputStream)

            val stripper = PDFTextStripper()
            text = stripper.getText(document)

            // Do something with the extracted text
            Log.d(TAG, "Extracted Text: $text")

            document.close()

        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return text
}

private fun getTextFromImage(bitmap: Bitmap, recognizer: TextRecognizer, context: Context){
    val image = bitmap.let {
        InputImage.fromBitmap(it, 0)
    }
    image.let {
        recognizer.process(it)
            .addOnSuccessListener { visionText ->
                Log.i("kaaali billi", visionText.text)
                Toast.makeText(context, visionText.text, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

            }
    }
}

