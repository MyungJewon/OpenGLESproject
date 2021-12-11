package kr.ac.hallym.opengles

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyCube {

    private val mvpMatrix=FloatArray(16)
    private val projectionManager=FloatArray(16)
    private val viewMatrix=FloatArray(16)
    private val vertexBuffer: FloatBuffer =
        // (# of coodinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(objvertex1.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(objvertex1)
                position(0)
            }
        }
    private val colorBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(objcolor1.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(objcolor1)
                position(0)
            }
        }

    private var indexBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(objface1.size * 4).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(objface1)
                position(0)
            }
        }

    private val vertexShaderCode =
        "#version 300 es \n" +
                "uniform mat4 uMVPMatrix;\n" +
                "layout(location = 0) in vec4 vPosition; \n" +
                "layout(location = 1) in vec4 vColor; \n" +
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
    private var mvpMatrixHandle: Int = -1
    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init{
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES30.glCreateProgram().also{
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        GLES30.glUseProgram(mProgram)

        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(
            0,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glVertexAttribPointer(
            1,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    private fun loadShader(type: Int, shaderCode: String): Int{

        return GLES30.glCreateShader(type).also { shader ->

            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

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

        Matrix.setLookAtM(viewMatrix,0,1f,1f,100f,0f,0f,0f,0f,1f,0f)
    }
    fun draw(rotationMatrix:FloatArray){
        Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0, rotationMatrix,0)

        Matrix.multiplyMM(mvpMatrix,0,projectionManager,0,mvpMatrix,0)
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, objface1.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}