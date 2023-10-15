package more.tech.app.feature_main.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import more.tech.app.BuildConfig
import more.tech.app.R
import more.tech.app.core.presentation.util.ex.onBackPressedDispatcher
import more.tech.app.core.presentation.util.ex.toDistance
import more.tech.app.core.presentation.util.ex.toTime
import more.tech.app.core.util.CustomResult
import more.tech.app.databinding.BottomAtmFilterBinding
import more.tech.app.databinding.BottomOfficeFilterBinding
import more.tech.app.databinding.BottomSheetAtmInfoBinding
import more.tech.app.databinding.BottomSheetOfficeInfoBinding
import more.tech.app.databinding.BottomSheetTripBinding
import more.tech.app.databinding.FragmentMainBinding
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.models.FilterOption
import more.tech.app.feature_main.domain.models.Office
import more.tech.app.feature_main.presentation.utils.CustomAdapter
import more.tech.app.feature_main.presentation.utils.FilterAdapter
import more.tech.app.feature_main.presentation.utils.MarkerClickListener
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@AndroidEntryPoint
class MainFragment : Fragment(), MarkerClickListener {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var locationOverlay: MyLocationNewOverlay
    private var startPoint: GeoPoint = GeoPoint(
        55.93941603121454,
        37.51538748523003
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setupLocationOverlay()
            } else {
                // Permission is denied, todo
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher(this)
        binding = FragmentMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        initMap()
        getLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.controller.setZoom(15.0)
        binding.map.setMultiTouchControls(true)
        binding.map.overlays.add(RotationGestureOverlay(binding.map))
    }

    override fun onMarkerClick(marker: Marker, context: Context, place: Any) {
        when (place) {
            is ATM -> showATMBottomSheetDialog(marker, context, place)
            is Office -> showOfficeBottomSheetDialog(marker, context, place)
            else -> throw IllegalArgumentException("Invalid marker type")
        }
    }

    private fun setMarker(place: Any): Marker {
        val placeLocation = when (place) {
            is ATM -> GeoPoint(place.latitude, place.longitude)
            is Office -> GeoPoint(place.latitude, place.longitude)
            else -> throw IllegalArgumentException("Invalid marker type")
        }

        val marker = Marker(binding.map)
        marker.position = placeLocation
        marker.title = when (place) {
            is ATM -> place.address
            is Office -> place.address
            else -> throw IllegalArgumentException("Invalid marker type")
        }

        marker.icon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_marker,
        )

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.setOnMarkerClickListener { marker2, _ ->
            onMarkerClick(marker2, requireContext(), place)
            return@setOnMarkerClickListener true
        }

