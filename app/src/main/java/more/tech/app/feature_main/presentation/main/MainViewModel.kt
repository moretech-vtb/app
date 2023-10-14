package more.tech.app.feature_main.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.core.util.CustomResult
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.models.Office
import more.tech.app.feature_main.domain.use_case.FetchATMsUseCase
import more.tech.app.feature_main.domain.use_case.FetchOfficesUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferences: SharedPrefsManager,
    private val fetchATMsUseCase: FetchATMsUseCase,
    private val fetchOfficesUseCase: FetchOfficesUseCase
) : ViewModel() {

    val progressBarVisibility = MutableLiveData<Boolean>()
    val lastFetchTime = MutableLiveData<String>()

    val atmsLiveData = MutableLiveData<CustomResult<List<ATM>>>()
    val officesLiveData = MutableLiveData<CustomResult<List<Office>>>()

    init {
        progressBarVisibility.value = false
        lastFetchTime.value = preferences.getLastFetchTime()
        fetchATMs()
        fetchOffices()
    }

    fun fetchATMs() {
        progressBarVisibility.value = true

        viewModelScope.launch {
            when (val result = fetchATMsUseCase()) {
                is CustomResult.Success -> {
                    progressBarVisibility.value = false
                    val fetch = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    lastFetchTime.value = fetch
                    preferences.saveLastFetchTime(fetch)
                    atmsLiveData.value = result
                }

                is CustomResult.Error -> {
                    progressBarVisibility.value = false
                }
            }
        }

    }

    fun fetchOffices() {
        progressBarVisibility.value = true

        viewModelScope.launch {
            when (val result = fetchOfficesUseCase()) {
                is CustomResult.Success -> {
                    progressBarVisibility.value = false
                    val fetch = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    lastFetchTime.value = fetch
                    preferences.saveLastFetchTime(fetch)
                    officesLiveData.value = result
                }

                is CustomResult.Error -> {
                    progressBarVisibility.value = false
                }
            }
        }

    }

}