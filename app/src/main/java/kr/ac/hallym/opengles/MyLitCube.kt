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

class MyLitCube(private val myContext: Context) {

    private val mvpMatrix=FloatArray(16)
    private val projectionMatrix=FloatArray(16)
    private val viewMatrix=FloatArray(16)
   // private val modelMatrix=FloatArray(16)
   private var vertexCoords= floatArrayOf(
       -0.5f, 0.5f,-0.5f,
       0.5f, 0.5f,-0.5f,
       0.5f,-0.5f,-0.5f,
       -0.5f, 0.5f,-0.5f,
       0.5f,-0.5f,-0.5f,
       -0.5f,-0.5f,-0.5f,

       -0.5f,-0.5f,0.5f,
       0.5f,-0.5f,0.5f,
       0.5f, 0.5f,0.5f,
       -0.5f,-0.5f,0.5f,
       0.5f, 0.5f,0.5f,
       -0.5f, 0.5f,0.5f,

       -0.5f,-0.5f,-0.5f,
       0.5f,-0.5f,-0.5f,
       0.5f,-0.5f, 0.5f,
       -0.5f,-0.5f,-0.5f,
       0.5f,-0.5f, 0.5f,
       -0.5f,-0.5f, 0.5f,

       0.5f, 0.5f,-0.5f,
       -0.5f, 0.5f,-0.5f,
       -0.5f, 0.5f, 0.5f,
       0.5f, 0.5f,-0.5f,
       -0.5f, 0.5f, 0.5f,
       0.5f, 0.5f, 0.5f,

       0.5f,-0.5f,-0.5f,
       0.5f, 0.5f,-0.5f,
       0.5f, 0.5f, 0.5f,
       0.5f,-0.5f,-0.5f,
       0.5f, 0.5f, 0.5f,
       0.5f,-0.5f, 0.5f,

       -0.5f, 0.5f, 0.5f,
       -0.5f, 0.5f,-0.5f,
       -0.5f,-0.5f,-0.5f,
       -0.5f, 0.5f, 0.5f,
       -0.5f,-0.5f,-0.5f,
       -0.5f,-0.5f, 0.5f,
   )
    private var vertexUVs= floatArrayOf(
        0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,
        0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,
        0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,
        0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,
        0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,
        0.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,1.0f,1.0f,0.0f,
    )
//    private var vertexCoords= floatArrayOf(
//        -0.5f, 0.5f,-0.5f,
//        -0.5f,-0.5f,-0.5f,
//         0.5f,-0.5f,-0.5f,
//         0.5f, 0.5f,-0.5f,
//        -0.5f, 0.5f, 0.5f,
//        -0.5f,-0.5f, 0.5f,
//         0.5f,-0.5f, 0.5f,
//         0.5f, 0.5f, 0.5f,
//    )
    private var vertexNormals= floatArrayOf(
    -0.57735f, 0.57735f,-0.57735f,
     0.57735f, 0.57735f,-0.57735f,
     0.57735f,-0.57735f,-0.57735f,
    -0.57735f, 0.57735f,-0.57735f,
     0.57735f,-0.57735f,-0.57735f,
    -0.57735f,-0.57735f,-0.57735f,

    -0.57735f,-0.57735f,0.57735f,
    0.57735f,-0.57735f,0.57735f,
    0.57735f, 0.57735f,0.57735f,
    -0.57735f,-0.57735f,0.57735f,
    0.57735f, 0.57735f, 0.57735f,
    -0.57735f, 0.57735f, 0.57735f,

    -0.57735f,-0.57735f,-0.57735f,
     0.57735f,-0.57735f,-0.57735f,
     0.57735f,-0.57735f, 0.57735f,
    -0.57735f,-0.57735f,-0.57735f,
     0.57735f,-0.57735f, 0.57735f,
    -0.57735f,-0.57735f, 0.57735f,

     0.57735f, 0.57735f,-0.57735f,
    -0.57735f, 0.57735f,-0.57735f,
    -0.57735f, 0.57735f, 0.57735f,
     0.57735f, 0.57735f,-0.57735f,
    -0.57735f, 0.57735f, 0.57735f,
     0.57735f,-0.57735f, 0.57735f,

     0.57735f,-0.57735f,-0.57735f,
     0.57735f, 0.57735f,-0.57735f,
     0.57735f, 0.57735f, 0.57735f,
     0.57735f,-0.57735f,-0.57735f,
     0.57735f, 0.57735f, 0.57735f,
     0.57735f,-0.57735f, 0.57735f,

    -0.57735f, 0.57735f, 0.57735f,
    -0.57735f, 0.57735f,-0.57735f,
    -0.57735f,-0.57735f,-0.57735f,
    -0.57735f, 0.57735f, 0.57735f,
    -0.57735f,-0.57735f,-0.57735f,
    -0.57735f,-0.57735f, 0.57735f,
    )
//    private var cubeIndex= shortArrayOf(
//        0,1,2,0,2,3,
//        2,3,7,2,7,6,
//        1,2,6,1,6,5,
//        4,0,1,4,1,5,
//        3,0,4,3,4,7,
//        5,6,7,5,7,4
//    )

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }
    private val normalBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexNormals.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexNormals)
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
//    private val indexBuffer: ShortBuffer =
//        ByteBuffer.allocateDirect(cubeIndex.size * 2).run{
//            order(ByteOrder.nativeOrder())
//            asShortBuffer().apply {
//                put(cubeIndex)
//                position(0)
//            }
//        }
    private val vertexShaderCode =
                "#version 300 es                \n" +
                "uniform mat4 uMVPMatrix,uMVMatrix;           \n" +
                        "uniform vec4 uAmbient,uDiffuse, uSpecular;           \n" +
                        "uniform vec4 uLightDir;           \n" +
                        "uniform float uShininess;           \n" +
                "layout(location =0) in vec4 vPosition;             \n" +
                "layout(location =1) in vec3 vNormal;             \n" +
                "out vec4 fNormal;             \n" +
                "void main(){                   \n" +
                        "   gl_Position = uMVPMatrix * vPosition;         \n" +
                        "   vec3 N=normalize(uMVMatrix * vec4(vNormal,0.0)).xyz;    \n" +
                        "   vec3 L=normalize(uLightDir.xyz);    \n" +
                        "   float kd = max(dot(L,N),0.0);    \n" +
                        "   vec3 V = normalize(uMVMatrix * vPosition).xyz;    \n" +
                        "   vec3 H = normalize(L - V).xyz;    \n" +
                        "   float ks=pow(max(dot(N,H),0.0),uShininess);    \n" +
                        "   fNormal=uAmbient + kd * uDiffuse + ks * uSpecular;    \n" +
                "}                              \n"

    private val fragmentShaderCode =
                "#version 300 es                \n" +
                "precision mediump float;       \n" +
                "uniform sampler2D sTexture;           \n" +
                "in vec4 fNormal;      \n"+
                "out vec4 fragColor;            \n" +
                "void main(){                   \n" +
                "   fragColor = fNormal;            \n" +
                "}                              \n"
    private val vertexShaderCodePhong =
                "#version 300 es                \n" +
                "uniform mat4 uMVPMatrix;           \n" +
                "layout(location =0) in vec4 vPosition;             \n" +
                "layout(location =1) in vec2 vTexCoord;             \n" +
                "layout(location =2) in vec3 vNormal;             \n" +
                "out vec4 fPosition;             \n" +
                "out vec2 fTexCoord;             \n" +
                "out vec3 fNormal;             \n" +
                "void main(){                   \n" +
                "   gl_Position = uMVPMatrix * vPosition;         \n" +
                "   fPosition=vPosition;    \n" +
                "   fTexCoord=vTexCoord;    \n" +
                "   fNormal=vNormal;    \n" +
                "}                              \n"

    private val fragmentShaderCodePhong =
        "#version 300 es                \n" +
                "precision mediump float;       \n" +
                "uniform mat4 uMVMatrix;           \n" +
                "uniform vec4 uAmbient, uDiffuse, uSpecular;           \n" +
                "uniform vec3 uLightDir;           \n" +
                "uniform float uShininess;           \n" +
                "uniform sampler2D sTexture;           \n" +
                "in vec4 fPosition;      \n"+
                "in vec2 fTexCoord;            \n" +
                "in vec3 fNormal;            \n" +
                "out vec4 fragColor;            \n" +
                "void main(){                   \n" +
                "   vec3 N=normalize(uMVMatrix * vec4(fNormal,0.0)).xyz;    \n" +
                "   vec3 L=normalize(uLightDir);    \n" +
                "   float kd = max(dot(L,N),0.0);    \n" +
                "   vec3 V = normalize(uMVMatrix * fPosition).xyz;    \n" +
                "   vec3 H = normalize(L - V).xyz;    \n" +
                "   float ks=pow(max(dot(N,H),0.0),uShininess);    \n" +
                "   fragColor=(uAmbient + kd * uDiffuse + ks * uSpecular)* texture(sTexture,fTexCoord);    \n" +
                "}                              \n"

    private var mPrograms: Int = -1
    private var mvpMatrixHandle: Int = -1
    private var mvMatrixHandle: Int = -1
    private var ambientHandle: Int = -1
    private var diffuseHandle: Int = -1
    private var specularHandle: Int = -1
    private var lightDirHandle: Int = -1
    private var shininessHandle: Int = -1

    private var textureID= IntArray(1)

    private val vertexCount: Int = vertexCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4   // 4 bytes per vertex

    init {
//        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
//        val fragmentShader: Int = loadShader
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCodePhong)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCodePhong)

        // create empty OpenGL ES Program
        mPrograms = GLES30.glCreateProgram().also {

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // create OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }
        GLES30.glUseProgram(mPrograms)

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
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            uvBuffer
        )
        GLES30.glEnableVertexAttribArray(2)
        GLES30.glVertexAttribPointer(
            2,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            normalBuffer
        )

        mvpMatrixHandle = GLES30.glGetUniformLocation(mPrograms, "uMVPMatrix")
        mvMatrixHandle=GLES30.glGetUniformLocation(mPrograms,"uMVMatrix")
        ambientHandle=GLES30.glGetUniformLocation(mPrograms,"uAmbient")
        diffuseHandle=GLES30.glGetUniformLocation(mPrograms,"uDiffuse")
        specularHandle=GLES30.glGetUniformLocation(mPrograms,"uSpecular")
        lightDirHandle=GLES30.glGetUniformLocation(mPrograms,"uLightDir")
        shininessHandle=GLES30.glGetUniformLocation(mPrograms,"uShininess")
        GLES30.glGenTextures(1,textureID,0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,loadBitmap("crate.bmp"),0)

        setLighting()
    }

    private fun setLighting(){
        val matAmbient = floatArrayOf(1.0f,1.0f,1.0f,1.0f)
        val matDiffuse = floatArrayOf(1.0f,1.0f,1.0f,1.0f)
        val matSpecular = floatArrayOf(1.0f,0.0f,0.0f,1.0f)
        val matShininess=10.0f

        val ambient= floatArrayOf(
            ambientLight[0]*matAmbient[0],
            ambientLight[1]*matAmbient[1],
            ambientLight[2]*matAmbient[2],1.0f)
        val diffuse= floatArrayOf(
            diffuseLight[0]*matDiffuse[0],
            diffuseLight[1]*matDiffuse[1],
            diffuseLight[2]*matDiffuse[2],1.0f)
        val specular= floatArrayOf(
            specularLight[0]*matSpecular[0],
            specularLight[1]*matSpecular[1],
            specularLight[2]*matSpecular[2],1.0f)

        GLES30.glUniform4fv(ambientHandle,1,ambient,0)
        GLES30.glUniform4fv(diffuseHandle,1,diffuse,0)
        GLES30.glUniform4fv(specularHandle,1,specular,0)

        GLES30.glUniform3fv(lightDirHandle,1,lightDir,0)
        GLES30.glUniform1f(shininessHandle,matShininess)
    }
    private fun loadBitmap(filename:String): Bitmap {
        val manager=myContext.assets
        val inputStream= BufferedInputStream(manager.open(filename))
        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
        return bitmap!!
    }
    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader,shaderCode)
            GLES30.glCompileShader(shader)

            val compiler= ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS,compiler)
            if(compiler.get(0)==0){
                GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH,compiler)
                if(compiler.get(0)>1){
                    Log.e("Shader","$type shader: " + GLES30.glGetShaderInfoLog(shader))
                }
                GLES30.glDeleteShader(shader)
                Log.e("Shader","$type shader compile error ")
            }
        }
    }
    fun resize(width:Int,height: Int){
        val ratio: Float = width.toFloat()/height.toFloat()
        Matrix.perspectiveM(projectionMatrix,0,90f,ratio,0.001f,1000f)

        Matrix.setLookAtM(viewMatrix,0,1f,1f,2f,0f,0f,0f,0f,1f,0f)
    }
    fun draw(rotationMatrix:FloatArray){

        Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0, rotationMatrix,0)

        GLES30.glUniformMatrix4fv(mvMatrixHandle,1,false,mvpMatrix,0)
        Matrix.multiplyMM(mvpMatrix,0,projectionMatrix,0,mvpMatrix,0)
        GLES30.glUseProgram(mPrograms)

        //Pass the projection and view transformation t the shader
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0])
        //Draw the square
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,vertexCount)
    }
}