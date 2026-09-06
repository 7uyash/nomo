package com.nomo.app

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class NomoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize OSMDroid configuration for MapLibre / OpenStreetMap tiles
        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = packageName
        osmConfig.osmdroidBasePath = File(cacheDir, "osmdroid")
        osmConfig.osmdroidTileCache = File(osmConfig.osmdroidBasePath, "tiles")
    }
}
