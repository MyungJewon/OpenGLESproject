package kr.ac.hallym.opengles

import android.opengl.GLES30
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.core.graphics.rotationMatrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyCube {

    private val mvpMatrix=FloatArray(16)
    private val projectionManager=FloatArray(16)
    private val viewMatrix=FloatArray(16)
    private val modelMatrix=FloatArray(16)

    private var vertexCoords = floatArrayOf(
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f
    )

    private val vertexColors = floatArrayOf(
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f
    )

    private val cubeIndex = shortArrayOf(
        0, 1, 2, 0, 2, 3,
        2, 3, 7, 2, 7, 6,
        1, 2, 6, 1, 6, 5,
        4, 0, 1, 4, 1, 5,
        3, 0, 4, 3, 4, 7,
        5, 6, 7, 5, 7, 4
    ) // order to draw vertices

    //initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
        // (# of coodinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }

    //initialize byte buffer for the draw list
    private val colorBuffer: FloatBuffer =
        // (# of coodinate values * 4 bytes per short)
        ByteBuffer.allocateDirect(vertexColors.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexColors)
                position(0)
            }
        }

    private var indexBuffer: ShortBuffer =
        // (numberof coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(cubeIndex.size * 4).run {
            //use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            //create a floating point buffer from the ByteBuffer
            asShortBuffer().apply {
                //add the coordinates to the FloatBuffer
                put(cubeIndex)
                // set the buffer to read the first coordinates
                position(0)
            }
        }

    private val vertexShaderCode =
        "#version 300 es \n" +
                "uniform mat4 uMVPMatrix;\n" +
                "layout(location = 3) in vec4 vPosition; \n" +
                "layout(location = 4) in vec4 vColor; \n" +
                "out vec4 fColor; \n" +
                "void main(){ \n" +
                "   gl_Position = uMVPMatrix * vPosition; \n" +
                "   fColor = vColor; \n" +
                "}\n"
    private val fragmentShaderCode =
        "#version 300 es \n" +
                "precision mediump float; \n"+
                "in vec4 fColor; \n"+
                "out vec4 fragColor; \n"+
                "void main(){ \n" +
                "   fragColor = fColor; \n"+
                "} \n"

    private var mProgram: Int = -1

    // Use to access and set the view transformation
    private var mvpMatrixHandle: Int = -1

    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init{
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        //create emptY OpenGL ES Program
        mProgram = GLES30.glCreateProgram().also{

            //add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            //add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }

        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mProgram)

        //get handle to vertex shader's vPosition member
        //mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {

        //Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(3)

        //Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            3,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        //}

        //get handle to fragment shader's vColor member
        //mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also{

        //Set color for drawing the triangle
        //GLES30.glUniform4fv(it, 1, color, 0)
        //}

        GLES30.glEnableVertexAttribArray(4)

        // Prepare the square coordinate data
        GLES30.glVertexAttribPointer(
            4,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )

        //get handle to shape's transformation matrix
        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    private fun loadShader(type: Int, shaderCode: String): Int{

        //create a vertex shader type( GLES30.GL_VERTEX_SHADER)
        //or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        return GLES30.glCreateShader(type).also { shader ->

            //add the source code to the shader and compile it
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

            // log the compile error
            val compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled)
            if(compiled.get(0) == 0){
                GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled)
                if(compiled.get(0) > 1){
                    Log.e("Shader", "$type shader: " + GLES30.glGetShaderInfoLog(shader))
                }
                GLES30.glDeleteShader(shader)
                Log.e("Shader", "$type shader compile error cube.")
            }
        }
    }
    fun resize(width:Int,height: Int){
        val ratio: Float = width.toFloat()/height.toFloat()
        Matrix.perspectiveM(projectionManager,0,90f,ratio,0.001f,1000f)

        Matrix.setLookAtM(viewMatrix,0,1f,1f,1f,0f,0f,0f,0f,1f,0f)
    }
    fun draw(rotationMatrix:FloatArray){

        val time=SystemClock.uptimeMillis() % 4000L
        val angle=0.090f*time.toInt()
        Matrix.setRotateM(modelMatrix,0,angle,1f,0f,0f)

        Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0,modelMatrix,0)
        Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0, rotationMatrix,0)

        //Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0,mvpMatrix,0)
        Matrix.multiplyMM(mvpMatrix,0,projectionManager,0,mvpMatrix,0)
        GLES30.glUseProgram(mProgram)

        //Pass the projection and view transformation t the shader
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        //Draw the square
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, cubeIndex.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}