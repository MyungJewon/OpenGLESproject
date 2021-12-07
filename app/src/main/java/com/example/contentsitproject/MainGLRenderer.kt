package com.example.contentsitproject

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
//import android.opengl.Matrix
import android.view.MotionEvent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainGLRenderer(private val myContext: Context): GLSurfaceView.Renderer{

//    private lateinit var mTriangle: MyTriangle
//    private lateinit var mSquare: MySquare
//    private lateinit var mCube: MyCube
//    private lateinit var mHexagonalPyramid: MyHexagonalPyramid
    private lateinit var mTrackball: MyTrackball
    private lateinit var mTexCube: MyTextCube
    
 //   private val mvpMatrix = FloatArray(16)
   // private val projectionMatrix = FloatArray(16)
   // private val viewMatrix = FloatArray(16)


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES30.glClearColor(0.2f, 0.2f, 0.2f, 1.0f)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        mTexCube = MyTextCube(myContext)


        // initialize a trianlge
        mTrackball= MyTrackball()
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        // Set the camera position (View matrix)
        //Matrix.setLookAtM(viewMatrix, 0, 0f, 2.5f,  3.5f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Calculate the projection and view transformation
        //Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)


         mTexCube.draw(mTrackball.rotationMatrix)


    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // 좌하단 (0,0), 캔버스 넓이를 width, height) (전체)
        GLES30.glViewport(0, 0, width, height)

        // this projection matrix is applied to object coordinates in the onDrawFrame() method
        /*if (width > height){
            val ratio: Float = width.toFloat() / height.toFloat()
            //Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0f, 1000f)
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0.5f, 1000f)
        }
        else{
            val ratio: Float = height.toFloat() / width.toFloat()
            //Matrix.orthoM(projectionMatrix, 0, -1f, 1f,-ratio, ratio, 0f, 1000f)
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f,-ratio, ratio, 0f, 1000f)
        }
        
        //val ratio: Float = width.toFloat()/height.toFloat()
        //Matrix.perspectiveM(projectionMatrix, 0, 60f, ratio, .5f, 1000f)*/

        mTexCube.resize(width,height)
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