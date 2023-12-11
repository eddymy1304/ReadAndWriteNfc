package com.example.readandwritenfc.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.readandwritenfc.ui.theme.ReadAndWriteNfcTheme

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    textLoading: String = "Cargando..."
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                        .height(48.dp)
                        .weight(0.25f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = textLoading,
                    modifier = Modifier
                        .weight(0.75f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingDialog() {
    ReadAndWriteNfcTheme {
        LoadingDialog()
    }

}