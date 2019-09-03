package ir.parsgeeks.parkingapp

import android.content.Context
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.mapbox.mapboxsdk.geometry.LatLng
import org.json.JSONObject

class AddParkPlacePresenter : BasePresenter<AddParkPlacePresenter.View>() {

    fun addPlace(context: Context,lat: Double, lon: Double, isForDisabled: Boolean) {
        view?.showLoading()
        AndroidNetworking.post(CONSTS.PARKING_API_URL+"new")
            .addBodyParameter("username", SharedPreferencesHelper.getUserData(context).username)
            .addBodyParameter("lat", lat.toString())
            .addBodyParameter("lon", lon.toString())
            .addBodyParameter("is_for_disabled", isForDisabled.toString())
            .setTag("addPlace")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (!response!!.optBoolean("result", false))
                        view?.showError(response.optString("message", ""))
                    else {
                        view?.placeAdded()
                    }
                }

                override fun onError(anError: ANError?) {
                    view?.showError(anError?.errorDetail!!)
                }
            })
    }

    fun pinLocation(latLng: LatLng) {
        view?.showOptions(latLng)
    }

    interface View {
        fun showError(text: String)
        fun showLoading()
        fun showOptions(latLng: LatLng)
        fun placeAdded()
    }

}