package com.example.summarizer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.summarizer.Utils.FONT_SIZE
import kotlinx.coroutines.launch
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SummarizedPage(summarizedText: String?) {
    Log.i("arguementPASSING", summarizedText.toString())
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            BottomSheetFontSize(modalSheetState)
        }
    ) {
        SummarizedPageContent(modalSheetState, summarizedText = summarizedText.toString())
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetFontSize(modalSheetState: ModalBottomSheetState) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp, 24.dp, 16.dp, 16.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.material3.Text(
                text = "Font Size",
                style = MaterialTheme.typography.subtitle1,
                fontSize = 20.sp
            )
            androidx.compose.material.Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .clickable {
                        scope.launch { modalSheetState.hide() }
                    }
            )
        }
        val sharedPreferences = remember {
            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
        }
        val fontSize = sharedPreferences.getInt(FONT_SIZE, 16)
        var selectedFontSize by remember { mutableStateOf(fontSize) }
        val editor = sharedPreferences.edit()
        FontSizeSection(selectedFontSize) { fontSize ->
            selectedFontSize = fontSize
            editor.putInt(FONT_SIZE, fontSize)
            editor.apply()
            Log.i("meoemoeoeoeoeoeoe", fontSize.toString())
            Log.i("meoemoeoeoeoeoeoe", sharedPreferences.getInt(FONT_SIZE, 16).toString())
        }
    }
}

@Composable
fun FontSizeSection(selectedFontSize: Int, onFontSizeSelected: (Int) -> Unit) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            (12..20).forEach { fontSize ->
                val isSelected = fontSize == selectedFontSize
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                        .clickable { onFontSizeSelected(fontSize) }
                        .background(
                            color = if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fontSize.toString(),
                        style = TextStyle(fontSize = fontSize.sp, color = if (isSelected) Color.White else Color.Black)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SummarizedPageContent(modalSheetState: ModalBottomSheetState, summarizedText: String) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val fontSizeState = remember { mutableStateOf(sharedPreferences.getInt(FONT_SIZE, 16)) }
    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == FONT_SIZE) {
                fontSizeState.value = sharedPreferences.getInt(FONT_SIZE, 16)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    Column() {
        HeadingSummarizedPage(modalSheetState)
        val content = remember { mutableStateOf(summarizedText) }

        OutlinedTextField(
            trailingIcon = {
                val context = LocalContext.current
                val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                Column(
//                TODO("UPAR KAISE LAU ISSE?)
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start,
//                    modifier = Modifier
//                        .clickable {
//                            clipboardManager.setPrimaryClip(ClipData.newPlainText("Copied Text", content.value))
//                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Email",
                        tint = Color.Gray,
                        modifier = Modifier
                            .clickable {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("Copied Text", content.value))
                            }
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Email",
                        tint = Color.Gray,
                        modifier = Modifier
                            .clickable {
                                downloadTextPdf(content.value, context)
                            }
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Email",
                        tint = Color.Gray,
                        modifier = Modifier
                            .clickable {
                                shareText(content.value, context)
                            }
                    )
                }

            },
            value = content.value,
            readOnly = true,
            enabled = true,
            onValueChange = {

            },
            label = { Text("Summarized Text") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            visualTransformation = VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = fontSizeState.value.sp
            )
        )
    }

}

fun shareText(value: String, context: Context) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, value)
        type = "text/plain"
    }
    val chooserIntent = Intent.createChooser(intent, null).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val startActivity = { intent: Intent ->
        context.startActivity(intent)
    }
    startActivity.invoke(chooserIntent)
}



fun downloadTextPdf(content: String, context: Context) {
    convertTextToPdfAndSave(context, content)
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HeadingSummarizedPage(modalSheetState: ModalBottomSheetState) {
    val scope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Summarizer",
            style = MaterialTheme.typography.subtitle1,
            fontSize = 20.sp
        )
        IconButton(onClick = {
            scope.launch { modalSheetState.show() }
        }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options"
            )
        }
    }

}