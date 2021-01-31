package org.techtown.runningmate

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MapService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("service", "mapService")
        val dialogIntent = Intent(this, MapView::class.java)
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(dialogIntent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}