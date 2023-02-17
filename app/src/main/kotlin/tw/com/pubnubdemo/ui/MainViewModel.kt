package tw.com.pubnubdemo.ui

import androidx.lifecycle.ViewModel
import tw.com.pubnubdemo.data.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: PreferencesDataStore
) : ViewModel() {

    val isLoggedIn get() = dataStore.isLoggedIn()

}