@file:OptIn(ExperimentalMaterialApi::class)

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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.falcon.summarizer.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val recognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
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
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            BottomSheet(
                modalSheetState,
                onPdfOptionClicked = {
                    launcher.launch("application/pdf")
                },
                onImageOptionClicked = {
                    launcher2.launch("image/*")
                }
            )
        }
    ) {
        MainScreenContent(
            modalSheetState = modalSheetState
        )
    }
}

@Composable
private fun MainScreenContent(
    modalSheetState: ModalBottomSheetState
) {
    val scope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.5f)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(1f)
            ) {
                Text(
                    text = "Upload Image Or E-Book",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
                Tokens(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(id = R.color.light_grey))
                        .padding(8.dp)
                )
            }
            UploadAnimation(
                onClick = {
                    scope.launch { modalSheetState.show() }
                }
            )
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
                .weight(0.5f)
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
fun ColumnScope.UploadAnimation(
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.upload))
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .weight(1f)
            .clickable {
                onClick()
            }
    )

}

@Composable
fun Tokens(modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
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
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    state: ModalBottomSheetState,
    onPdfOptionClicked: () -> Unit,
    onImageOptionClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
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
                        scope.launch { state.hide() }
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
                        scope.launch { state.hide() }
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.pdffff),
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        onPdfOptionClicked()
                        scope.launch { state.hide() }
                    }
            )
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