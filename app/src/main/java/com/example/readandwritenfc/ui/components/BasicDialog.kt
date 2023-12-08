package com.example.readandwritenfc.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@Composable
fun BasicDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    icon: ImageVector,
    textConfirm: String,
    textDismiss: String = "",
    isMessageDialog: Boolean = false,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit
) {
    AlertDialog(modifier = modifier,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        title = { Text(text = title) },
        text = { Text(text = message) },
        dismissButton = {
            if (!isMessageDialog) {
                TextButton(onClick = { onDismiss() }) {
                    Text(
                        text = textDismiss,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(
                    text = textConfirm,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        })
}