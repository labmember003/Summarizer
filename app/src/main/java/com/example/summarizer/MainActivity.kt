package com.example.summarizer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
import com.example.summarizer.DataManager.currentPage
import com.example.summarizer.presentation.sign_in.GoogleAuthUiClient
import com.falcon.summarizer.R
import com.google.android.gms.auth.api.identity.Identity

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
//    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var billingClient: BillingClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            Surface(
//                modifier = Modifier.fillMaxSize(),
//                color = MaterialTheme.colors.background
//            ) {
//                val navController = rememberNavController()
//                NavHost(navController = navController, startDestination = "sign_in") {
//
//                    composable("sign_in") {
//                        val viewModel = viewModel<SignInViewModel>()
//                        val state by viewModel.state.collectAsStateWithLifecycle()
//
//                        LaunchedEffect(key1 = Unit) {
//                            if(googleAuthUiClient.getSignedInUser() != null) {
//                                navController.navigate("profile")
//                            }
//                        }
//
//                        val launcher = rememberLauncherForActivityResult(
//                            contract = ActivityResultContracts.StartIntentSenderForResult(),
//                            onResult = { result ->
//                                if(result.resultCode == RESULT_OK) {
//                                    lifecycleScope.launch {
//                                        val signInResult = googleAuthUiClient.signInWithIntent(
//                                            intent = result.data ?: return@launch
//                                        )
//                                        viewModel.onSignInResult(signInResult)
//                                    }
//                                }
//                            }
//                        )
//
//                        LaunchedEffect(key1 = state.isSignInSuccessful) {
//                            if(state.isSignInSuccessful) {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Sign in successful",
//                                    Toast.LENGTH_LONG
//                                ).show()
//
//                                navController.navigate("profile")
//                                viewModel.resetState()
//                            }
//                        }
//
//                        SignInScreen(
//                            state = state,
//                            onSignInClick = {
//                                lifecycleScope.launch {
//                                    val signInIntentSender = googleAuthUiClient.signIn()
//                                    launcher.launch(
//                                        IntentSenderRequest.Builder(
//                                            signInIntentSender ?: return@launch
//                                        ).build()
//                                    )
//                                }
//                            }
//                        )
//                    }
//                    composable("profile") {
//                        ProfileScreen(
//                            userData = googleAuthUiClient.getSignedInUser(),
//                            onSignOut = {
//                                lifecycleScope.launch {
//                                    googleAuthUiClient.signOut()
//                                    Toast.makeText(
//                                        applicationContext,
//                                        "Signed out",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//
//                                    navController.popBackStack()
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//        }





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

//        setContent {
//            Test()
//        }
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
                PAGES.BUYTOKEN -> {
                    BuyTokenScreen { context, id ->
                        purchaseProduct(context, id)
                    }
                }
            }
        }
    }

    @Composable
    fun LoginWithGoogle() {
            // A surface container using the 'background' color from the theme


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
        } else {
            // Handle other billing errors
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
                "100_coins_id" -> grantTokens(100)
                "250_coins_id" -> grantTokens(250)
                "500_coins_id" -> grantTokens(500)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            // Purchase was canceled by the user
            // Display a message to the user indicating the cancellation
            Log.i("TokenToken", "Purchase canceled by the user")
        }
    }

    private fun grantTokens(tokens: Int) {
//        Log.i("meowmeowmeowmeowhappy", "Account Not Signed")
        val tokenManager = TokenManager()
        val tokenCount: Long = tokenManager.getTokenCount() ?: -1
        if (tokenCount.toInt() == -1) {
            Log.i("meowmeowmeowmeow", "Account Not Signed")
        } else {
            tokenManager.updateToken(tokenCount + tokens.toLong())
        }
//        Log.i("TokenToken", "tokens.toString()")
//        Log.i("TokenToken", tokens.toString())
//        val editor = sharedPreferences.edit()
//        editor.putString(USER_TOKEN, tokens.toString())
//        editor.apply()
    }
    @Composable
    fun Test() {
        val context = LocalContext.current
//        sharedPreferences = remember {
//            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
//        }

        Row {
            Button(onClick = {
                purchaseProduct(context, "100_coins_id")
            }) {
                Text(text = "button1")
            }
            Button(onClick = {
                purchaseProduct(context, "250_coins_id")
            }) {
                Text(text = "button2")
            }
            Button(onClick = {
                purchaseProduct(context, "500_coins_id")
            }) {
                Text(text = "button3")
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