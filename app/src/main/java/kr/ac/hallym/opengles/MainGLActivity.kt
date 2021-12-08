package kr.ac.hallym.opengles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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