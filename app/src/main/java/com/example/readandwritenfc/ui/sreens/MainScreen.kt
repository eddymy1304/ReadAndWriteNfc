package com.example.readandwritenfc.ui.sreens

import android.content.Context
import android.nfc.NfcAdapter
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readandwritenfc.NfcViewModel
import com.example.readandwritenfc.ui.components.BasicTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    context: Context,
    activity: ComponentActivity,
    nfcAdapter: NfcAdapter?,
    viewModel: NfcViewModel = viewModel()
) {
    Scaffold(
        modifier = modifier,
        topBar = { BasicTopBar(title = "Read and Write", subTitle = "NFC") }
    ) { paddingValues ->
        ReadAndWriteScreen(
            modifier = modifier.padding(paddingValues),
            context = context,
            activity = activity,
            nfcAdapter = nfcAdapter,
            viewModel = viewModel
        )
    }

}
