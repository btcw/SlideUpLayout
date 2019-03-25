package top.iwill.mylib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_test_list_layout.view.*
import top.iwill.mylib.R

/**
 * @description:
 * @author: btcw
 * @date: 2019/3/24
 */
class TestListAdapter(private val context: Context,private val list: MutableList<String>? = null) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return if (convertView != null){
            convertView.label.text = list?.get(position)
            convertView
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.item_test_list_layout,null)
            view.label.text = list?.get(position)
            view
        }
    }

    override fun getItem(position: Int): String = list?.get(position) ?: ""

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list?.size ?: 0

}