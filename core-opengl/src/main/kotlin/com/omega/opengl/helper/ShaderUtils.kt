package com.omega.opengl.helper

import android.opengl.GLES30



object ShaderUtils {

    // ------------------------------------------------------------------------
    // 编译顶点着色器
    // ------------------------------------------------------------------------
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES30.GL_VERTEX_SHADER, shaderCode)
    }

    // ------------------------------------------------------------------------
    // 编译片段着色器
    // ------------------------------------------------------------------------
    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES30.GL_FRAGMENT_SHADER, shaderCode)
    }

    /**
     * 编译
     * @param type       - 顶点着色器:GLES30.GL_VERTEX_SHADER
     * - 片段着色器:GLES30.GL_FRAGMENT_SHADER
     * @param shaderCode
     * @return
     */
    private fun compileShader(type: Int, shaderCode: String): Int {
        //创建一个着色器
        val shaderId = GLES30.glCreateShader(type)
        if (shaderId != 0) {
            GLES30.glShaderSource(shaderId, shaderCode)
            GLES30.glCompileShader(shaderId)
            //检测状态
            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                val logInfo = GLES30.glGetShaderInfoLog(shaderId)
                System.err.println(logInfo)
                //创建失败
                GLES30.glDeleteShader(shaderId)
                return 0
            }
            return shaderId
        } else {
            //创建失败
            return 0
        }
    }

    /**
     * 链接GLES程序
     *
     * @param vertexShaderId   顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return
     */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programId = GLES30.glCreateProgram()
        if (programId != 0) {
            //将顶点着色器加入到程序
            GLES30.glAttachShader(programId, vertexShaderId)
            //将片元着色器加入到程序中
            GLES30.glAttachShader(programId, fragmentShaderId)
            //链接着色器程序
            GLES30.glLinkProgram(programId)
            val linkStatus = IntArray(1)

            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val logInfo = GLES30.glGetProgramInfoLog(programId)
                System.err.println(logInfo)
                GLES30.glDeleteProgram(programId)
                return 0
            }
            return programId
        } else {
            //创建失败
            return 0
        }
    }
}