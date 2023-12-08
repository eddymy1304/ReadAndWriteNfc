package com.example.readandwritenfc.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readandwritenfc.ui.theme.ReadAndWriteNfcTheme

@Preview(showBackground = true)
@Composable
fun PreviewButtonNfc() {
    ReadAndWriteNfcTheme {
        ButtonNfc(text = "NFC", modifier = Modifier)
    }
}

@Composable
fun ButtonNfc(text: String, modifier: Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = { onClick() }) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}