package com.example.readandwritenfc


import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcF
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.readandwritenfc.ui.sreens.MainScreen
import com.example.readandwritenfc.ui.theme.ReadAndWriteNfcTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NfcViewModel by viewModels()

    private var intentFiltersArray: Array<IntentFilter>? = null

    private var pendingIntent: PendingIntent? = null

    private val nfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }
    private var techListsArray = arrayOf(arrayOf<String>(NfcF::class.java.name))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, javaClass).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }

        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("text/plain")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }

        intentFiltersArray = arrayOf(ndef)

        setContent {
            ReadAndWriteNfcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        modifier = Modifier,
                        context = LocalContext.current,
                        activity = this,
                        nfcAdapter = nfcAdapter,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(
            this,
            pendingIntent,
            intentFiltersArray,
            techListsArray
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("Eddycito", "onNewIntent, intent.action: ${intent?.action}")
        if (intent != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
                if (viewModel.isWriteNfc.value!!) {
                    val textId = viewModel.textId.value!!
                    val textName = viewModel.textName.value!!
                    if (textId.isNotBlank() && textName.isNotBlank()) {
                        val tag: Tag? =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
                            } else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                            }
                        val ndef = Ndef.get(tag) ?: return
                        if (ndef.isWritable) {
                            val message = NdefMessage(
                                arrayOf(
                                    NdefRecord.createTextRecord("es", textId),
                                    NdefRecord.createTextRecord("es", textName)
                                )
                            )

                            ndef.connect()
                            ndef.writeNdefMessage(message)
                            ndef.close()

                            Toast.makeText(this, "NFC written", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this, "NFC not writable", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Fields empty", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    val parcelables =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableArrayExtra(
                                NfcAdapter.EXTRA_NDEF_MESSAGES,
                                Parcelable::class.java
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                        }
                    with(parcelables) {
                        val inNdefMessage = this?.get(0) as NdefMessage
                        val inNdefRecords = inNdefMessage.records

                        val inTextId = inNdefRecords[0]
                        val inMessage1 = String(inTextId.payload)
                        val inMessage1Drop = inMessage1.drop(3)

                        val inTextName = inNdefRecords[1]
                        val inMessage2 = String(inTextName.payload)
                        val inMessage2Drop = inMessage2.drop(3)
                        viewModel.updateTextId(inMessage1Drop)
                        viewModel.updateTextName(inMessage2Drop)

                        Toast.makeText(
                            this@MainActivity,
                            "NFC read",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

            }
        }
    }
}
