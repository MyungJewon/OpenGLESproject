package kr.ac.hallym.opengles

import android.opengl.GLES30
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.sqrt

class MyHexagonalPyramid {
    private val mvpMatrix=FloatArray(16)
    private val projectionManager=FloatArray(16)
    private val viewMatrix=FloatArray(16)
    private val modelMatrix=FloatArray(16)

    private var vertexCoords = floatArrayOf(
        /*
        //0
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        //1
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f
        */
        0f,1f,0f,                       //w
        -0.5f,1f,-sqrt(3f)/2f,        //b
        -1f,1f,0f,                      //c
        -0.5f,1f,sqrt(3f)/2f,         //g
        0.5f,1f,sqrt(3f)/2f,          //y
        1f,1f,0f,                       //r
        0.5f,1f,-sqrt(3f)/2f,         //m
        0f,-1f, 0f                        //b
    )

    private var vertexColors = floatArrayOf(
        //0
        //wgcbmryb
        1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f
        //1
        /*
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        */

    )

    private val drawOrder = shortArrayOf(
        /*
        0, 1, 2, 0, 2, 3,   // back
        2, 3, 7, 2, 7, 6,   // right-side
        1, 2, 6, 1, 6, 5,   // bottom
        4, 0, 1, 4, 1, 5,   // left_side
        3, 0, 4, 3, 4, 7,   // top
        5, 6, 7, 5, 7, 4    //front
        */
        //wgcbmryb
        0,1,2,
        0,2,3,
        0,3,4,
        0,4,5,
        0,5,6,
        0,6,1,
        7,1,2,
        7,2,3,
        7,3,4,
        7,4,5,
        7,5,6,
        7,6,1

    )

    private val vertexBuffer: FloatBuffer =

        ByteBuffer.allocateDirect(vertexCoords.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }

    private val colorBuffer: FloatBuffer =

        ByteBuffer.allocateDirect(vertexColors.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexColors)
                position(0)
            }
        }

    private val indexBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run{
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexShaderCode =
        "#version 300 es\n"+
                "uniform mat4 uMVPMatrix;\n"+
                "layout(location = 3) in vec4 vPosition;\n"+
                "layout(location = 4) in vec4 vColor;\n"+
                "out vec4 fColor;\n"+
                "void main(){\n"+
                "   gl_Position = uMVPMatrix * vPosition;\n"+
                "   fColor = vColor;\n"+
                "}"

    private val fragmentShaderCode =
        "#version 300 es\n"+
                "precision mediump float;\n"+
                "in vec4 fColor;\n"+
                "out vec4 fragColor;\n"+
                "void main() {\n"+
                "    fragColor = fColor;\n"+
                "}"

    private var mProgram: Int = -1

    private var mvpMatrixHandle: Int = -1

    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram().also{

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // create OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }

        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mProgram)



        // Enable a handle to the triangle vertivcles
        GLES30.glEnableVertexAttribArray(3)
        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            3,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )


        // get handle to fragment shader's vColor member
        GLES30.glEnableVertexAttribArray(4)

        GLES30.glVertexAttribPointer(
            4,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    private fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex Shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        return GLES30.glCreateShader(type).also{ shader ->

            // add the source code to the shader and compile it
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

            // log the compile error
            val compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled)
            if(compiled.get(0) == 0){
                GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled)
                if(compiled.get(0) > 1) {
                    Log.e("Shader", "$type shader: " + GLES30.glGetShaderInfoLog(shader))
                }
                GLES30.glDeleteShader(shader)
                Log.e("Shader", "$type shader compile error.")
            }
        }
    }
    fun resize(width:Int,height:Int){
        val ratio :Float =width.toFloat()/height.toFloat()
        Matrix.perspectiveM(projectionManager,0,90f,ratio,0.001f,1000f)

        Matrix.setLookAtM(viewMatrix,0,1f,1f,1f,0f,0f,0f,0f,1f,0f)
    }
    fun draw(rotationMatrix:FloatArray){
        Matrix.setIdentityM(modelMatrix,0)
        Matrix.translateM(modelMatrix, 0,0f,0f,-0.5f)

        val tempMatrix= floatArrayOf(1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f)
        Matrix.scaleM(tempMatrix,0,0.5f,0.5f,2f)
        Matrix.multiplyMM(modelMatrix,0,tempMatrix,0,modelMatrix,0)

        Matrix.setRotateM(modelMatrix,0,-90f,1f,0f,0f)
        Matrix.multiplyMM(modelMatrix,0,tempMatrix,0,modelMatrix,0)

        //Matrix.setRotateM(modelMatrix,0,angle,1f,0f,0f)

        val time= SystemClock.uptimeMillis()%4000L
        val angle= 0.090f*time.toInt()
        Matrix.setRotateM(tempMatrix,0,angle,0f,1f,0f)
        //Matrix.multiplyMM(modelMatrix,0,tempMatrix,0,modelMatrix,0)

        Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0,modelMatrix,0)
        Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0, rotationMatrix,0)
        Matrix.multiplyMM(mvpMatrix,0,projectionManager,0,mvpMatrix,0)

        //Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0,mvpMatrix,0)
        GLES30.glUseProgram(mProgram)

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the triangle
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }

}