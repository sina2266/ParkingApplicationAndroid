package ir.parsgeeks.parkingapp

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import com.cedarstudios.cedarmapssdk.CedarMaps
import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.ColorUtils


//region MapboxMap Extension
fun MapboxMap.addMarkerToMapViewAtPosition
            (coordinate: LatLng, resources: Resources) {
    val MARKERS_SOURCE = "markers-source"
    val MARKERS_LAYER = "markers-layer"
    val MARKER_ICON_ID = "marker-icon-id"
    if (style != null) {
        val style = style

        if (style?.getImage(MARKER_ICON_ID) == null) {
            style?.addImage(
                MARKER_ICON_ID,
                BitmapFactory.decodeResource(
                    resources, R.drawable.map_pin
                )
            )
        }

        val geoJsonSource: GeoJsonSource?
        if (style?.getSource(MARKERS_SOURCE) == null) {
            geoJsonSource = GeoJsonSource(MARKERS_SOURCE)
            style?.addSource(geoJsonSource)
        } else {
            geoJsonSource = style.getSource(MARKERS_SOURCE) as GeoJsonSource?
        }
        if (geoJsonSource == null) {
            return
        }

        val feature = Feature.fromGeometry(
            Point.fromLngLat(coordinate.longitude, coordinate.latitude)
        )
        geoJsonSource.setGeoJson(feature)

        style?.removeLayer(MARKERS_LAYER)

        val symbolLayer = SymbolLayer(MARKERS_LAYER, MARKERS_SOURCE)
        symbolLayer.withProperties(
            PropertyFactory.iconImage(MARKER_ICON_ID),
            PropertyFactory.iconAllowOverlap(true)
        )
        style?.addLayer(symbolLayer)
    }
}


fun MapboxMap.animateToCoordinate(coordinate: LatLng) {
    val position = CameraPosition.Builder()
        .target(coordinate)
        .zoom(16.0)
        .build()
    animateCamera(CameraUpdateFactory.newCameraPosition(position))
}


fun MapboxMap.computeDirection(departure: LatLng, destination: LatLng, lineManager: LineManager, context: Context) {

    CedarMaps.getInstance().direction(departure, destination,
        object : GeoRoutingResultListener {
            override fun onSuccess(result: GeoRouting) {
                if (result.routes == null) {
                    return
                }
                val route = result.routes!![0]
                var distance: Double? = route.distance ?: return
                if (distance!! > 1000) {
                    distance = distance!! / 1000.0
                    distance = Math.round(distance * 100.0).toDouble() / 100.0
                } else {
                    distance = Math.round(distance!!).toDouble()
                }

                if (route.geometry == null || route.geometry!!.getCoordinates() == null) {
                    return
                }
                val coordinates = ArrayList(route.geometry!!.getCoordinates())

                drawCoordinatesInBound(coordinates, route.boundingBox!!,lineManager,context)

            }

            override fun onFailure(error: String) {
                lineManager.deleteAll()
            }
        })
}

fun MapboxMap.drawCoordinatesInBound(coordinates: ArrayList<LatLng>, bounds: LatLngBounds, lineManager: LineManager, context: Context) {

    val options = LineOptions()
        .withLatLngs(coordinates)
        .withLineWidth(6f)
        .withLineColor(
            ColorUtils.colorToRgbaString(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
        )
        .withLineOpacity(0.9f)
    lineManager.create(options)

    easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 1000)
}
//endregion

