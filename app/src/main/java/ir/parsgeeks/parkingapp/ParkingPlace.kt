package ir.parsgeeks.parkingapp

import com.mapbox.mapboxsdk.geometry.LatLng

data class ParkingPlace(var id: String? ,
                        var lat:Double,
                        var lon:Double,
                        var isForDisabled:Boolean?,
                        var isAvailable:Boolean?) {
    fun getLatLng():LatLng{
        return LatLng(lat,lon)
    }
}