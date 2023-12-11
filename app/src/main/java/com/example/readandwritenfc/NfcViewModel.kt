package com.example.readandwritenfc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NfcViewModel : ViewModel() {

    private val _showLoadingDialog = MutableLiveData(false)
    val showLoadingDialog: LiveData<Boolean> get() = _showLoadingDialog

    private val _isWriteNfc = MutableLiveData(false)
    val isWriteNfc: LiveData<Boolean> get() = _isWriteNfc

    private val _textId = MutableLiveData("")
    val textId: LiveData<String> get() = _textId

    private val _textName = MutableLiveData("")
    val textName: LiveData<String> get() = _textName

    fun updateTextId(textId: String) {
        _textId.value = textId
    }

    fun updateTextName(textName: String) {
        _textName.value = textName
    }

    fun updateIsWriteNfc(isWriteNfc: Boolean) {
        _isWriteNfc.value = isWriteNfc
        _textId.value = ""
        _textName.value = ""
    }

    fun updateShowLoadingDialog(showLoadingDialog: Boolean) {
        _showLoadingDialog.value = showLoadingDialog
    }
}