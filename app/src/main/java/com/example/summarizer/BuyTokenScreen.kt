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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
            style = MaterialTheme.typography.body1.copy(
                fontSize = 24.sp
            ),
        )
        Feature(R.drawable.chatbot, "Powered By GPT - 4", "Latest ChatGPT AI Model")
        Feature(R.drawable.speedometerr, "Higher Word Limits", "Type Longer Messages")
        Feature(R.drawable.ad, "No Ads", "Enjoy Summarizer without ads")
    }
}

@Composable
fun Feature(imageId: Int, text: String, extraText: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(6.dp)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
        )
        Column(
            modifier = Modifier
                .padding(4.dp, 0.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1.copy(

                ),
                fontSize = 24.sp
            )
            Text(
                text = extraText,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun Heading() {
    Box(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(1f)
    ) {
        Text(
            text = "Buy Token",
            style = MaterialTheme.typography.body1.copy(
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

@Composable
fun Happy() {

}