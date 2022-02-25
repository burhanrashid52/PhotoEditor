/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ja.burhanrashid52.photoeditor

import android.opengl.GLES20
import ja.burhanrashid52.photoeditor.GLToolbox.checkGlError
import ja.burhanrashid52.photoeditor.GLToolbox.createProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

internal class TextureRenderer {
    private var mProgram = 0
    private var mTexSamplerHandle = 0
    private var mTexCoordHandle = 0
    private var mPosCoordHandle = 0
    private var mTexVertices: FloatBuffer? = null
    private var mPosVertices: FloatBuffer? = null
    private var mViewWidth = 0
    private var mViewHeight = 0
    private var mTexWidth = 0
    private var mTexHeight = 0
    fun init() {
        // Create program
        mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        // Bind attributes and uniforms
        mTexSamplerHandle = GLES20.glGetUniformLocation(
            mProgram,
            "tex_sampler"
        )
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texcoord")
        mPosCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_position")

        // Setup coordinate buffers
        mTexVertices = ByteBuffer.allocateDirect(
            TEX_VERTICES.size * FLOAT_SIZE_BYTES
        )
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTexVertices?.put(TEX_VERTICES)?.position(0)
        mPosVertices = ByteBuffer.allocateDirect(
            POS_VERTICES.size * FLOAT_SIZE_BYTES
        )
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mPosVertices?.put(POS_VERTICES)?.position(0)
    }

    fun tearDown() {
        GLES20.glDeleteProgram(mProgram)
    }

    fun updateTextureSize(texWidth: Int, texHeight: Int) {
        mTexWidth = texWidth
        mTexHeight = texHeight
        computeOutputVertices()
    }

    fun updateViewSize(viewWidth: Int, viewHeight: Int) {
        mViewWidth = viewWidth
        mViewHeight = viewHeight
        computeOutputVertices()
    }

    fun renderTexture(texId: Int) {
        // Bind default FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        // Use our shader program
        GLES20.glUseProgram(mProgram)
        checkGlError("glUseProgram")

        // Set viewport
        GLES20.glViewport(0, 0, mViewWidth, mViewHeight)
        checkGlError("glViewport")

        // Disable blending
        GLES20.glDisable(GLES20.GL_BLEND)

        // Set the vertex attributes
        GLES20.glVertexAttribPointer(
            mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
            0, mTexVertices
        )
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(
            mPosCoordHandle, 2, GLES20.GL_FLOAT, false,
            0, mPosVertices
        )
        GLES20.glEnableVertexAttribArray(mPosCoordHandle)
        checkGlError("vertex attribute setup")

        // Set the input texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        checkGlError("glActiveTexture")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId)
        checkGlError("glBindTexture")
        GLES20.glUniform1i(mTexSamplerHandle, 0)

        // Draw
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun computeOutputVertices() {
        val imgAspectRatio = mTexWidth / mTexHeight.toFloat()
        val viewAspectRatio = mViewWidth / mViewHeight.toFloat()
        val relativeAspectRatio = viewAspectRatio / imgAspectRatio
        val x0: Float
        val y0: Float
        val x1: Float
        val y1: Float
        if (relativeAspectRatio > 1.0f) {
            x0 = -1.0f / relativeAspectRatio
            y0 = -1.0f
            x1 = 1.0f / relativeAspectRatio
            y1 = 1.0f
        } else {
            x0 = -1.0f
            y0 = -relativeAspectRatio
            x1 = 1.0f
            y1 = relativeAspectRatio
        }
        val coords = floatArrayOf(x0, y0, x1, y0, x0, y1, x1, y1)
        mPosVertices?.put(coords)?.position(0)
    }

    companion object {
        private const val VERTEX_SHADER = "attribute vec4 a_position;\n" +
                "attribute vec2 a_texcoord;\n" +
                "varying vec2 v_texcoord;\n" +
                "void main() {\n" +
                "  gl_Position = a_position;\n" +
                "  v_texcoord = a_texcoord;\n" +
                "}\n"
        private const val FRAGMENT_SHADER = "precision mediump float;\n" +
                "uniform sampler2D tex_sampler;\n" +
                "varying vec2 v_texcoord;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
                "}\n"
        private val TEX_VERTICES = floatArrayOf(
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
        )
        private val POS_VERTICES = floatArrayOf(
            -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f
        )
        private const val FLOAT_SIZE_BYTES = 4
    }
}