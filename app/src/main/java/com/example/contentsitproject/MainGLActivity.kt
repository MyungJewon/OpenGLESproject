package com.example.contentsitproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.FileReader
import java.lang.Exception

class MainGLActivity : AppCompatActivity() {
    private lateinit var mainSurfaceView: MainGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        // Create a GLSurfaceView instance and set it
        // as the ContentView fot this Activity.
        mainSurfaceView = MainGLSurfaceView(this)
        setContentView(mainSurfaceView)
    }
}