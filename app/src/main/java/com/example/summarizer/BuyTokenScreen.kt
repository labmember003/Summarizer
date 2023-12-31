package com.example.summarizer

import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.summarizer.R

val FontName = FontFamily(
    Font(R.font.nunito, FontWeight.Normal),
    Font(R.font.nunito,FontWeight.SemiBold),
    Font(R.font.nunito, FontWeight.Bold),
    Font(R.font.nunito, FontWeight.ExtraBold)
)
@Composable
fun BuyTokenScreen(
    onCLick: (Context, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "login_background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )
        Card(
            elevation = 4.dp,
            border = BorderStroke(1.dp, colorResource(id = R.color.purple_blue)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Heading(onDismiss)
                Features()
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )
                BuyToken(3500, 50) {
                    onCLick(context, "3500_coins_for_50")
                }
                BuyToken(8000, 100) {
                    onCLick(context, "8000_coins_for_100")
                }
                BuyToken(20000, 200) {
                    onCLick(context, "20000_coins_for_200")
                }
                BuyToken(50000, 500) {
                    onCLick(context, "50000_coins_for_500")
                }
            }
        }
    }



}

@Preview(showBackground = true)
@Composable
fun ColumnScope.Features() {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
//            .weight(1f)
    ) {
        Text(
            text = "Unlock Unlimited Access",
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = FontName,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
//                .padding(0.dp, 16.dp, 0.dp, 56.dp)
        )
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
        Feature(Icons.Filled.SmartToy, "Powered By GPT - 4", "Latest ChatGPT AI Model")
        Feature(Icons.Filled.Description, "Higher Word Limits", "Type Longer Messages")
        Feature(Icons.Filled.NotInterested, "No Ads", "Enjoy Summarizer without ads")
    }
}

@Composable
fun Feature(imageId: ImageVector, text: String, extraText: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(8.dp)
    ) {
//        Image(
//            painter = painterResource(id = imageId),
//            contentDescription = text,
//            contentScale = ContentScale.FillBounds,
//            modifier = Modifier
//                .size(32.dp) // Adjust the size according to your needs
//                .padding(end = 12.dp)
//        )
//        Column(
//            modifier = Modifier
//                .padding(4.dp, 0.dp)
//        ) {
//            Text(
//                text = text,
//                style = TextStyle(
//                    fontFamily = FontName,
//                    fontWeight = FontWeight.Normal
//                ),
//                fontSize = 24.sp
//            )
//            Text(
//                text = extraText,
//                fontSize = 16.sp,
//                style = TextStyle(
//                    fontFamily = FontName,
//                    fontWeight = FontWeight.Normal
//                )
//            )
//        }
//        val accountCircleIcon: ImageVector = Icons.Filled.SmartToy
//        val accountCircleIcon: ImageVector = Icons.Filled.Description
//        val accountCircleIcon: ImageVector = Icons.Filled.NotInterested
        Icon(
            imageVector = imageId,
            contentDescription = "Account",
            modifier = Modifier
//                .size(32.dp) // Adjust the size according to your needs
                .padding(end = 12.dp)
        )
        Text(
            text = text,
//            style = TextStyle(
//                fontFamily = FontName,
//                fontWeight = FontWeight.Normal
//            ),
            fontSize = 19.sp
        )
    }
}

@Composable
private fun Heading(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(1f)
    ) {
        Text(
            text = "Buy Token",
            style = TextStyle(
                fontFamily = FontName,
                fontWeight = FontWeight.Bold
            ),
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.Center)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(Color.LightGray, shape = CircleShape)
                .clickable(onClick = {
                    onDismiss()
                }),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .clickable {
                        onDismiss()
                    }
            )
        }
    }
}

@Composable
fun ColumnScope.BuyToken(tokenCount: Int, priceInINR: Int, onCLick: () -> Unit) {
    Card(elevation = 4.dp,
        border = BorderStroke(1.dp, colorResource(id = R.color.purple_blue)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .clickable {
                onCLick()
            }
    )
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = "$tokenCount Tokens",
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 18.sp
            )
            Text(
                text = "$priceInINR Rupees",
                style = TextStyle(
                    fontFamily = FontName,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
    
}
