package com.example.summarizer

import androidx.compose.runtime.Composable

val walkThroughPagerContent = listOf(
    WalkThroughPager(R.raw.idea_loader, "Demo Title 1", "Demo description 1"),
    WalkThroughPager(R.raw.idea_loader, "Demo Title 2", "Demo description 2"),
    WalkThroughPager(R.raw.idea_loader, "Demo Title 3", "Demo description 3")
)

@Composable
fun WalkThroughScreen() {

}