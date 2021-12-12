package kr.ac.hallym.opengles

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import java.io.File

var drawMode: Int = -1
val objvertex= ArrayList<Float>()
val objface=ArrayList<Short>()
val objcolor=ArrayList<Float>()
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.listView)
        val path = File(filesDir.toString())
        val files = path.listFiles()
        val strFileList = ArrayList<String>()
        val fileSize = ArrayList<String>()
        for(file in files){
            if(file.name.contains(".obj"))
                strFileList +=file.name
            fileSize +=file.length().toString()
        }
//        어답터 설정
        listView.adapter = MyCustomAdapter(this,strFileList,fileSize)

        listView.onItemClickListener=AdapterView.OnItemClickListener{parent, view, position, id ->
        val selectItem = parent.getItemAtPosition(position) as String
        drawCube(selectItem)
        }
    }

    fun drawCube(view: String){
        drawMode=2
        objvertex.clear()
        objface.clear()
        objcolor.clear()
        var objscr:String=filesDir.toString()+"/"+view
        var objread:String=File(objscr).bufferedReader().use { it.readText()}
        val objlist=objread.split("\n")
        Log.d("Log1",objscr)
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
                    objface.add((q.toFloat().toInt()-1).toShort())
            }
        }
        for(i in 0 until objvertex.size)
            objcolor.add(Math.random().toFloat())
        val intent = Intent(this,MainGLActivity::class.java)
        startActivity(intent)

    }
}
    private class MyCustomAdapter(context: Context, strFileList: ArrayList<String>,fileSize: ArrayList<String>) : BaseAdapter() {
        private val mContext: Context
        val list=strFileList
        val size=fileSize
    init {
        mContext = context
    }
    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItem(position: Int): Any {
        val selectItem = list.get(position)
        return selectItem
    }
    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val rowMain = layoutInflater.inflate(R.layout.row_main, viewGroup, false)

        val nameTextView = rowMain.findViewById<TextView>(R.id.name_textview)
        nameTextView.text = list.get(position)
        val positionTextView = rowMain.findViewById<TextView>(R.id.position_textview)
        positionTextView.text = "용량 : "+size.get(position)+"Byte"

        return rowMain
    }

}