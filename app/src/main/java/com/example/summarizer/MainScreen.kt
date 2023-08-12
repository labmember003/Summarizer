@file:OptIn(ExperimentalMaterialApi::class)

package com.example.summarizer

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.summarizer.Utils.LANGUAGE
import com.example.summarizer.retrofit.ApiUtilities
import com.example.summarizer.retrofit.ChatRequest
import com.falcon.summarizer.R
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

@Composable
fun MainScreen(
    onTokenCountClick: () -> Unit,
    handleProfileButtonClickInNavigationDrawer: () -> Unit,
    handleSettingsButtonClickInNavigationDrawer: () -> Unit,
    handleNavigationFromMainScreenToSummarizedPage: (String) -> Unit
) {
    ModalDrawerSample(onTokenCountClick, handleProfileButtonClickInNavigationDrawer, handleSettingsButtonClickInNavigationDrawer, handleNavigationFromMainScreenToSummarizedPage)

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreenPage(
    onTokenCountClick: () -> Unit,
    drawerState: DrawerState,
    handleNavigtionFromMainScreenToSummarizedPage: (String) -> Unit
) {
    val tokenManager = TokenManager()
    val context = LocalContext.current
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val recognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    var isLoading by remember { mutableStateOf(false) }
    var responseText by remember { mutableStateOf("") }
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val language = sharedPreferences.getString(LANGUAGE, "English")
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val text = extractTextFromPdf(uri, context)
            Log.i("hapyhapyhapy", "text")
            Log.i("hapyhapyhapyhapyhapyhapyhapyhapyhapy", text)
            println("hapyhapyhapyhapyhapyhapyhapyhapyhapy$text cat")
            Log.i("hapyhapyhapyhapyhapyhapyhapyhapyhapy", text.length.toString())
            if (text.length < 3) {
                Log.i("hapyhapyhapyhapyhapyhapyhapyhapyhapy2", text)
                println("hapyhapyhapyhapyhapyhapyhapyhapyhapy$text cat")
            }
//            val summarizedText = getResponseFromApi(text)
//            handleNavigtionFromMainScreenToSummarizedPage(summarizedText) //  Navigate to summarized page with summarizedText
            isLoading = true

            getResponseFromApi(
                prompt = text,
                onSuccess = { response ->
                    Log.i("hapyhapyhapy - 1", response)
                    isLoading = false
//                    responseText = response
                    handleNavigtionFromMainScreenToSummarizedPage(response)
                    // Navigate to the summarized page here if needed
                    // You can use the responseText variable to pass the result
                },
                onError = { error ->
                    isLoading = false
                    Log.i("hapyhapyhapy - 2", error)
                    handleNavigtionFromMainScreenToSummarizedPage(error)
                    // Handle the error here if needed
                },
                language,
                onTokenCountClick,
                context
            )
        }
    }
    var isLoading2 by remember { mutableStateOf(false) }
//    var extractedTextOut by remember { mutableStateOf("") }
    val launcher2 = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            isLoading2 = true
            val bitmap: Bitmap = uriToBitmap(context, it)
//            val text = getTextFromImage(bitmap, recognizer, context, )
            getTextFromImage(bitmap, recognizer, context) { extractedText ->
//                extractedTextOut = extractedText
                Log.i("hapyhapyhapy - text - in", extractedText)
                Log.i("hapyhapyhapy - text", extractedText)
                getResponseFromApi(
                    prompt = extractedText,
                    onSuccess = { response ->
                        Log.i("hapyhapyhapy - 1", response)
                        isLoading = false
//                    responseText2 = response
                        handleNavigtionFromMainScreenToSummarizedPage(response)
                        // Navigate to the summarized page here if needed
                        // You can use the responseText variable to pass the result
                    },
                    onError = { error ->
                        isLoading = false
                        Log.i("hapyhapyhapy - 2", error)
                        handleNavigtionFromMainScreenToSummarizedPage(error)
                        // Handle the error here if needed
                    },
                    language = language,
                    onTokenCountClick = onTokenCountClick,
                    context = context
                )
            }
