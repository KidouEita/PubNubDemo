package tw.com.pubnubdemo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tw.com.pubnubdemo.data.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataStore: PreferencesDataStore) : ViewModel() {

    val channelList =
        dataStore.getChannels().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    val username =
        dataStore.getUserId().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun addNewChannel(channelName: String) {
        viewModelScope.launch {
            val originalList = channelList.value?.toMutableList() ?: mutableListOf()
            dataStore.setChannels(originalList.apply { add(channelName) })
        }
    }
}