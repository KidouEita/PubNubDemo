package tw.com.pubnubdemo.ui.home

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tw.com.pubnubdemo.data.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStore: PreferencesDataStore,
    application: Application
) : AndroidViewModel(application) {

    val channelList =
        dataStore.getChannels().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    val username =
        dataStore.getUserId().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val file = File.createTempFile("picFromCamera", ".jpg", application.filesDir)
    val uriFromCamera: Uri =
        FileProvider.getUriForFile(application, "${application.packageName}.provider", file)

    private val _uriState = MutableStateFlow<Uri?>(null)
    val uriState = _uriState.asStateFlow()

    fun addNewChannel(channelName: String) {
        viewModelScope.launch {
            val originalList = channelList.value?.toMutableList() ?: mutableListOf()
            dataStore.setChannels(originalList.apply { add(channelName) })
        }
    }

    fun setDisplayFromCamera() {
        viewModelScope.launch {
            _uriState.emit(uriFromCamera)
        }
    }

    fun setDisplayFromGallery(newUri: Uri) {
        viewModelScope.launch {
            _uriState.emit(newUri)
        }
    }

    fun deleteCameraTempFile() {
        file.delete()
    }

}