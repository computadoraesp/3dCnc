package com.example.cnc3d.ui.preview3d

import android.opengl.GLES10
import android.opengl.GLSurfaceView
import com.example.cnc3d.domain.models.GcodePath
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GcodeRenderer3D(
    private val pathProvider: () -> GcodePath?
) : GLSurfaceView.Renderer {

    var angleX = 0f
    var angleY = 0f
    var zoom = 1f

    private var vertexBuffer: FloatBuffer? = null
    private var lastPathHash: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES10.glClearColor(0f, 0f, 0f, 1f)
        GLES10.glEnable(GLES10.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES10.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT or GLES10.GL_DEPTH_BUFFER_BIT)

        val path = pathProvider() ?: return

        // Update buffer only if path changes
        if (path.hashCode() != lastPathHash) {
            updateBuffer(path)
            lastPathHash = path.hashCode()
        }

        val buffer = vertexBuffer ?: return

        GLES10.glMatrixMode(GLES10.GL_MODELVIEW)
        GLES10.glLoadIdentity()

        GLES10.glTranslatef(0f, 0f, -50f * zoom)
        GLES10.glRotatef(angleX, 1f, 0f, 0f)
        GLES10.glRotatef(angleY, 0f, 1f, 0f)

        GLES10.glColor4f(0f, 0.5f, 1f, 1f)
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY)
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, buffer)
        GLES10.glDrawArrays(GLES10.GL_LINES, 0, path.segments.size * 2)
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY)
    }

    private fun updateBuffer(path: GcodePath) {
        val size = path.segments.size * 2 * 3 * 4 // 2 vertices * 3 coords * 4 bytes
        val bb = ByteBuffer.allocateDirect(size)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()

        path.segments.forEach { seg ->
            fb.put(seg.x1); fb.put(seg.y1); fb.put(seg.z1)
            fb.put(seg.x2); fb.put(seg.y2); fb.put(seg.z2)
        }
        fb.position(0)
        vertexBuffer = fb
    }
}