//            val summarizedText = getResponseFromApi(text)
//            handleNavigtionFromMainScreenToSummarizedPage(summarizedText) //  Navigate to summarized page with summarizedText

        }
    }
    val imageBitmapState = remember { mutableStateOf<Bitmap?>(null) }
    var isLoading3 by remember { mutableStateOf(false) }
    val launcher3 = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        isLoading3 = true
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data")?.let { it as android.graphics.Bitmap }
            imageBitmapState.value = imageBitmap
            if (imageBitmap != null) {
                getTextFromImage(imageBitmap, recognizer, context) { extractedText ->
                    Log.i("hapyhapyhapy - text - in", extractedText)
                    Log.i("hapyhapyhapy - text", extractedText)
                    getResponseFromApi(
                        prompt = extractedText,
                        onSuccess = { response ->
                            Log.i("hapyhapyhapy - 1", response)
                            isLoading3 = false
                            handleNavigtionFromMainScreenToSummarizedPage(response)
                        },
                        onError = { error ->
                            isLoading3 = false
                            Log.i("hapyhapyhapy - 2", error)
                            handleNavigtionFromMainScreenToSummarizedPage(error)
                        },
                        language = language,
                        onTokenCountClick = onTokenCountClick,
                        context = context
                    )
                }
            }
        }
    }
    if (isLoading || isLoading2 || isLoading3) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_cats))
            com.airbnb.lottie.compose.LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxSize()
                    .size(400.dp)
            )
        }
    } else if (!isLoading || !isLoading2 || !isLoading3){
        ModalBottomSheetLayout(
            sheetState = modalSheetState,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetContent = {
                BottomSheet(
                    modalSheetState,
                    onPdfOptionClicked = {
                        if (isNetworkAvailable(context)) {
                            launcher.launch("application/pdf")
                        } else {
                            Toast.makeText(context, "Please Check Your Internet Connection And Try Again", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onImageOptionClicked = {
                        if (isNetworkAvailable(context)) {
                            launcher2.launch("image/*")
                        }
                        else {
                            Toast.makeText(context, "Please Check Your Internet Connection And Try Again", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    if (isNetworkAvailable(context)) {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        launcher3.launch(takePictureIntent)
                    }
                    else {
                        Toast.makeText(context, "Please Check Your Internet Connection And Try Again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) {
            MainScreenContent(
                modalSheetState = modalSheetState,
                tokenManager = tokenManager,
                onTokenCountClick = onTokenCountClick,
                drawerState = drawerState,
                handleNavigtionFromMainScreenToSummarizedPage = handleNavigtionFromMainScreenToSummarizedPage
            )
        }
    }
}
@Composable
fun ModalDrawerSample(
    onTokenCountClick: () -> Unit,
    handleProfileButtonClickInNavigationDrawer: () -> Unit,
    handleSettingsButtonClickInNavigationDrawer: () -> Unit,
    handleNavigtionFromMainScreenToSummarizedPage: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
//            Column {
//                Text("Text in Drawer")
//                Button(onClick = {
//                    scope.launch {
//                        drawerState.close()
//                    }
//                }) {
//                    Text("Close Drawer")
//                }
//            }
            DrawerContent(handleProfileButtonClickInNavigationDrawer, handleSettingsButtonClickInNavigationDrawer)

        },
        content = {
            MainScreenPage(onTokenCountClick, drawerState, handleNavigtionFromMainScreenToSummarizedPage)
        }
    )
}
@Composable
fun DrawerContent(
    handleProfileButtonClickInNavigationDrawer: () -> Unit,
    handleSettingsButtonClickInNavigationDrawer: () -> Unit
) {
//    val navController = rememberNavController()
//    NavHost(navController = navController, )

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),

    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.productivity))
        com.airbnb.lottie.compose.LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .size(300.dp)
        )
        NavDrawerContent("Profile", R.drawable.baseline_person_24) {
            handleProfileButtonClickInNavigationDrawer()
        }
        Spacer(modifier = Modifier.height(2.dp))
        NavDrawerContent("Settings", R.drawable.baseline_settings_24) {
            handleSettingsButtonClickInNavigationDrawer()
        }
    }
}

@Composable
fun NavDrawerContent(contentName: String, imageID: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = imageID),
            contentDescription = "" ,
            modifier = Modifier
                .size(30.dp)
        )
        Spacer(
            modifier = Modifier
                .size(5.dp)
        )
        Text(
            text = contentName,
            style = MaterialTheme.typography.body1,
            color = Color.Unspecified
        )
    }
}

