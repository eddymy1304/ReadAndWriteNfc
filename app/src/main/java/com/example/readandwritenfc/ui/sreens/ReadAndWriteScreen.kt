package com.example.readandwritenfc.ui.sreens

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.readandwritenfc.NfcViewModel
import com.example.readandwritenfc.ui.components.BasicDialog
import com.example.readandwritenfc.ui.components.ButtonNfc
import com.example.readandwritenfc.ui.components.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadAndWriteScreen(
    modifier: Modifier,
    context: Context,
    activity: ComponentActivity,
    nfcAdapter: NfcAdapter?,
    viewModel: NfcViewModel
) {
    val nfcNotNull = nfcAdapter != null
    val nfcEnabled = nfcAdapter?.isEnabled == true

    val isWrite by viewModel.isWriteNfc.observeAsState(initial = false)
    val textId by viewModel.textId.observeAsState(initial = "")
    val textName by viewModel.textName.observeAsState(initial = "")
    val showLoadingDialog by viewModel.showLoadingDialog.observeAsState(initial = false)

    if (!nfcNotNull) {
        BasicDialog(
            title = "Read and Write NFC",
            message = "El dispositivo no tiene NFC",
            icon = Icons.Outlined.Dangerous,
            textConfirm = "Aceptar"
        ) { activity.finish() }
    }

    if (nfcEnabled) {
        Column(modifier = modifier) {
            Row {
                Text(
                    text = "Lectura / Escritura",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(alignment = Alignment.CenterVertically)
                        .weight(0.8f),
                    textAlign = TextAlign.Start
                )
                Switch(checked = isWrite,
                    onCheckedChange = { viewModel.updateIsWriteNfc(it) },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(0.2f)
                        .align(alignment = Alignment.CenterVertically),
                    thumbContent = {
                        Icon(
                            imageVector = Icons.Outlined.Check, contentDescription = null
                        )
                    })
            }

            OutlinedTextField(
                value = textId,
                onValueChange = { viewModel.updateTextId(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = { Text(text = "Id") },
            )

            OutlinedTextField(
                value = textName,
                onValueChange = { viewModel.updateTextName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = { Text(text = "Nombres") },
            )

            ButtonNfc(text = if (isWrite) "Enrolar tarjeta NFC" else "Leer tarjeta NFC") {
                viewModel.updateShowLoadingDialog(true)
            }

            AnimatedVisibility(visible = showLoadingDialog) {
                LoadingDialog()
            }

        }
    } else {
        BasicDialog(title = "Read and Write NFC",
            message = "NFC no activado, puedes activarlo en Configuraciones",
            icon = Icons.Outlined.Warning,
            textDismiss = "Salir",
            textConfirm = "Ir a Configuraciones",
            onDismiss = { activity.finish() }) {
            activity.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            activity.finish()
        }
    }

}