package kr.ac.hallym.opengles

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
//import android.opengl.Matrix
import android.view.MotionEvent
import androidx.core.graphics.rotationMatrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

var lightDir = floatArrayOf(1.0f,1.0f,1.0f)
var ambientLight = floatArrayOf(0.2f,0.2f,0.2f,1.0f)
var diffuseLight = floatArrayOf(1.0f,1.0f,1.0f,1.0f)
val specularLight = floatArrayOf(1.0f,1.0f,1.0f,1.0f)
const val COORDS_PER_VERTEX=3
class MainGLRenderer(private val myContext: Context): GLSurfaceView.Renderer{
    private lateinit var mCube: MyCube
    private lateinit var mTrackball: MyTrackball
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.2f, 0.2f, 0.2f, 1.0f)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        when(drawMode){
            2 -> mCube = MyCube()
        }
        mTrackball= MyTrackball()
    }

    override fun onDrawFrame(unused: GL10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        when(drawMode) {
            2 -> mCube.draw(mTrackball.rotationMatrix)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        when(drawMode){
            2->mCube.resize(width,height)
        }
        mTrackball.resize(width,height)
    }

    fun onTouchEvent(e:MotionEvent):Boolean{

        val x:Int=e.x.toInt()
        val y:Int=e.y.toInt()

        when(e.action){
            MotionEvent.ACTION_DOWN->{
                mTrackball.start(x,y)
            }
            MotionEvent.ACTION_MOVE->{
                mTrackball.end(x,y)
            }
        }
        return true
    }
}