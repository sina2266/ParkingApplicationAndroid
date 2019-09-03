package ir.parsgeeks.parkingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!GPSTracker(applicationContext).canGetLocation)
            GPSTracker(this).showSettingsAlert()

        findViewById<View>(R.id.activity_main__add_btn).setOnClickListener {
            startActivity(Intent(applicationContext,AddParkPlaceActivity::class.java))
        }

        findViewById<View>(R.id.activity_main__find_btn).setOnClickListener {
            startActivity(Intent(applicationContext,FindParkPlaceActivity::class.java))
        }
    }
}
