package com.example.summarizer

import android.os.Handler
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.example.summarizer.Utils.LANGUAGE
import com.falcon.summarizer.R
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SummarizedPage(summarizedText: String?) {
    Log.i("arguementPASSING", summarizedText.toString())
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val languages = listOf(
        Language("English", "en"),
        Language("Español", "es"), // Spanish
        Language("Français", "fr"), // French
        Language("Deutsch", "de"), // German
        Language("简体中文", "zh"), // Chinese (Simplified)
        Language("日本語", "ja"), // Japanese
        Language("العربية", "ar"), // Arabic
        Language("हिन्दी", "hi"), // Hindi
        Language("русский", "ru"), // Russian
        Language("한국어", "ko"), // Korean
        Language("Italiano", "it"), // Italian
        Language("Português", "pt") // Portuguese
        // Add more languages as needed
    )
    val languageMap: Map<String, String> = languages.associate { it.languageName to it.languageCode }
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            Column() {
                BottomSheetFontSize(modalSheetState)

                LanguagePicker(languages)
            }
        }
    ) {
        SummarizedPageContent(modalSheetState, summarizedText = summarizedText.toString(), languageMap = languageMap)
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
fun SummarizedPageContent(
    modalSheetState: ModalBottomSheetState,
    summarizedText: String,
    languageMap: Map<String, String>
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val fontSizeState = remember { mutableStateOf(sharedPreferences.getInt(FONT_SIZE, 16)) }
    val languageState = remember { mutableStateOf(sharedPreferences.getString(LANGUAGE, "ENGLISH")) }
    var languageCode = languageMap[languageState.value]
    Log.i("catcatcatcat", languageCode.toString())
    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == FONT_SIZE) {
                fontSizeState.value = sharedPreferences.getInt(FONT_SIZE, 16)
            }
            if (key == LANGUAGE) {
                languageState.value = sharedPreferences.getString(LANGUAGE, "ENGLISH")
                languageCode = languageMap[languageState.value]
                Log.i("catcatcatcat - 22222", languageCode.toString())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    Column {
        HeadingSummarizedPage(modalSheetState)
        val summary = remember { mutableStateOf(summarizedText) }
        val isTranslationLoading = remember {mutableStateOf(false)}
//        val content = remember { mutableStateOf(translateText(summary.value, languageCode.toString(), isTranslationLoading)) }
        val content2 = translateText(summary.value, languageCode.toString(), isTranslationLoading)
        Log.i("catqwertyuiopp", content2)
//        Log.i("catqwertyuiopp - 1", content.value)
        if (!isTranslationLoading.value) {
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
                                    clipboardManager.setPrimaryClip(ClipData.newPlainText("Copied Text", content2))
                                }
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Email",
                            tint = Color.Gray,
                            modifier = Modifier
                                .clickable {
                                    downloadTextPdf(content2, context)
                                }
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Email",
                            tint = Color.Gray,
                            modifier = Modifier
                                .clickable {
                                    shareText(content2, context)
                                }
                        )
                    }

                },
                value = content2,
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
        } else {
            LottieAnimation(animationID = R.raw.translate_animation)
        }

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


@Composable
fun LanguagePicker(languages: List<Language>){
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val language = sharedPreferences.getString(LANGUAGE, "ENGLISH")
    Log.i(LANGUAGE, "meow "+language.toString())
    var mSelectedText by remember { mutableStateOf(language) }
    val editor = sharedPreferences.edit()






    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }

    // Create a list of cities





    val languageMap: Map<String, String> = languages.associate { it.languageName to it.languageCode }
//    println(languageMap["English"])


    // Create a string value to store the selected city
//    var mSelectedText by remember { mutableStateOf("ENGLISH") }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        Modifier
            .padding(20.dp)
    ) {

        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            readOnly = true,
            value = mSelectedText.toString(),
            onValueChange = {
                mSelectedText = it
                editor.putString(LANGUAGE, it)
                editor.apply()
//                TRY HERE TO OPEN DROP DOWN TODO()
                            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }


            ,
            label = {Text("Language", modifier = Modifier
                .clickable {
                    mExpanded = !mExpanded
                })},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier
                        .size(35.dp)
                        .clickable {
                            mExpanded = !mExpanded
                        }
                )
            }
        )

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                .clickable {
                    mExpanded = true
                }
        ) {
            languages.forEach { label ->
                DropdownMenuItem(onClick = {

                    editor.putString(LANGUAGE, label.languageName)
                    editor.apply()
                    Log.i("qwertyuiop", label.languageName)
                    Log.i("qwertyuiop", "label.languageName")

                    mSelectedText = label.languageName
                    mExpanded = false
                }) {
                    Text(
                        text = label.languageName,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

fun translateText(sourceText: String, targetLanguageCode: String, isLoading: MutableState<Boolean>): String {
    val API_KEY = "YOUR_GOOGLE_CLOUD_API_KEY_INPUT_STREAM"
    isLoading.value = true
    // Replace 'YOUR_GOOGLE_CLOUD_API_KEY' with your actual API key or use another authentication method.
    val credentials = GoogleCredentials.fromStream(API_KEY.toByteArray().inputStream())
    val translate = TranslateOptions.newBuilder().setCredentials(credentials).build().service
    val translation: Translation = translate.translate(
        sourceText,
        Translate.TranslateOption.targetLanguage(targetLanguageCode)
    )
    isLoading.value = false
    return translation.translatedText
}

//fun translateText(sourceText: String, targetLanguageCode: String, isLoading: MutableState<Boolean>): String {
//    isLoading.value = true
//    delayWithoutSuspend(5000) {
//        println("Delayed action executed after 2 seconds.")
//    }
//    return if (targetLanguageCode == "en") {
//        isLoading.value = false
//        "meow"
//    } else {
//        isLoading.value = false
//        "लिखित शब्द या पाठ"
//    }
//}
//
//
//fun delayWithoutSuspend(timeMillis: Long, action: () -> Unit) {
//    Handler().postDelayed(action, timeMillis)
//}
