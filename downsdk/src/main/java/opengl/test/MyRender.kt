package opengl.test

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRender() : GLSurfaceView.Renderer {

    private var program = 0
    private var vPosition = 0
    private var uColor = 0

    // 顶点着色器的脚本
    private val verticesShader =
        """attribute vec2 vPosition;            
void main(){                         
   gl_Position = vec4(vPosition,0,1);
}"""

    // 片元着色器的脚本
    private val fragmentShader =
        """precision mediump float;         
uniform vec4 uColor;             
void main(){                     
   gl_FragColor = uColor;        
}"""

    /**
     * 加载制定shader的方法
     * @param shaderType shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
     * @param sourceCode shader的脚本
     * @return shader索引
     */
    private fun loadShader(shaderType: Int, sourceCode: String): Int {
        // 创建一个新shader
        var shader = GLES20.glCreateShader(shaderType)
        // 若创建成功则加载shader
        if (shader != 0) {
            // 加载shader的源代码
            GLES20.glShaderSource(shader, sourceCode)
            // 编译shader
            GLES20.glCompileShader(shader)
            // 存放编译成功shader数量的数组
            val compiled = IntArray(1)
            // 获取Shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) { //若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader $shaderType:")
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    /**
     * 创建shader程序的方法
     */
    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        //加载顶点着色器
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }

        // 加载片元着色器
        val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }

        // 创建程序
        var program = GLES20.glCreateProgram()
        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (program != 0) {
            // 向程序中加入顶点着色器
            GLES20.glAttachShader(program, vertexShader)
            // 向程序中加入片元着色器
            GLES20.glAttachShader(program, pixelShader)
            // 链接程序
            GLES20.glLinkProgram(program)
            // 存放链接成功program数量的数组
            val linkStatus = IntArray(1)
            // 获取program的链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ")
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    /**
     * 获取图形的顶点
     * 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
     * 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
     *
     * @return 顶点Buffer
     */
    private fun getVertices(): FloatBuffer? {
        val vertices = floatArrayOf(
            0.0f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
        )

        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个float占四个字节
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) //设置字节顺序
        val vertexBuf = vbb.asFloatBuffer() //转换为Float型缓冲
        vertexBuf.put(vertices) //向缓冲区中放入顶点坐标数据
        vertexBuf.position(0) //设置缓冲区起始位置
        return vertexBuf
    }

    override fun onDrawFrame(gl: GL10) {
        // 获取图形的顶点坐标
        val vertices = getVertices()
        // 清屏
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        // 使用某套shader程序
        GLES20.glUseProgram(program)
        // 为画笔指定顶点位置数据(vPosition)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertices)
        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(vPosition)
        // 设置属性uColor(颜色 索引,R,G,B,A)
        GLES20.glUniform4f(uColor, 0.0f, 1.0f, 0.0f, 1.0f)
        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        val ratio = (width / height).toFloat()
        gl.glFrustumf(-ratio, ratio, -1F, 1F, 1F, 10F)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        // 初始化着色器
        // 基于顶点着色器与片元着色器创建程序
        program = createProgram(verticesShader, fragmentShader)
        // 获取着色器中的属性引用id(传入的字符串就是我们着色器脚本中的属性名)
        vPosition = GLES20.glGetAttribLocation(program, "vPosition")
        uColor = GLES20.glGetUniformLocation(program, "uColor")

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
        GLES20.glClearColor(1.0f, 0f, 0f, 1.0f)
    }

//    fun intBufferUtil(arr: IntArray): IntBuffer {
//        val mBuffer: IntBuffer
//        val bb = ByteBuffer.allocateDirect(arr.size * 4)
//        mBuffer = bb.asIntBuffer()
//        mBuffer.put(arr)
//        mBuffer.position(0)
//        return mBuffer
//    }
//
//    fun floatBufferUtil(arr: FloatArray): FloatBuffer {
//        val mBuffer: FloatBuffer
//        val bb = ByteBuffer.allocateDirect(arr.size * 4)
//        mBuffer = bb.asFloatBuffer()
//        mBuffer.put(arr)
//        mBuffer.position(0)
//        return mBuffer
//    }

}