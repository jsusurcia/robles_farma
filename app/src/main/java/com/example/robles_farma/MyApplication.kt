package com.example.robles_farma

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder

class MyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }

    companion object {
        // 'lateinit' promete que inicializaremos esto antes de usarlo
        private lateinit var appContext: Context

        // ⚠️ IMPORTANTE: Esta anotación hace que Java vea este método como "static"
        @JvmStatic
        fun getAppContext(): Context {
            return appContext
        }
    }
}