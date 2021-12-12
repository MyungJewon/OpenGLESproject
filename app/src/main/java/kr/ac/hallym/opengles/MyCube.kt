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
    private val modelMatrix=FloatArray(16)
    private val objvertex1=objvertex.toFloatArray()
    private val objcolor1=objcolor.toFloatArray()
    private val objface1=objface.toShortArray()
    private val objnomal1= objnomal.toFloatArray()
    private val vertexBuffer: FloatBuffer =
        // (# of coodinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(objvertex1.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(objvertex1)
                position(0)
            }
        }
    private val normalBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(objnomal1.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(objnomal1)
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
                "   fragColor=(uAmbient + kd * uDiffuse + ks * uSpecular);    \n" +
                "}                              \n"

    private var mProgram: Int = -1

    private var mvpMatrixHandle: Int = -1
    private var mvMatrixHandle: Int = -1
    private var ambientHandle: Int = -1
    private var diffuseHandle: Int = -1
    private var specularHandle: Int = -1
    private var lightDirHandle: Int = -1
    private var shininessHandle: Int = -1

    private val vertexCount: Int = objvertex1.size / COORDS_PER_VERTEX
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
        mvMatrixHandle=GLES30.glGetUniformLocation(mProgram,"uMVMatrix")
        ambientHandle=GLES30.glGetUniformLocation(mProgram,"uAmbient")
        diffuseHandle=GLES30.glGetUniformLocation(mProgram,"uDiffuse")
        specularHandle=GLES30.glGetUniformLocation(mProgram,"uSpecular")
        lightDirHandle=GLES30.glGetUniformLocation(mProgram,"uLightDir")
        shininessHandle=GLES30.glGetUniformLocation(mProgram,"uShininess")

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
        Matrix.setIdentityM(modelMatrix,0)
        Matrix.multiplyMM(mvpMatrix,0,viewMatrix,0, rotationMatrix,0)

        GLES30.glUniformMatrix4fv(mvMatrixHandle,1,false,mvpMatrix,0)
        Matrix.multiplyMM(mvpMatrix,0,projectionManager,0,mvpMatrix,0)
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, objface1.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}