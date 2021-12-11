package kr.ac.hallym.opengles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

var drawMode: Int = -1
val objvertex= ArrayList<Float>()
val objface=ArrayList<Short>()

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

        var objscr:String=filesDir.toString()+"/tinker.obj"
        var objread:String=File(objscr).bufferedReader().use { it.readText()}
        val objlist=objread.split("\n")

        for(i in objlist){
            if(i.indexOf("v")==0){
                val j = i.replace("v ", "")
                val k= j.split(" ")
                for(q in k)
                    objvertex.add(q.toFloat())
            }
            else if(i.indexOf("f")==0){
                val j = i.replace("f ", "")
                val k= j.split(" ")
                for(q in k)
                    objface.add(q.toFloat().toInt().toShort())
            }
        }
//        Log.d("Log1", objvertex.toString()+"f")
//        Log.d("Log2", objface.toString())

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