fun getResponseFromApi(
    prompt: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    language: String?,
    onTokenCountClick: () -> Unit,
    context: Context
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val apiKey = fetchDataFromUrl("https://get-api-key-api-avishishtgupta-gmailcom.vercel.app/key")
            withContext(Dispatchers.Main) {
                gptResponse(language, prompt, apiKey, onSuccess, onError, context, onTokenCountClick)
                Log.i("API_KEY", apiKey)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}

private fun gptResponse(
    language: String?,
    prompt: String,
    API_KEY: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    context: Context,
    onTokenCountClick: () -> Unit
) {
    val requestBody = RequestBody.create(
        "application/json".toMediaType(),
        Gson().toJson(
            ChatRequest(
                1000,
                "text-davinci-003",
                prompt = "Summarise this text in $language: \n$prompt",
                0.7
            )
        )
    )
    Log.i("qwsdrfghjiklp", API_KEY)
    val contentType = "application/json"
    val authorization = "Bearer ${API_KEY}"

    // Coroutine scope for making the HTTP request
    val tokenManager = TokenManager()
    tokenManager.getToken { tokenCount ->
        val zero = 0
        if (tokenCount!! > zero.toLong()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = ApiUtilities.getApiInterface().getChat(
                        contentType, authorization, requestBody
                    )
                    val textResponse = response.choices.first().text
                    withContext(Dispatchers.Main) {
                        val tokenUsed = countWords(prompt) + countWords(textResponse)
                        tokenManager.getToken { tokenCount ->
                            if (tokenCount != null) {
                                tokenManager.updateToken(tokenCount - tokenUsed.toLong())
                            } else {
                                Log.i(Utils.ERROR_TAG, "Account Not Signed")
                            }
                        }
                        onSuccess(textResponse)
                    }
                } catch (e: Exception) {
                    val errorResponse = e.message.toString()
                    Log.i("hapyhapyhapy", errorResponse)
                    withContext(Dispatchers.Main) {
                        onError(errorResponse)
                    }
                }
            }
        } else {
            inSufficientTokens(context) { onTokenCountClick() }
        }
    }
}

fun inSufficientTokens(context: Context, onTokenCountClick: () -> Unit) {
    Toast.makeText(context, "Tokens running low! Get more to keep going. Cheers!", Toast.LENGTH_LONG).show()
    onTokenCountClick()
}

