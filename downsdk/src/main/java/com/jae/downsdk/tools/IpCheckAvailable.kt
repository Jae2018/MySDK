package com.jae.downsdk.tools

import java.net.InetAddress

object IpCheckAvailable {

    fun tryNet(host: String, timeOut: Int): Boolean {
        return InetAddress.getByName(host).isReachable(timeOut)
    }

    fun tryNet() {
        val runtime = Runtime.getRuntime()
        val proc = runtime.exec("ping www.google.com")

    }

}