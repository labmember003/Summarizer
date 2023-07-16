package com.example.summarizer

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.summarizer.Utils.FONT_SIZE
import com.falcon.summarizer.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun SummarizedPage() {
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            BottomSheetFontSize(modalSheetState)
        }
    ) {
        SummarizedPageContent(modalSheetState)
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetFontSize(modalSheetState: ModalBottomSheetState) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp, 24.dp, 16.dp, 16.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.material3.Text(
                text = "Font Size",
                style = MaterialTheme.typography.subtitle1,
                fontSize = 20.sp
            )
            androidx.compose.material.Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .clickable {
                        scope.launch { modalSheetState.hide() }
                    }
            )
        }
        val sharedPreferences = remember {
            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
        }
        val fontSize = sharedPreferences.getInt(FONT_SIZE, 16)
        var selectedFontSize by remember { mutableStateOf(fontSize) }
        val editor = sharedPreferences.edit()
        FontSizeSection(selectedFontSize) { fontSize ->
            selectedFontSize = fontSize
            editor.putInt(FONT_SIZE, fontSize)
            editor.apply()
            Log.i("meoemoeoeoeoeoeoe", fontSize.toString())
            Log.i("meoemoeoeoeoeoeoe", sharedPreferences.getInt(FONT_SIZE, 16).toString())
        }
    }
}

@Composable
fun FontSizeSection(selectedFontSize: Int, onFontSizeSelected: (Int) -> Unit) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            (12..20).forEach { fontSize ->
                val isSelected = fontSize == selectedFontSize
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                        .clickable { onFontSizeSelected(fontSize) }
                        .background(
                            color = if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fontSize.toString(),
                        style = TextStyle(fontSize = fontSize.sp, color = if (isSelected) Color.White else Color.Black)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SummarizedPageContent(modalSheetState: ModalBottomSheetState) {
    Column() {
        HeadingSummarizedPage(modalSheetState)

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HeadingSummarizedPage(modalSheetState: ModalBottomSheetState) {
    val scope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Summarizer",
            style = MaterialTheme.typography.subtitle1,
            fontSize = 20.sp
        )
        IconButton(onClick = {
            scope.launch { modalSheetState.show() }
        }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options"
            )
        }
    }

}
