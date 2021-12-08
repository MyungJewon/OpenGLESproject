package kr.ac.hallym.opengles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

var drawMode: Int = -1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun drawTriangle(view: View){
        drawMode = 0;
        val intent = Intent(this,MainGLActivity::class.java)
        startActivity(intent)
    }

    fun drawSquare(view: View){
        drawMode = 1;
        val intent = Intent(this,MainGLActivity::class.java)
        startActivity(intent)
    }

    fun drawCube(view: View){
        drawMode = 2;
        val intent = Intent(this,MainGLActivity::class.java)
        startActivity(intent)
    }

    fun drawHexagonalPyramid(view: View){
        drawMode = 3;
        val intent = Intent(this,MainGLActivity::class.java)
        startActivity(intent)
    }
    fun drawTextCube(view: View){
        drawMode = 4;
        val intent = Intent(this,MainGLActivity::class.java)
        startActivity(intent)
    }
    fun drawLitCube(view: View){
        drawMode = 5;
        val intent = Intent(this,MainGLActivity::class.java)
        startActivity(intent)
    }

}