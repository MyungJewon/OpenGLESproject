package kr.ac.hallym.opengles

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3

class MyTriangle {

    private val triangleCoords = floatArrayOf(
        // 이등변삼각형
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    private val color = floatArrayOf( 1.0f, 1.0f, 0.0f, 1.0f )

    private val vertexBuffer: FloatBuffer =
        // (number of coordinate values = 4 bytes per float
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run{
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer form the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }

        }

    private val vertexShaderCode =
        "#version 300 es                \n" +
                "in vec4 vPosition;             \n" +
                "void main(){                   \n" +
                "   gl_PointSize = 5.0;         \n" +
                "   gl_Position = vPosition;    \n" +
                "}                              \n"

    private val fragmentShaderCode =
        "#version 300 es                \n" +
                "precision mediump float;       \n" +
                "uniform vec4 vColor;           \n" +
                "out vec4 fragColor;            \n" +
                "void main(){                   \n" +
                "fragColor = vColor;            \n" +
                "}                              \n"

    private var mPrograms: Int = -1

    private var mPositionHandle: Int = -1
    private var mColorHandle: Int = -1

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
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

        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mPrograms)

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mPrograms, "vPosition").also{

            // Enable a handle to the triangle vertivcles
            GLES30.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES30.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        }

        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mPrograms, "vColor").also{

            // Set color for drawing the triangle
            GLES30.glUniform4fv(it, 1, color, 0)
        }
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

    fun draw(){
        GLES30.glUseProgram(mPrograms)

        // Draw the triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}