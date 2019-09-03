package ir.parsgeeks.parkingapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import android.R.attr.colorPrimary
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.utils.ColorUtils.colorToRgbaString
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import android.widget.Toast
import androidx.annotation.NonNull
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting
import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener
import com.cedarstudios.cedarmapssdk.CedarMaps
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.utils.ColorUtils


//region GPS Helper
class GPSTracker(private val mContext: Context) : LocationListener {
    // flag for GPS status
    var isGPSEnabled = false
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null
    // flag for network status
    internal var isNetworkEnabled = false
    // flag for GPS status
    internal var canGetLocation = false
    internal var location: Location? = null // location
    internal var latitude = 0.0 // latitude
    internal var longitude = 0.0 // longitude

    init {
        getLocation()
    }

    /**
     * Function to get the user's current location
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            locationManager = mContext
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)


            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    location = null
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    location = null
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle(R.string.gps_disable)
        alertDialog.setMessage(R.string.gps_disable_go_to_settings)

        alertDialog.setPositiveButton(R.string.settings,
            DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            })

        alertDialog.setCancelable(false)

        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {

        // The minimum distance to change Updates in meters
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters
        // The minimum time between updates in milliseconds
        private val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute
    }

}
//endregion

//region CONST Helper
class CONSTS {
    companion object {
        val BASE_URL = "http://localhost:3001"
        val PARKING_API_URL = "$BASE_URL/v1.0/parking/"
    }
}
//endregion

//region SharedPreferences Helper
object SharedPreferencesHelper {
    private val SP_USER_DATA = "SPUserData"


    fun getUserData(context: Context): UserData {
        return UserData(context)
    }


    class UserData (context: Context) {

        private val SP: SharedPreferences

        var username: String?
            get() = SP.getString(KEY_USERNAME, null)
            set(username) = SP.edit().putString(KEY_USERNAME, username).apply()

        init {
            SP = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE)
        }

        fun clear() {
            SP.edit().clear().apply()
        }

        companion object {
            private val KEY_USERNAME = SP_USER_DATA + "username"
        }


    }
}

//endregion



//region Other Helper
//endregion

