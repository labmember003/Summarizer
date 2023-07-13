package com.example.summarizer.presentation.profile


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.summarizer.TokenManager
import com.example.summarizer.presentation.sign_in.UserData

@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(userData?.profilePicUrl != null) {
            AsyncImage(
                model = userData.profilePicUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(userData?.userName != null) {
            Text(
                text = userData.userName,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(onClick = onSignOut) {
            Text(text = "Sign out")
        }
        Text(text = userData?.userId.toString())


        val tokenManager = TokenManager()



//         Call updateToken() to update the token count
//        val tokensUsed = 20
//        tokenManager.updateToken(tokensUsed.toLong())

        // Call getToken() to retrieve the token count
//        tokenManager.getToken { tokenCount ->
//            if (tokenCount != null) {
//                // Use the tokenCount value here
//                println("Token count: $tokenCount")
//                Log.i("kwfkbwekef", tokenCount.toString())
//            } else {
//                // Handle the case where the token count is not available
//                //         Call putToken() to store the token count
//                val initialTokenCount = 100
//                tokenManager.putToken(initialTokenCount.toLong())
//                println("New Registration, Granted 100 Token")
//            }
//        }


        val tokenCount = tokenManager.getTokenCount()
        if (tokenCount != null) {
            println("Token count: $tokenCount")
            Log.i("kwfkbwekef", tokenCount.toString())
        } else {
            // Handle the case where the token count is not available
            //         Call putToken() to store the token count
            val initialTokenCount = 100
            tokenManager.putToken(initialTokenCount.toLong())
            println("New Registration, Granted 100 Token")
        }

    }
}