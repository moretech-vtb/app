package more.tech.app.feature_main.presentation.utils

import more.tech.app.feature_main.domain.models.RoadInfo
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.util.GeoPoint

interface OsmdroidService {
    suspend fun calculateRoadInfo(
        startPoint: GeoPoint,
        endPoint: GeoPoint,
        mean: String
    ): RoadInfo
}

class OsmdroidServiceImpl(private val roadManager: OSRMRoadManager) : OsmdroidService {
    override suspend fun calculateRoadInfo(startPoint: GeoPoint, endPoint: GeoPoint, mean: String): RoadInfo {
        roadManager.setMean(mean)
        val waypoints = arrayListOf(startPoint, endPoint)

        val road = roadManager.getRoad(waypoints)
        val duration = road.mDuration
        val distance = road.mLength * 1000.0

        return RoadInfo(duration, distance)
    }
}