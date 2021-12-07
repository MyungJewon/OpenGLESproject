package com.example.contentsitproject

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class MainGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val mainRenderer: MainGLRenderer

    init {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3)

        mainRenderer = MainGLRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mainRenderer)

        // Render the view only when there is a change in the drawing data
        //renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return mainRenderer.onTouchEvent(e)
    }
}