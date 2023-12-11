package com.example.readandwritenfc


import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
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
    private var techListsArray =
        arrayOf(
            arrayOf<String>(NfcF::class.java.name),
            arrayOf<String>(Ndef::class.java.name),
            arrayOf<String>(NdefFormatable::class.java.name),
            arrayOf<String>(NfcA::class.java.name),
            arrayOf<String>(MifareClassic::class.java.name)
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Eddycito", "onCreate, intent.action: ${intent?.action}")

        val intent = Intent(this, javaClass).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }

        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }

        val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }

        intentFiltersArray = arrayOf(ndef, tech)

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
        Log.d("Eddycito", "onPause, intent.action: ${intent?.action}")
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(
            this,
            pendingIntent,
            intentFiltersArray,
            techListsArray
        )
        Log.d("Eddycito", "onResume, intent.action: ${intent?.action}")
    }

    private fun processIntent(intent: Intent?) {
        if (intent != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
                NfcAdapter.ACTION_TECH_DISCOVERED == intent.action
            ) {
                if (viewModel.isWriteNfc.value!!) writeNfc(intent)
                else readNfc(intent)
            }
        }
        viewModel.updateShowLoadingDialog(false)
    }

    private fun readNfc(intent: Intent) {
        viewModel.updateTextId("")
        viewModel.updateTextName("")
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

            if (inNdefRecords.isNotEmpty()) {

                val inTextId = inNdefRecords[0]
                val inMessage1 = String(inTextId.payload)
                val inMessage1Drop = inMessage1.drop(3)
                Log.d(
                    "NfcTag1", """
                                intextid = $inTextId 
                                inmessage1 = $inMessage1
                                inmessage1drop = $inMessage1Drop
                            """.trimIndent()
                )
                viewModel.updateTextId(inMessage1Drop)

                if (inNdefRecords.size > 1) {
                    val inTextName = inNdefRecords[1]
                    val inMessage2 = String(inTextName.payload)
                    val inMessage2Drop = inMessage2.drop(3)
                    Log.d(
                        "NfcTag2", """
                                $inTextName
                                $inMessage2
                                $inMessage2Drop
                            """.trimIndent()
                    )
                    viewModel.updateTextName(inMessage2Drop)
                }

                Toast.makeText(
                    this@MainActivity,
                    "NFC read",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "NFC empty",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return
            }

        }
    }

    private fun writeNfc(intent: Intent) {
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
            viewModel.updateTextId("")
            viewModel.updateTextName("")
        } else {
            Toast.makeText(this, "Fields empty", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //setIntent(intent)
        Log.d("Eddycito", "onNewIntent, intent.action: ${intent?.action}")
        if (viewModel.showLoadingDialog.value!!) processIntent(intent)
    }
}
