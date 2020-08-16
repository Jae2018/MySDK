package com.jae.downsdk.tools

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process.myPid
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object CrashHandler : Thread.UncaughtExceptionHandler {

    private val PATH: String = Environment.getExternalStorageDirectory().absolutePath
    private const val FILE_NAME_SUFFIX = ".trace"
    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    private var mContext: Context? = null

    fun init(context: Context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        mContext = context.applicationContext
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            //保存到本地
            exportExceptionToSDCard(e)
            //下面也可以写上传的服务器的代码
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        e.printStackTrace()
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler!!.uncaughtException(t, e)
        } else {
            android.os.Process.killProcess(myPid())
        }
    }

    /**
     * 导出异常信息到SD卡
     *
     * @param e
     */
    private fun exportExceptionToSDCard(e: Throwable) {
        //判断SD卡是否存在
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            return
        }
        val time: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
        val file = File(PATH + File.separator.toString() + time + FILE_NAME_SUFFIX)
        try {
            //往文件中写入数据
            val pw = PrintWriter(BufferedWriter(FileWriter(file)))
            pw.println(time)
            pw.println(appendPhoneInfo())
            e.printStackTrace(pw)
            pw.close()
        } catch (e1: IOException) {
            e1.printStackTrace()
        } catch (e1: PackageManager.NameNotFoundException) {
            e1.printStackTrace()
        }
    }

    /**
     * 获取手机信息
     */
    @Throws(PackageManager.NameNotFoundException::class)
    private fun appendPhoneInfo(): String? {
        val pm = mContext!!.packageManager
        val pi = pm.getPackageInfo(mContext!!.packageName, PackageManager.GET_ACTIVITIES)
        val sb = StringBuilder()
        //App版本
        sb.append("App Version: ")
        sb.append(pi.versionName)
        sb.append("_")
        sb.append(if (Build.VERSION.SDK_INT<28) pi.versionCode else pi.longVersionCode)

        //Android版本号
        sb.append("OS Version: ")
        sb.append(Build.VERSION.RELEASE)
        sb.append("_")
        sb.append(Build.VERSION.SDK_INT)

        //手机制造商
        sb.append("Vendor: ")
        sb.append(Build.MANUFACTURER.trimIndent()        )

        //手机型号
        sb.append("Model: ")
        sb.append(Build.MODEL.trimIndent()        )

        //CPU架构
        sb.append("CPU: ")
        sb.append(Arrays.toString(Build.SUPPORTED_ABIS))
        return sb.toString()
    }
}