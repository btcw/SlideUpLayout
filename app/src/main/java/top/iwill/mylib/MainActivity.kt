package top.iwill.mylib

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import top.iwill.mylib.adapter.TestListAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listview.adapter =
            TestListAdapter(this, mutableListOf("第一行数据", "第二行数据", "第三行数据", "第四行数据", "第五行数据", "第六行数据", "第七行数据", "第八行数据"))
        content.registerProgressListener {
            onMove {

            }
            onRelease{

            }
            onSlideToTop{

            }
            onSlideToBottom{

            }
        }
    }
}
