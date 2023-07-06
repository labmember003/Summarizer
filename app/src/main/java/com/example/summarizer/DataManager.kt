package com.example.summarizer

import androidx.compose.runtime.mutableStateOf

object DataManager {

    val currentPage = mutableStateOf(PAGES.WALKTHROUGH)
    val screen = mutableStateOf(SCREEN.LOGIN)
    val currentRole = mutableStateOf(PAGES.LOGIN)
    val USER_TOKEN = "FALCON_TECH_LAB"
}