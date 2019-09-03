package ir.parsgeeks.parkingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.cedarstudios.cedarmapssdk.CedarMaps
import com.cedarstudios.cedarmapssdk.CedarMapsStyle
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator
import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_find_park_place.*
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager

import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class FindParkPlaceActivity : AppCompatActivity(), FindParkPlacePresenter.View {

    private val presenter: FindParkPlacePresenter = FindParkPlacePresenter()
    private var symbolManager: SymbolManager?= null
    private var lineManager: LineManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_park_place)

        presenter.attachView(this)

        mapView.getMapAsync {
            CedarMapsStyleConfigurator.configure(
                CedarMapsStyle.VECTOR_LIGHT, object : OnStyleConfigurationListener {
                    override fun onSuccess(styleBuilder: Style.Builder) {
                        it.setStyle(styleBuilder) { style ->

                            symbolManager = SymbolManager(mapView, it, style)

                            lineManager = LineManager(mapView, it, style)
                        }
                    }

                    override fun onFailure(errorMessage: String) {
                    }

                })


            it.addOnMapClickListener { point ->
                presenter.setDestination(point.latitude, point.longitude,false)
                true
            }
        }

        drawDirectionBtn.setOnClickListener {
            presenter.agreedWithParkingLocation()
        }

    }


    override fun showError(text: String) {
        gpDirectionBtn.visibility = View.GONE
        progressbar.visibility = View.GONE
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    override fun showLoading() {
        gpDirectionBtn.visibility = View.VISIBLE
        progressbar.visibility = View.VISIBLE
        drawDirectionTxt.visibility = View.GONE
    }

    override fun addClosestParkingPin(pp: ParkingPlace) {
        gpDirectionBtn.visibility = View.VISIBLE
        drawDirectionTxt.visibility = View.VISIBLE
        progressbar.visibility = View.GONE
        mapView.getMapAsync {
            it.addMarkerToMapViewAtPosition(pp.getLatLng(),resources)
        }
    }

    override fun drawDirectionToParkingPlace(parkingPlace: ParkingPlace) {
        gpDirectionBtn.visibility = View.GONE
        progressbar.visibility = View.GONE
        mapView.getMapAsync {
            it.computeDirection(LatLng(GPSTracker(applicationContext).latitude,GPSTracker(applicationContext).longitude),parkingPlace.getLatLng(),lineManager!!,applicationContext)
        }
    }

    override fun done() {
        onBackPressed()
    }




}
