package more.tech.app.feature_main.presentation.main

import android.Manifest
import android.R.attr.height
import android.R.attr.width
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import more.tech.app.core.presentation.util.ex.toast
import more.tech.app.core.util.CustomResult
import more.tech.app.databinding.BottomSheetAtmInfoBinding
import more.tech.app.databinding.BottomSheetTripBinding
import more.tech.app.databinding.FragmentMainBinding
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.models.Office
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
class MainFragment : Fragment() {

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

        configurationMap()
        initMap()
        getLocation()
        setZoomMultiTouch()
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

    private fun configurationMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
//        Configuration.getInstance().osmdroidBasePath = filesDir todo
    }

    private fun initMap() {
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.controller.setZoom(15.0)
    }

    private fun setMarker(place: ATM) {
        val placeLocation = GeoPoint(place.latitude, place.longitude)

        val marker = Marker(binding.map)
        marker.position = placeLocation
        marker.title = place.address
        marker.icon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_marker,
        )

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.setOnMarkerClickListener { marker2, _ ->
            showATMBottomSheetDialog(marker2, requireContext(), place)
            return@setOnMarkerClickListener true
        }

        binding.map.overlays.add(marker)
        binding.map.invalidate()
    }

    private fun setMarker(place: Office) {
        val placeLocation = GeoPoint(place.latitude, place.longitude)

        val marker = Marker(binding.map)
        marker.position = placeLocation
        marker.title = place.address
        marker.icon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_marker
        )
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.setOnMarkerClickListener { marker2, _ ->
            showOfficeBottomSheetDialog(marker2, requireContext(), place)
            return@setOnMarkerClickListener true
        }

        binding.map.overlays.add(marker)
        binding.map.invalidate()
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

    private fun setZoomMultiTouch() {
        binding.map.setMultiTouchControls(true)
        binding.map.overlays.add(RotationGestureOverlay(binding.map))
    }

    private fun buildRoad(endPoint: GeoPoint, mean: String = OSRMRoadManager.MEAN_BY_CAR) {
        binding.map.overlays.removeAll { it is Polyline }

        CoroutineScope(Dispatchers.IO).launch {
            val roadManager = OSRMRoadManager(requireActivity(), System.getProperty("http.agent"))
            roadManager.setMean(mean)
            val waypoints = arrayListOf(locationOverlay.myLocation ?: startPoint, endPoint)
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            withContext(Dispatchers.Main) {
                binding.map.overlays.add(0, roadOverlay)
                binding.map.invalidate()
                toast(road.mDuration.toString())
            }

        }
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.btnUpdate.setOnClickListener {
            viewModel.fetchATMs()
//            viewModel.startBackgroundUpdates()
        }
        binding.getMyLocation.setOnClickListener {
            getLocation()
        }
    }

    private fun initObservers() {
        viewModel.progressBarVisibility.observe(viewLifecycleOwner) { visible ->
            binding.progressBar.visibility = if (visible) View.VISIBLE else View.GONE
        }

        viewModel.lastFetchTime.observe(viewLifecycleOwner) { time ->
            binding.lastUpdateTimeTextView.text =
                getString(R.string.last_fetch_time, time)
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
//        viewModel.officesLiveData.observe(viewLifecycleOwner) { result ->
//            when (result) {
//                is CustomResult.Success -> {
//                    binding.progressBar.isVisible = false
//                    result.data.forEach {
//                        setMarker(it)
//                    }
//                }
//
//                is CustomResult.Error -> {
//                    binding.progressBar.isVisible = false
//                }
//            }
//        }
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
            showTripBottomSheetDialog(marker, context, place)
        }

        bottomSheetBinding.address.text = place.address
        val distance = locationOverlay.myLocation.distanceToAsDouble(marker.position)

        bottomSheetBinding.distance.text = distance.toDistance()
        if (place.allDay) bottomSheetBinding.allDays.text = getString(R.string.all_days)

        with (bottomSheetBinding) {
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

    private fun showTripBottomSheetDialog(marker: Marker, context: Context, place: ATM) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding: BottomSheetTripBinding =
            BottomSheetTripBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root
        var mean = OSRMRoadManager.MEAN_BY_FOOT
        bottomSheetBinding.byFoot.background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorVtb80))

        bottomSheetBinding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.byFoot.setOnClickListener {
            bottomSheetBinding.byFoot.background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorVtb80))
            bottomSheetBinding.byCar.background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorCyan40))
            mean = OSRMRoadManager.MEAN_BY_FOOT
        }

        bottomSheetBinding.byCar.setOnClickListener {
            bottomSheetBinding.byFoot.background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorCyan40))
            bottomSheetBinding.byCar.background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorVtb80))
            mean = OSRMRoadManager.MEAN_BY_CAR
        }
        bottomSheetBinding.btnStart.setOnClickListener {
            bottomSheetDialog.dismiss()
            buildRoad(marker.position, mean)
        }

        bottomSheetBinding.end.text = place.address

        calculateRoadInfo(
            endPoint = marker.position,
            mean = OSRMRoadManager.MEAN_BY_FOOT,
            durationTextView = bottomSheetBinding.footTime,
            distanceTextView = bottomSheetBinding.footDistance
        )
        calculateRoadInfo(
            endPoint = marker.position,
            mean = OSRMRoadManager.MEAN_BY_CAR,
            durationTextView = bottomSheetBinding.carTime,
            distanceTextView = bottomSheetBinding.carDistance
        )

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun calculateRoadInfo(
        endPoint: GeoPoint,
        mean: String = OSRMRoadManager.MEAN_BY_CAR,
        distanceTextView: TextView,
        durationTextView: TextView,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val roadManager = OSRMRoadManager(requireActivity(), System.getProperty("http.agent"))
            roadManager.setMean(mean)
            val waypoints =
                arrayListOf<GeoPoint>(locationOverlay.myLocation ?: startPoint, endPoint)

            val road = roadManager.getRoad(waypoints)
            val duration = road.mDuration
            val distance = road.mLength * 1000.0

            withContext(Dispatchers.Main) {
                distanceTextView.text = distance.toDistance()
                durationTextView.text = duration.toTime()
            }
        }
    }

    private fun showOfficeBottomSheetDialog(marker: Marker, context: Context, place: Office) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetBinding: BottomSheetAtmInfoBinding =
            BottomSheetAtmInfoBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root

        bottomSheetBinding.closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.btnRoad.setOnClickListener {
            bottomSheetDialog.dismiss()
//            showTripBottomSheetDialog(marker, context, place)
        }

        bottomSheetBinding.address.text = place.address
        val distance = locationOverlay.myLocation.distanceToAsDouble(marker.position)

        bottomSheetBinding.distance.text = distance.toDistance()
//        if (place.allDay) bottomSheetBinding.allDays.text = getString(R.string.all_days)

        with (bottomSheetBinding) {
//            wheelchairService.isVisible = place.wheelchairService
//            blindService.isVisible = place.blindService
//            nfcService.isVisible = place.nfcService
//            qrService.isVisible = place.qrService
//            supportsUsd.isVisible = place.supportsUsd
//            supportsChargeRub.isVisible = place.supportsChargeRub
//            supportsEur.isVisible = place.supportsEur
//            supportsRub.isVisible = place.supportsRub
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

}