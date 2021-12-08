package kr.ac.hallym.opengles

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MySquare {
    private var squareCoords = floatArrayOf(
        -0.75f, 0.75f, 0.0f,
        -0.75f, -0.75f, 0.0f,
        0.75f, -0.75f, 0.0f,
        0.75f, 0.75f, 0.0f
    )

    private var squareColors = floatArrayOf(
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    private val vertexBuffer: FloatBuffer =

        ByteBuffer.allocateDirect(squareCoords.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    private val colorBuffer: FloatBuffer =

        ByteBuffer.allocateDirect(squareColors.size * 4).run{
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareColors)
                position(0)
            }
        }

    private val drawListBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run{
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexShaderCode =
                "#version 300 es\n"+
                "layout(location = 1) in vec4 vPosition;\n"+
                "layout(location = 2) in vec4 vColor;\n"+
                "out vec4 fColor;\n"+
                "void main() {\n"+
                "    gl_Position = vPosition;\n"+
                "fColor = vColor;\n"+
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
        GLES30.glEnableVertexAttribArray(1)
        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
                1,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )


        // get handle to fragment shader's vColor member
        GLES30.glEnableVertexAttribArray(2)

        GLES30.glVertexAttribPointer(
            2,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )
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

        GLES30.glUseProgram(mProgram)

        // Draw the triangle
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, drawListBuffer)
    }

}