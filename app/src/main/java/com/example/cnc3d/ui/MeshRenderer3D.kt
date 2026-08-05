package com.example.cnc3d.ui

import android.opengl.GLES10
import android.opengl.GLSurfaceView
import com.example.cnc3d.domain.models.Mesh
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MeshRenderer3D(
    private val meshProvider: () -> Mesh?
) : GLSurfaceView.Renderer {

    var angleX = 0f
    var angleY = 0f
    var zoom = 1f

    private var vertexBuffer: FloatBuffer? = null
    private var lastMeshHash: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES10.glClearColor(0f, 0f, 0f, 1f)
        GLES10.glEnable(GLES10.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES10.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT or GLES10.GL_DEPTH_BUFFER_BIT)

        val mesh = meshProvider() ?: return

        if (mesh.hashCode() != lastMeshHash) {
            updateBuffer(mesh)
            lastMeshHash = mesh.hashCode()
        }

        val buffer = vertexBuffer ?: return

        GLES10.glMatrixMode(GLES10.GL_MODELVIEW)
        GLES10.glLoadIdentity()
        GLES10.glTranslatef(0f, 0f, -50f * zoom)
        GLES10.glRotatef(angleX, 1f, 0f, 0f)
        GLES10.glRotatef(angleY, 0f, 1f, 0f)

        GLES10.glColor4f(0f, 1f, 0f, 1f)
        GLES10.glPointSize(5f)
        GLES10.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        GLES10.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer)
        GLES10.glDrawArrays(GL10.GL_POINTS, 0, mesh.points.size)
        GLES10.glDisableClientState(GL10.GL_VERTEX_ARRAY)
    }

    private fun updateBuffer(mesh: Mesh) {
        val points = mesh.points
        val bb = ByteBuffer.allocateDirect(points.size * 3 * 4)
            .order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()

        points.forEach { p ->
            fb.put(p.x)
            fb.put(p.y)
            fb.put(p.z)
        }
        fb.position(0)
        vertexBuffer = fb
    }
}
