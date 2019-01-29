package tech.jackywang.greenbean.pods.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import tech.jackywang.greenbean.pods.demo.library.DepClass1

class MainActivity : AppCompatActivity() {

    lateinit var ddep: DepClass1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
