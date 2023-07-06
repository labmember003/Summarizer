package com.example.summarizer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.falcon.summarizer.R

@Composable
fun LoginScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
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
            val screen = DataManager.screen

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val loginButtonColor = remember{ mutableStateOf(Color.Blue) }
                    if (screen.value == SCREEN.LOGIN) {
                        loginButtonColor.value = Color.Blue
                    } else {
                        loginButtonColor.value = Color.Black
                    }
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.h5,
                        color = loginButtonColor.value,
                        modifier = Modifier.clickable {
                            screen.value = SCREEN.LOGIN
                        }
                    )
                    val signUpButtonColor = remember{ mutableStateOf(Color.Black) }
                    if (screen.value == SCREEN.SIGNUP) {
                        signUpButtonColor.value = Color.Blue
                    } else {
                        signUpButtonColor.value = Color.Black
                    }
                    Text(text = "SignUp",
                        style = MaterialTheme.typography.h5,
                        color = signUpButtonColor.value,
                        modifier = Modifier.clickable{
                            screen.value = SCREEN.SIGNUP
                        }
                    )
                }
                if (screen.value == SCREEN.LOGIN) {
                    Login()
                } else {
                    SignUp()
                }

            }
        }
    }
}

@Composable
fun Login() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoginImage()
        EditText("Username")
        EditText("Password", PasswordVisualTransformation())
        LoginButton("Login", PAGES.MAINPAGE)
    }
}
@Preview
@Composable
fun SignUp() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoginImage()
        EditText("Username")
        EditText("Email")
        EditText("Password", PasswordVisualTransformation())
        LoginButton("SignUp", PAGES.MAINPAGE)
        // TODO PASS ROLE
        // yaha ek aisa ROLL class ka object pass krna jo sirf DropDown ne set kiya ho
    }
}

@Composable
fun LoginImage() {
    Image(
        painter = painterResource(id = R.drawable.login_img),
        contentDescription = "",
        modifier = Modifier
            .padding(0.dp, 30.dp, 0.dp, 0.dp)
            .scale(1.5f)
//            .size(150.dp)
    )
}


@Composable
fun LoginButton(text: String, role: PAGES = PAGES.LOGIN) {
    Button(onClick = {
        DataManager.currentPage.value = role
    }) {
        Text(text = text)
    }
}