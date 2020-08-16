package com.jae.downsdk

import android.os.Bundle
import android.os.Handler
import android.os.Messenger
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_animate.*

class MyTest :AppCompatActivity(){
    val messager = Messenger(Handler())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_animate)



    }
}