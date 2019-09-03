package ir.parsgeeks.parkingapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.cedarstudios.cedarmapssdk.CedarMapsStyle
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_add_park_place.*


class AddParkPlaceActivity : AppCompatActivity() , AddParkPlacePresenter.View{

    private val presenter: AddParkPlacePresenter = AddParkPlacePresenter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_park_place)

        presenter.attachView(this)

        mapView.getMapAsync {
            CedarMapsStyleConfigurator.configure(
                CedarMapsStyle.VECTOR_LIGHT, object : OnStyleConfigurationListener {
                    override fun onSuccess(styleBuilder: Style.Builder) {
                        it.setStyle(styleBuilder)
                    }

                    override fun onFailure(errorMessage: String) {
                    }

                })
            it.addOnMapLongClickListener {latlng ->
                it.addMarkerToMapViewAtPosition(latlng,resources)
                presenter.pinLocation(latlng)
                true
            }
        }
    }



    override fun showError(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE

    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun showOptions(latLng: LatLng) {
        progressBar.visibility = View.GONE
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(R.string.is_for_disabled)
        alertDialog.setMessage(R.string.is_for_disabled_description)

        alertDialog.setPositiveButton(R.string.yes) { dialog, which ->
            presenter.addPlace(applicationContext,latLng.latitude,latLng.longitude,true)
        }

        alertDialog.setNegativeButton(R.string.no) { dialog, which ->
            presenter.addPlace(applicationContext,latLng.latitude,latLng.longitude,false)
        }

        alertDialog.setCancelable(false)

        alertDialog.show()
    }

    override fun placeAdded() {
        progressBar.visibility = View.GONE
        onBackPressed()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

}
