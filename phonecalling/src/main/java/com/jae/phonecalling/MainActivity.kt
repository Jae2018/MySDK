package com.jae.phonecalling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jae.phonecalling.bean.TestBean
import com.jae.phonecalling.bean.User
import com.orhanobut.logger.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list1 = arrayListOf<TestBean>()
        val list2 = arrayListOf<User>()
        val testBean = TestBean("236-891-25", "火星三区", "3")
        list1.add(testBean)
        val user = User("", "", 12, list1)
        list2.add(user)
        Logger.d(list2)


    }

}