        binding.map.overlays.add(marker)
        binding.map.invalidate()
        return marker
    }

    private fun getLocation() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setupLocationOverlay() {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        val imageDraw =
            ContextCompat.getDrawable(requireContext(), R.drawable.marker_cluster)!!.toBitmap()
        locationOverlay.setPersonAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        locationOverlay.setDirectionAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        locationOverlay.setPersonIcon(imageDraw)
        locationOverlay.setDirectionIcon(imageDraw)

        binding.map.overlays.add(locationOverlay)
    }

    private fun buildRoad(endPoint: GeoPoint, mean: String = OSRMRoadManager.MEAN_BY_CAR) {
        binding.map.overlays.removeAll { it is Polyline }

        CoroutineScope(Dispatchers.IO).launch {
            val roadManager = OSRMRoadManager(requireActivity(), System.getProperty("http.agent"))
            roadManager.setMean(mean)
            val waypoints = arrayListOf(locationOverlay.myLocation ?: startPoint, endPoint)
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay.outlinePaint.color =
                ContextCompat.getColor(requireActivity(), R.color.colorVtb)
            roadOverlay.outlinePaint.strokeWidth = 12f
            withContext(Dispatchers.Main) {
                binding.map.overlays.add(0, roadOverlay)
                binding.map.invalidate()
            }

        }
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.getMyLocation.setOnClickListener {
            getLocation()
        }

        binding.btnATM.setOnClickListener {
            showATMFilterDialog(requireContext())
        }
        binding.btnOffice.setOnClickListener {
            showOfficeFilterDialog(requireContext())
        }
    }

    private fun initObservers() {
        viewModel.progressBarVisibility.observe(viewLifecycleOwner) { visible ->
            binding.progressBar.visibility = if (visible) View.VISIBLE else View.GONE
        }
        viewModel.atmsLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is CustomResult.Success -> {
                    binding.progressBar.isVisible = false
                    result.data.forEach {
                        setMarker(it)
                    }
                }

                is CustomResult.Error -> {
                    binding.progressBar.isVisible = false
                }
            }
        }
        viewModel.officesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is CustomResult.Success -> {
                    binding.progressBar.isVisible = false
                    result.data.forEach {
                        setMarker(it)
                    }
                }

                is CustomResult.Error -> {
                    binding.progressBar.isVisible = false
                }
            }
        }
        viewModel.filteredATMsLiveData.observe(viewLifecycleOwner) { filteredATMs ->
            drawFiltered(filteredATMs)
        }
        viewModel.filteredOfficesLiveData.observe(viewLifecycleOwner) { filteredOffices ->
            drawFiltered(filteredOffices)
        }
    }

    private fun drawFiltered(filteredList: List<Any>) {
        binding.map.overlays.filterIsInstance<Marker>().forEach { marker ->
            binding.map.overlays.remove(marker)
        }
        binding.map.overlays.filterIsInstance<Polyline>().forEach { polyline ->
            binding.map.overlays.remove(polyline)
        }
        binding.map.invalidate()
        if (filteredList.isNotEmpty()) {
            for (i in filteredList.indices) {
                val marker = setMarker(filteredList[i])

                if (i == 0) {
                    showInfoAboutFirstFilteredItem(marker, filteredList[i])
                }
            }
        }
    }

    private fun showInfoAboutFirstFilteredItem(marker: Marker, item: Any) {
        marker.icon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.pin,
        )
        binding.map.overlays.add(marker)
        binding.map.invalidate()
        when (item) {
            is ATM -> showATMBottomSheetDialog(marker, requireContext(), item)
            is Office -> showOfficeBottomSheetDialog(marker, requireContext(), item)
            else -> throw IllegalArgumentException("Invalid marker type")
        }
    }

    private fun showTripBottomSheetDialog(marker: Marker, context: Context, address: String) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding: BottomSheetTripBinding =
            BottomSheetTripBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root
        var mean = OSRMRoadManager.MEAN_BY_FOOT

        bottomSheetBinding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.byFoot.setOnClickListener {
            bottomSheetBinding.byFoot.setTextColor(Color.WHITE)
            bottomSheetBinding.byFoot.setBackgroundResource(R.drawable.selected_item_background)
            bottomSheetBinding.byCar.setTextColor(Color.BLACK)
            bottomSheetBinding.byCar.setBackgroundResource(R.drawable.unselected_item_background)
            mean = OSRMRoadManager.MEAN_BY_FOOT
        }

        bottomSheetBinding.byCar.setOnClickListener {
            bottomSheetBinding.byCar.setTextColor(Color.WHITE)
            bottomSheetBinding.byCar.setBackgroundResource(R.drawable.selected_item_background)
            bottomSheetBinding.byFoot.setTextColor(Color.BLACK)
            bottomSheetBinding.byFoot.setBackgroundResource(R.drawable.unselected_item_background)
            mean = OSRMRoadManager.MEAN_BY_CAR
        }
        bottomSheetBinding.btnStart.setOnClickListener {
            bottomSheetDialog.dismiss()
            buildRoad(marker.position, mean)
        }

        bottomSheetBinding.end.text = address

        calculateRoadInfo(
            endPoint = marker.position,
            mean = OSRMRoadManager.MEAN_BY_FOOT,
            button = bottomSheetBinding.byFoot,
        )
        calculateRoadInfo(
            endPoint = marker.position,
            mean = OSRMRoadManager.MEAN_BY_CAR,
            button = bottomSheetBinding.byCar,
        )

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showATMBottomSheetDialog(marker: Marker, context: Context, place: ATM) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding: BottomSheetAtmInfoBinding =
            BottomSheetAtmInfoBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root

        bottomSheetBinding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.btnRoad.setOnClickListener {
            bottomSheetDialog.dismiss()
            showTripBottomSheetDialog(marker, context, place.address)
        }

        bottomSheetBinding.address.text = place.address
        val distance = locationOverlay.myLocation.distanceToAsDouble(marker.position)

        bottomSheetBinding.distance.text = distance.toDistance()
        if (place.allDay) bottomSheetBinding.allDays.text = getString(R.string.allDay)

        with(bottomSheetBinding) {
            wheelchairService.isVisible = place.wheelchairService
            blindService.isVisible = place.blindService
            nfcService.isVisible = place.nfcService
            qrService.isVisible = place.qrService
            supportsUsd.isVisible = place.supportsUsd
            supportsChargeRub.isVisible = place.supportsChargeRub
            supportsEur.isVisible = place.supportsEur
            supportsRub.isVisible = place.supportsRub
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showOfficeBottomSheetDialog(marker: Marker, context: Context, place: Office) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding: BottomSheetOfficeInfoBinding =
            BottomSheetOfficeInfoBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root

        bottomSheetBinding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.btnRoad.setOnClickListener {
            bottomSheetDialog.dismiss()
            showTripBottomSheetDialog(marker, context, place.address)
        }

        bottomSheetBinding.name.text = place.name
        bottomSheetBinding.address.text = place.address

        with(bottomSheetBinding) {
            officeType.text = getString(R.string.officeType, place.officeType)
            rko.text = getString(R.string.rko, if (place.rko) "Да" else "Нет")
            suoAvailability.text =
                getString(R.string.suoAvailability, if (place.suoAvailability) "Да" else "Нет")
            kep.text = getString(R.string.kep, if (place.kep) "Да" else "Нет")
            hasRamp.text = getString(R.string.hasRamp, if (place.hasRamp) "Да" else "Нет")
        }

        if (place.openHoursIndividual.isNotEmpty()) {
            with(bottomSheetBinding) {
                openHoursInd.visibility = View.VISIBLE
                customChartViewInd.visibility = View.VISIBLE
                recyclerViewInd.visibility = View.VISIBLE

                recyclerViewInd.layoutManager = LinearLayoutManager(activity)
                val customAdapter = CustomAdapter(place.openHoursIndividual)
                recyclerViewInd.adapter = customAdapter

                if (!place.loadsIndividual.isNullOrEmpty()) {
                    val dataPoints = place.loadsIndividual.let { list -> list.map { it.toInt() } }
                    customChartViewInd.setDataPoints(dataPoints)
                }
            }
        }

        if (place.openHours.isNotEmpty()) {
            with(bottomSheetBinding) {
                openHours.visibility = View.VISIBLE
                customChartView.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE

                recyclerView.layoutManager = LinearLayoutManager(activity)
                val customAdapter = CustomAdapter(place.openHoursIndividual)
                recyclerView.adapter = customAdapter

                if (!place.loads.isNullOrEmpty()) {
                    val dataPoints = place.loads.let { list -> list.map { it.toInt() } }
                    customChartView.setDataPoints(dataPoints)
                }
            }
        }

        val distance = locationOverlay.myLocation.distanceToAsDouble(marker.position)
        bottomSheetBinding.distance.text = distance.toDistance()

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showATMFilterDialog(context: Context) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding: BottomAtmFilterBinding =
            BottomAtmFilterBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root

        val filterOptions = arrayOf(
            FilterOption("allDay", getString(R.string.allDay)),
            FilterOption("wheelchairService", getString(R.string.wheelchairService)),
            FilterOption("blindService", getString(R.string.blindService)),
            FilterOption("nfcService", getString(R.string.nfcService)),
            FilterOption("qrService", getString(R.string.qrService)),
            FilterOption("supportsUsd", getString(R.string.supportsUsd)),
            FilterOption("supportsChargeRub", getString(R.string.supportsChargeRub)),
            FilterOption("supportsEur", getString(R.string.supportsEur)),
            FilterOption("supportsRub", getString(R.string.supportsRub)),
        )

        val adapter = FilterAdapter(filterOptions)
        bottomSheetBinding.filterRecyclerView.adapter = adapter
        bottomSheetBinding.filterRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bottomSheetBinding.btnStart.setOnClickListener {
            viewModel.filterATMs(
                adapter.getSelectedKeys(),
                locationOverlay.myLocation ?: startPoint
            )
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showOfficeFilterDialog(context: Context) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding: BottomOfficeFilterBinding =
            BottomOfficeFilterBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root

        val filterOptions = arrayOf(
            FilterOption("supportsInd", getString(R.string.supportsInd)),
            FilterOption("supports", getString(R.string.supports)),
            FilterOption("suoAvailability", getString(R.string.suoAvailabilityStatus)),
            FilterOption("hasRamp", getString(R.string.hasRampStatus)),
        )

        val adapter = FilterAdapter(filterOptions)
        bottomSheetBinding.filterRecyclerView.adapter = adapter
        bottomSheetBinding.filterRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bottomSheetBinding.btnStart.setOnClickListener {
            viewModel.filterOffices(
                adapter.getSelectedKeys(),
                locationOverlay.myLocation ?: startPoint
            )
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun calculateRoadInfo(
        endPoint: GeoPoint,
        mean: String = OSRMRoadManager.MEAN_BY_CAR,
        button: Button,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val roadManager = OSRMRoadManager(requireActivity(), System.getProperty("http.agent"))
            roadManager.setMean(mean)
            val waypoints = arrayListOf(locationOverlay.myLocation ?: startPoint, endPoint)

            val road = roadManager.getRoad(waypoints)
            val duration = road.mDuration
            val distance = road.mLength * 1000.0

            withContext(Dispatchers.Main) {
                button.text = "${duration.toTime()}, ${distance.toDistance()}"
            }
        }
    }

}