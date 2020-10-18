package br.com.apsmobile.tarefasonline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.apsmobile.tarefasonline.adapter.ViewPagerAdapter
import br.com.apsmobile.tarefasonline.fragment.TasksPendingFragment
import br.com.apsmobile.tarefasonline.fragment.TasksCompletedFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpTabs()

    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(TasksPendingFragment(), "Pendentes")
        adapter.addFragment(TasksCompletedFragment(), "Feitas")

        viewPager.adapter = adapter

        tabs.setupWithViewPager(viewPager)

//        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_home)
//        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_favorite)
//        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_settings)
    }

}