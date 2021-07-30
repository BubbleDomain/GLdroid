package com.batteria.gldroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.batteria.gldroid.base.Item
import com.batteria.gldroid.base.ItemAdapter
import com.batteria.gldroid.base.OpenglActivity
import com.batteria.gldroid.render.RectRender
import com.batteria.gldroid.render.TriggerRender

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/7/29
 * @description:
 */
class MainActivity : AppCompatActivity() {
    private fun initRenders() {
        Renders.init(mapOf(
            TriggerRender.TAG to RenderData(R.drawable.triangle, TriggerRender::class.java),
            RectRender.TAG to RenderData(R.drawable.square, RectRender::class.java)
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdapter()
    }

    private fun initAdapter() {
        initRenders()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ItemAdapter()
        val list = Renders.map.map {
            Item(it.key, it.value.resId) {
                val intent = Intent(this, OpenglActivity::class.java)
                intent.putExtra("TAG", it.key)
                startActivity(intent)
            }
        }

        adapter.setItems(list)
        recyclerView.adapter = adapter
    }
}