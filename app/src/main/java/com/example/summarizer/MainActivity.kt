package com.example.summarizer

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.example.summarizer.Utils.ERROR_TAG
import com.example.summarizer.Utils.INITIAL_LAUCH
import com.example.summarizer.presentation.profile.ProfileScreen
import com.example.summarizer.presentation.sign_in.GoogleAuthUiClient
import com.example.summarizer.presentation.sign_in.SignInScreen
import com.example.summarizer.presentation.sign_in.SignInViewModel
import com.example.summarizer.settings.SettingsScreen
import com.example.summarizer.ui.PreferredLanguageScreen
import com.falcon.summarizer.R
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import java.util.Base64

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var billingClient: BillingClient
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                val navController = rememberNavController()
                val context = LocalContext.current
                NavHost(navController = navController, startDestination = "walk_through_screen") {

                    composable("walk_through_screen") {
                        BackHandler(
                            onBack = {
                                finish()
                            }
                        )
                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate("main_screen")
                            }
                        }
                        WalkThroughScreen {
                            navController.navigate("sign_in")
                        }
                    }
                    composable("sign_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate("main_screen")
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if(result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if(state.isSignInSuccessful) {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Sign in successful",
//                                    Toast.LENGTH_LONG
//                                ).show()

                                navController.navigate("prefered_language_screen")
                                viewModel.resetState()
                            }
                        }
                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                if (!isNetworkAvailable(context)) {
                                    Toast.makeText(context, "Please Check Your Internet Connection And Try Again", Toast.LENGTH_SHORT).show()
                                } else {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            }
                        )
                    }
                    val languages = listOf("English", "Español", "Français", "Deutsch",
                        "简体中文", "日本語", "العربية", "हिन्दी", "русский", "한국어", "Italiano", "Português")
                    composable("prefered_language_screen") {
                        val sharedPreferences = remember {
                            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                        }
                        if (sharedPreferences.getBoolean(INITIAL_LAUCH, true)) {
                            PreferredLanguageScreen(languages = languages, navController = navController)
                        } else {
                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("main_screen")
                                }
                            }
                        }
                    }
                    composable("main_screen") {
                        MainScreen (
                            {
                                navController.navigate("buy_token_screen")
                        }, {
                                navController.navigate("profile")
                            }
                        , {
                            navController.navigate("settings")
                        }) { text ->
                            navController.navigate("summarized_page/${base64Encode(text)}")
                        }
                    }
                    //        val languages = listOf(
                    //            Language("English", "en"),
                    //            Language("Español", "es"), // Spanish
                    //            Language("Français", "fr"), // French
                    //            Language("Deutsch", "de"), // German
                    //            Language("简体中文", "zh"), // Chinese (Simplified)
                    //            Language("日本語", "ja"), // Japanese
                    //            Language("العربية", "ar"), // Arabic
                    //            Language("हिन्दी", "hi"), // Hindi
                    //            Language("русский", "ru"), // Russian
                    //            Language("한국어", "ko"), // Korean
                    //            Language("Italiano", "it"), // Italian
                    //            Language("Português", "pt") // Portuguese

                    composable("settings") {
                        SettingsScreen (languages){
                            navController.popBackStack()
                        }
                    }
                    composable("buy_token_screen") {
                        BuyTokenScreen(onCLick = { context, id ->
                            purchaseProduct(context, id)
                        }, onDismiss = {
                            navController.popBackStack()
                        })
                    }

                    composable("profile") {
                        val sharedPreferences = remember {
                            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                        }
                        ProfileScreen(
                            userData = googleAuthUiClient.getSignedInUser(),
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()
//                                    Toast.makeText(
//                                        applicationContext,
//                                        "Signed out",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                    navController.popBackStack()
//                                    navController.popBackStack()
//                                    navController.popBackStack()
//                                    navController.popBackStack()
                                    navController.navigate("walk_through_screen")
                                }
                                val editor = sharedPreferences.edit()
                                editor.clear()
                                editor.apply()
                            }
                        )
                    }
                    composable(
                        "summarized_page" + "/{summarizedText}",
                        arguments = listOf(
                            navArgument("summarizedText") {
                                 type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {  entry ->
                        val decodedString = base64Decode(entry.arguments?.getString("summarizedText"))
                        SummarizedPage(summarizedText = decodedString)
                    }
                }
            }
        }

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready
                }
            }

            override fun onBillingServiceDisconnected() {
                // Handle billing service disconnection
            }
        })
    }

    private fun purchaseProduct(context: Context, productId: String) {
        val skuDetailsParams = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(productId))
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                if (skuDetailsList.isNotEmpty()) {
                    val skuDetails = skuDetailsList[0]
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build()

                    billingClient.launchBillingFlow(context as Activity, billingFlowParams)
                } else {
                    // Handle the case when SKU details are empty
                }
            } else {
                // Handle the case when the query fails
            }
        }
    }

    private val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode ==BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle user cancellation
            Log.i(ERROR_TAG, "Purchase canceled by the user")
        } else {
            // Handle other billing errors
            Log.i(ERROR_TAG, "Purchase canceled by the user")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Verify the purchase and grant the tokens to the user
        // You can implement server-side verification here for added security
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Purchase is valid
            val sku = purchase.skus[0] // SKU of the purchased product
            // Grant tokens based on the SKU
            when (sku) {
                "100_coins_id" -> {
                    Log.i("rj3r3r", "100_coins_id")
                    grantTokens(100)
                }
                "250_coins_id" -> grantTokens(250)
                "500_coins_id" -> grantTokens(500)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            // Purchase was canceled by the user
            // Display a message to the user indicating the cancellation
            Log.i(ERROR_TAG, "Purchase canceled by the user")
        }
    }

    private fun grantTokens(tokens: Int) {
        val tokenManager = TokenManager()
        tokenManager.getToken { tokenCount ->
            if (tokenCount != null) {
                // ADD PURCHASED TOKENS IN USER'S ACCOUNT
                tokenManager.updateToken(tokenCount + tokens.toLong())
            } else {
                Log.i(ERROR_TAG, "Account Not Signed")
            }
        }
    }
}

enum class PAGES {
    LOGIN, WALKTHROUGH, MAINPAGE, BUYTOKEN
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

@Composable
fun GoogleSignInCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(15.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(id = R.drawable.ic_goole),
                modifier = Modifier.size(20.dp),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Continue With Google",
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun GoogleSignInMainScreen(
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LottieAnimation(R.raw.login_animation)
        Spacer(modifier = Modifier.padding(32.dp))
        GoogleSignInCard(onClick = onClick)
    }
}
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
@RequiresApi(Build.VERSION_CODES.O)
fun base64Encode(input: String): String {
    val bytes = input.toByteArray(Charsets.UTF_8)
    val encodedBytes = Base64.getEncoder().encode(bytes)
    return String(encodedBytes, Charsets.UTF_8)
}

@RequiresApi(Build.VERSION_CODES.O)
fun base64Decode(input: String?): String {
    val decodedBytes = Base64.getDecoder().decode(input)
    return String(decodedBytes, Charsets.UTF_8)
}
