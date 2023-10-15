package more.tech.app.feature_main.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.core.presentation.util.ex.toDistance
import more.tech.app.core.presentation.util.ex.toTime
import more.tech.app.core.util.CustomResult
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.models.Office
import more.tech.app.feature_main.domain.use_case.FetchATMsUseCase
import more.tech.app.feature_main.domain.use_case.FetchOfficesUseCase
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIUS_OF_EARTH_KM = 6371

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferences: SharedPrefsManager,
    private val fetchATMsUseCase: FetchATMsUseCase,
    private val fetchOfficesUseCase: FetchOfficesUseCase
) : ViewModel() {

    val progressBarVisibility = MutableLiveData<Boolean>()
    val lastFetchTime = MutableLiveData<String>()

    val atmsLiveData = MutableLiveData<CustomResult<List<ATM>>>()
    private val fullATMsList = mutableListOf<ATM>()
    val filteredATMsLiveData = MutableLiveData<List<ATM>>()

    val officesLiveData = MutableLiveData<CustomResult<List<Office>>>()
    private val fullOfficesList = mutableListOf<Office>()
    val filteredOfficesLiveData = MutableLiveData<List<Office>>()

    init {
        progressBarVisibility.value = false
        lastFetchTime.value = preferences.getLastFetchTime()
        fetchATMs()
        fetchOffices()
    }

    private fun fetchATMs() {
        progressBarVisibility.value = true

        viewModelScope.launch {
            when (val result = fetchATMsUseCase()) {
                is CustomResult.Success -> {
                    progressBarVisibility.value = false
                    val fetch = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    lastFetchTime.value = fetch
                    preferences.saveLastFetchTime(fetch)

                    fullATMsList.clear()
                    fullATMsList.addAll(result.data)
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

                    fullOfficesList.clear()
                    fullOfficesList.addAll(result.data)
                    officesLiveData.value = result
                }

                is CustomResult.Error -> {
                    progressBarVisibility.value = false
                }
            }
        }

    }

    fun filterATMs(filters: Set<String>, myLocation: GeoPoint) {
        if (filters.isEmpty()) {
            displayFilteredATMsOnMap(fullATMsList, myLocation)
            return
        }

        var filteredATMs = fullATMsList.toList()

        filters.forEach { filter ->
            filteredATMs = when (filter) {
                "allDay" -> filteredATMs.filter { it.allDay }
                "wheelchairService" -> filteredATMs.filter { it.wheelchairService }
                "blindService" -> filteredATMs.filter { it.blindService }
                "nfcService" -> filteredATMs.filter { it.nfcService }
                "qrService" -> filteredATMs.filter { it.qrService }
                "supportsUsd" -> filteredATMs.filter { it.supportsUsd }
                "supportsChargeRub" -> filteredATMs.filter { it.supportsChargeRub }
                "supportsEur" -> filteredATMs.filter { it.supportsEur }
                "supportsRub" -> filteredATMs.filter { it.supportsRub }
                else -> filteredATMs
            }
        }

        displayFilteredATMsOnMap(filteredATMs, myLocation)

    }

    fun filterOffices(filters: Set<String>) {
        if (filters.isEmpty()) {
            displayFilteredOfficesOnMap(fullOfficesList)
            return
        }

        var filteredOffices = fullOfficesList.toList()

        filters.forEach { filter ->
            filteredOffices = when (filter) {
                "supportsInd" -> filteredOffices.filter { it.openHoursIndividual.isNotEmpty() }
                "supports" -> filteredOffices.filter { it.openHours.isNotEmpty() }
                "suoAvailability" -> filteredOffices.filter { it.suoAvailability }
                "hasRamp" -> filteredOffices.filter { it.hasRamp }
                else -> filteredOffices
            }
        }
        displayFilteredOfficesOnMap(filteredOffices)
    }


    private fun displayFilteredATMsOnMap(filteredATMs: List<ATM>, myLocation: GeoPoint) {
        val sortedATMs = filteredATMs.sortedBy { atm ->
            calculateDistance(
                atmLatitude = atm.latitude,
                atmLongitude = atm.longitude,
                myLocation = myLocation,
            )
        }

        filteredATMsLiveData.value = sortedATMs
    }

    private fun displayFilteredOfficesOnMap(filteredOffices: List<Office>) {
        filteredOfficesLiveData.value = filteredOffices
    }

    private fun calculateDistance(
        atmLatitude: Double,
        atmLongitude: Double,
        myLocation: GeoPoint
    ): Double {
        val lat1 = Math.toRadians(atmLatitude)
        val lon1 = Math.toRadians(atmLongitude)
        val lat2 = Math.toRadians(myLocation.latitude)
        val lon2 = Math.toRadians(myLocation.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + (cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return RADIUS_OF_EARTH_KM * c
    }

}