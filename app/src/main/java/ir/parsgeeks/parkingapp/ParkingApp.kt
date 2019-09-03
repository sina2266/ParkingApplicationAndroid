package ir.parsgeeks.parkingapp

import android.app.Application
import com.cedarstudios.cedarmapssdk.CedarMaps

class ParkingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CedarMaps.getInstance()
            .setClientID("sinamirshafiee-2510761315293476502")
            .setClientSecret("cDDZC3NpbmFtaXJzaGFmaWVlG0KwDa_qHXphB42vQ9IHRzZJeCEgbkh0_ZYENkK3Ov4=")
            .setContext(applicationContext)
    }
}