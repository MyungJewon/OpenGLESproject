package com.example.contentsitproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import java.io.*
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Files.write
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    fun fileread(view: View){
//        val path=filesDir.toString()+"/tinker.obj"
//        val bufferedReader: BufferedReader=File(path).bufferedReader()
//        var inputString= bufferedReader.use { it.readText() }
//        var objlist= ArrayList<String>()
//
//        val obj=inputString.split("\n")
//        for(i in obj){
//            if(i.contains("v ") ) {
//                val j = i.replace("v ", "")
//                val k= j.split(" ")
//                for(q in k)
//                    objlist.add(q.toFloat().toString()+"f")
//            }
//        }
//        val intentdata = Intent(this@MainActivity,MyTextCube::class.java)
//        intentdata.putExtra("obj",objlist)
//
//        //       Log.d("Log",objlist.toString())
//        val memo:String =objlist.toString()
//        var outputFile : FileOutputStream = openFileOutput("test.txt", MODE_PRIVATE)
//        outputFile.write(memo.toByteArray())	//memo : String DATA
//        outputFile.close()

        val intent = Intent(this@MainActivity,MainGLActivity::class.java)
        startActivity(intent)
    }
}