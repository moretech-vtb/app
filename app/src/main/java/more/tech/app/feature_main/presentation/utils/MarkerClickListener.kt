package more.tech.app.feature_main.presentation.utils

import android.content.Context
import org.osmdroid.views.overlay.Marker

interface MarkerClickListener {
    fun onMarkerClick(marker: Marker, context: Context, place: Any)
}