package com.example.summarizer

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun BuyTokenScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Heading()
        Features()


    }

}

@Preview(showBackground = true)
@Composable
fun ColumnScope.Features() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
    ) {
        Text(
            text = "Unlock Unlimited Access",
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = FontName,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .padding(0.dp, 16.dp, 0.dp, 56.dp)
        )
        Feature(R.drawable.chatbot, "Powered By GPT - 4", "Latest ChatGPT AI Model")
        Feature(R.drawable.speedometer, "Higher Word Limits", "Type Longer Messages")
        Feature(R.drawable.ad, "No Ads", "Enjoy Summarizer without ads")
    }
}

@Composable
fun Feature(imageId: Int, text: String, extraText: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(45.dp) // Adjust the size according to your needs
                .padding(end = 12.dp)
        )
        Column(
            modifier = Modifier
                .padding(4.dp, 0.dp)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal
                ),
                fontSize = 24.sp
            )
            Text(
                text = extraText,
                fontSize = 16.sp,
                style = TextStyle(
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
private fun Heading() {
    Box(
        modifier = Modifier
            .padding(24.dp, 48.dp)
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
//                        TODO()
                }),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .clickable {
//                        onDismiss()
                    }
            )
        }
    }
}