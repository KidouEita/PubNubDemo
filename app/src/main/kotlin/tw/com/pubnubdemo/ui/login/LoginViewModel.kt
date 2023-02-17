package tw.com.pubnubdemo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tw.com.pubnubdemo.data.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStore: PreferencesDataStore
) : ViewModel() {

    val userId = dataStore.getUserId().shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    fun login(userId: String) {
        viewModelScope.launch {
            dataStore.setUserId(userId)
        }
    }
}