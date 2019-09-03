package ir.parsgeeks.parkingapp

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.mapbox.mapboxsdk.geometry.LatLng
import org.json.JSONObject

class FindParkPlacePresenter : BasePresenter<FindParkPlacePresenter.View>() {

    var selectedParkingPlace: ParkingPlace? = null

    fun setDestination(lat: Double, lon: Double,isForDisabled:Boolean) {
        view?.showLoading()
        AndroidNetworking.get(CONSTS.PARKING_API_URL + "closest")
            .addQueryParameter("lat",lat.toString())
            .addQueryParameter("lon",lon.toString())
            .addQueryParameter("is_for_disabled",isForDisabled.toString())
            .setTag("ClosestParking")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (!response!!.optBoolean("result", false)) {
                        selectedParkingPlace = null
                        view?.showError(response.optString("message", ""))
                    } else {
                        selectedParkingPlace = ParkingPlace(
                            response.optString("_id", ""),
                            response.optDouble("lat"),
                            response.optDouble("lon"),
                            response.optBoolean("is_for_disabled", false),
                            response.optBoolean("is_available")
                        )
                        view?.addClosestParkingPin(selectedParkingPlace!!)
                    }
                }

                override fun onError(anError: ANError?) {
                    selectedParkingPlace = null
                    view?.showError(anError?.errorDetail!!)
                }
            })
    }


    fun agreedWithParkingLocation() {
        view?.showLoading()
        AndroidNetworking.post(CONSTS.PARKING_API_URL+"")

            .setTag("AgreedWithParking")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (!response!!.optBoolean("result", false))
                        view?.showError(response.optString("message", ""))
                    else {
                        view?.drawDirectionToParkingPlace(selectedParkingPlace!!)
                    }
                }

                override fun onError(anError: ANError?) {
                    view?.showError(anError?.errorDetail!!)
                }
            })

    }

    fun userReachTheParkingLocation() {
        view?.done()
    }

    interface View {
        fun showError(text: String)
        fun showLoading()
        fun addClosestParkingPin(pp: ParkingPlace)
        fun drawDirectionToParkingPlace(parkingPlace: ParkingPlace)
        fun done()
    }

}