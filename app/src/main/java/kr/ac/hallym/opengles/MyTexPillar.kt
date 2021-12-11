package kr.ac.hallym.opengles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyTexPillar(private val myContext: Context) {

    private val mvpMatrix=FloatArray(16)
    private val projectionMatrix=FloatArray(16)
    private val viewMatrix=FloatArray(16)
    private val modelMatrix=FloatArray(16)

    private var vertexCoords= floatArrayOf(
         0.5f,3.0f,0.0f,
         0.25f,3.0f,0.4333f,
        -0.25f,3.0f,0.4333f,
        -0.5f,3.0f,0.0f,
        -0.25f,3.0f,-0.4333f,
        0.25f,3.0f,-0.4333f,
        0.5f,3.0f,0.0f,

        0.5f,0.0f,0.0f,
        0.25f,0.0f,0.4333f,
        -0.25f,0.0f,0.4333f,
        -0.5f,0.0f,0.0f,
        -0.25f,0.0f,-0.4333f,
        0.25f,0.0f,-0.4333f,
        0.5f,0.0f,0.0f,

        )
    private val pillarIndex= shortArrayOf(
        0,1,8,0,8,7,
        1,2,9,1,9,8,
        2,3,10,2,10,9,
        3,4,11,3,11,10,
        4,5,12,4,12,11,
        5,6,13,5,13,12
    )
    private var vertexUVs= floatArrayOf(
        0.0f,0.0f,
        0.1667f,0.0f,
        0.3334f,0.0f,
        0.5f,0.0f,
        0.6667f,0.0f,
        0.8334f,0.0f,
        1.0f,0.0f,

        0.0f,1.0f,
        0.1667f,1.0f,
        0.3334f,1.0f,
        0.5f,1.0f,
        0.6667f,1.0f,
        0.8334f,1.0f,
        1.0f,1.0f
    )
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }
    private val indexBuffer:ShortBuffer=
        ByteBuffer.allocateDirect(pillarIndex.size * 2).run{
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(pillarIndex)
                position(0)
            }
        }
    private val uvBuffer: FloatBuffer=
        ByteBuffer.allocateDirect(vertexUVs.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexUVs)
                position(0)
            }
        }
    private val vertexShaderCode =
        "#version 300 es                \n" +
                "uniform mat4 uMVPMatrix;           \n" +
                "layout(location =7) in vec4 vPosition;             \n" +
                "layout(location =8) in vec2 vTexCoord;             \n" +
                "out vec2 fTexCoord;             \n" +
                "void main(){                   \n" +
                "   gl_Position = uMVPMatrix * vPosition;         \n" +
                "   fTexCoord = vTexCoord;    \n" +
                "}                              \n"

    private val fragmentShaderCode =
        "#version 300 es                \n" +
                "precision mediump float;       \n" +
                "uniform sampler2D sTexture;           \n" +
                "in vec2 fTexCoord;      \n"+
                "out vec4 fragColor;            \n" +
                "void main(){                   \n" +
                "   fragColor = texture(sTexture, fTexCoord);            \n" +
                "}                              \n"

    private var mPrograms: Int = -1

    private var mvpMatrixHandle: Int = -1

    private var textureID= IntArray(1)

    private val vertexStride: Int = COORDS_PER_VERTEX * 4   // 4 bytes per vertex

    init{
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mPrograms = GLES30.glCreateProgram().also{

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // create OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }
        GLES30.glUseProgram(mPrograms)

        GLES30.glEnableVertexAttribArray(7)
        GLES30.glVertexAttribPointer(
            7,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(8)
        GLES30.glVertexAttribPointer(
            8,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            uvBuffer
        )
        mvpMatrixHandle=GLES30.glGetUniformLocation(mPrograms,"uMVPMatrix")

        GLES30.glGenTextures(1,textureID,0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR_MIPMAP_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR_MIPMAP_LINEAR)
//        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_MIRRORED_REPEAT)
//        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_MIRRORED_REPEAT)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,loadBitmap("brick.bmp"),0)
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
    }
    private fun loadBitmap(filename:String):Bitmap{
        val manager=myContext.assets
        val inputStream= BufferedInputStream(manager.open(filename))
        val bitmap:Bitmap? = BitmapFactory.decodeStream(inputStream)
        return bitmap!!
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader,shaderCode)
            GLES30.glCompileShader(shader)

            val compiler=ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS,compiler)
            if(compiler.get(0)==0){
                GLES30.glGetShaderiv(shader,GLES30.GL_INFO_LOG_LENGTH,compiler)
                if(compiler.get(0)>1){
                    Log.e("Shader","$type shader: " +GLES30.glGetShaderInfoLog(shader))
                }
                GLES30.glDeleteShader(shader)
                Log.e("Shader","$type shader compile error ")
            }
        }
    }
    fun resize(width:Int,height: Int){
        val ratio: Float = width.toFloat()/height.toFloat()
        Matrix.perspectiveM(projectionMatrix,0,90f,ratio,0.001f,1000f)

        Matrix.setLookAtM(viewMatrix,0,1f,1f,3f,0f,0f,0f,0f,1f,0f)
    }
    fun draw(rotationMatrix:FloatArray){
        GLES30.glUseProgram(mPrograms)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0])
        for(z in -5..5 step(2)){
            Matrix.setIdentityM(modelMatrix,0)
            Matrix.translateM(modelMatrix,0,-2f,-1f,z.toFloat())
            Matrix.multiplyMM(modelMatrix,0,rotationMatrix,0,modelMatrix,0)
            Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0,modelMatrix,0)
            Matrix.multiplyMM(mvpMatrix,0,projectionMatrix,0,mvpMatrix,0)

            GLES30.glUniformMatrix4fv(mvpMatrixHandle,1,false,mvpMatrix,0)
            GLES30.glDrawElements(GLES30.GL_TRIANGLES,pillarIndex.size,GLES30.GL_UNSIGNED_SHORT,indexBuffer)

            Matrix.setIdentityM(modelMatrix,0)

            Matrix.translateM(modelMatrix,0,2f,-1f,z.toFloat())
            Matrix.multiplyMM(modelMatrix,0,rotationMatrix,0,modelMatrix,0)
            Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0,modelMatrix,0)
            Matrix.multiplyMM(mvpMatrix,0,projectionMatrix,0,mvpMatrix,0)

            GLES30.glUniformMatrix4fv(mvpMatrixHandle,1,false,mvpMatrix,0)
            GLES30.glDrawElements(GLES30.GL_TRIANGLES,pillarIndex.size,GLES30.GL_UNSIGNED_SHORT,indexBuffer)
        }
    }


}