suspend fun fetchDataFromUrl(url: String): String {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    val response: Response = client.newCall(request).execute()
    return response.body?.string() ?: "No response"
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainScreenContent(
    modalSheetState: ModalBottomSheetState,
    tokenManager: TokenManager,
    onTokenCountClick: () -> Unit,
    drawerState: DrawerState,
    handleNavigtionFromMainScreenToSummarizedPage: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var responseText by remember { mutableStateOf("") }
    if (isLoading) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_cats))
            com.airbnb.lottie.compose.LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxSize()
                    .size(400.dp)
            )
        }
    } else if (!isLoading){
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
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                    }
                    Text(
                        text = "Upload Image Or E-Book",
                        modifier = Modifier
                    )
                    Tokens(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorResource(id = R.color.light_grey))
                            .padding(8.dp),
                        tokenManager = tokenManager,
                        onTokenCountClick = onTokenCountClick
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
            var isVisible by remember { mutableStateOf(false) }
            val context = LocalContext.current
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            ) {
                OutlinedTextField(
                    value = content.value,
                    onValueChange = {
                        if (it.length >= 6) {
                            isVisible = true
                        } else if (it.length < 6) {
                            isVisible = false
                        }
                        content.value = it
                    },
                    label = { androidx.compose.material.Text("Input Text Here To Summarize") },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
                val sharedPreferences = remember {
                    context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                }
                val language = sharedPreferences.getString(LANGUAGE, "English")
                if (isVisible) {
                    FloatingActionButton(
                        onClick = {
                            if (isNetworkAvailable(context)) {
                                isLoading = true
                                getResponseFromApi(
                                    prompt = content.value,
                                    onSuccess = { response ->
                                        Log.i("hapyhapyhapy", response)
                                        isLoading = false
                                        responseText = response
                                        handleNavigtionFromMainScreenToSummarizedPage(responseText)
                                    },
                                    onError = { error ->
                                        isLoading = false
                                        Log.i("hapyhapyhapy", error)
                                    },
                                    language = language,
                                    onTokenCountClick = onTokenCountClick,
                                    context = context
                                )
                            } else {
                                Toast.makeText(context, "Please Check Your Internet Connection And Try Again", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                            .size(56.dp),
                        shape = RoundedCornerShape(percent = 30),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NavigateNext,
                            contentDescription = "Go",
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                }

            }
        }
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
fun Tokens(modifier: Modifier, tokenManager: TokenManager, onTokenCountClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                onTokenCountClick()
            }
    ) {
        Image(
            painter = painterResource(R.drawable.token),
            contentDescription = "Your Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
        )
//        val tokenCount = remember { mutableStateOf(tokenManager.getTokenCount()) }
//        val tokenCount2 = tokenCount.value
//        if (tokenCount2 != null) {
//            println("Token count: $tokenCount2")
//            Log.i("kwfkbwekef", tokenCount2.toString())
//        } else {
//            // Handle the case where the token count is not available
//            //         Call putToken() to store the token count
////            val initialTokenCount = 100
////            tokenManager.putToken(initialTokenCount.toLong())
//
//
//
//            tokenManager.getToken { tokenCount ->
//                if (tokenCount != null) {
//                    // Use the tokenCount value here
//                    println("Token count: $tokenCount")
//                    Log.i("kwfkbwekef", tokenCount.toString())
//                } else {
//                    // Handle the case where the token count is not available
//                    //         Call putToken() to store the token count
////                    val initialTokenCount = 100
////                    tokenManager.putToken(initialTokenCount.toLong())
////                    println("New Registration, Granted 100 Token")
//                }
//            }
//
//
//
//
//            println("New Registration, Granted 100 Token")
//            println("billi mausi" + tokenManager.getTokenCount().toString() + "billi mausi")
//        }
        val minusOne : Long = -1
        val tokenCountOut = remember { mutableStateOf(minusOne) }
        tokenManager.getToken { tokenCount ->
            if (tokenCount != null) {
                // Use the tokenCount value here
                tokenCountOut.value = tokenCount
                println("Token count meow: $tokenCount")
                Log.i("kwfkbwekef", tokenCount.toString())
                println("Token count meow: $tokenCount")
            } else {
                // Handle the case where the token count is not available
                //         Call putToken() to store the token count
//                    val initialTokenCount = 100
//                    tokenManager.putToken(initialTokenCount.toLong())
//                    println("New Registration, Granted 100 Token")
            }
        }

        Text(
            text = tokenCountOut.value.toString(),
        )
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    state: ModalBottomSheetState,
    onPdfOptionClicked: () -> Unit,
    onImageOptionClicked: () -> Unit,
    onCameraOptionClicked: () -> Unit
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
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        onCameraOptionClicked()
                        scope.launch { state.hide() }
                    }
            )
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

//private fun getTextFromImage(bitmap: Bitmap, recognizer: TextRecognizer, context: Context): String {
//    var extractedText = ""
//    val image = bitmap.let {
//        InputImage.fromBitmap(it, 0)
//    }
//    image.let {
//        recognizer.process(it)
//            .addOnSuccessListener { visionText ->
//                Log.i("kaaali billi", visionText.text)
//                extractedText = visionText.text
//                Toast.makeText(context, visionText.text, Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                extractedText = "Error - $e"
//            }
//    }
//    return extractedText
//}

private fun getTextFromImage(
    bitmap: Bitmap,
    recognizer: TextRecognizer,
    context: Context,
    callback: (String) -> Unit
) {
    val image = InputImage.fromBitmap(bitmap, 0)
    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val extractedText = visionText.text
            Log.i("kaaali billi", extractedText)
            callback(extractedText) // Pass the result to the callback function
        }
        .addOnFailureListener { e ->
            val errorText = "Error - $e"
            Log.e("kaaali billi", errorText)
            callback(errorText) // Pass the error to the callback function
        }
}

fun countWords(text: String): Int {
    val words = text.trim().split("\\s+".toRegex())
    return words.size
}