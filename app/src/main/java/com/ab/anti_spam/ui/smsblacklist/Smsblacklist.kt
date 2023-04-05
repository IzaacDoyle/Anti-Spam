package com.ab.anti_spam.ui.smsblacklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.ab.anti_spam.adapters.SMSBlacklistTablayoutAdapter
import com.ab.anti_spam.databinding.FragmentSmsblacklistBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.smswarning.overlayservice
import com.google.android.material.tabs.TabLayout

class Smsblacklist : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentSmsblacklistBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val blacklistViewModel: SmsblacklistViewModel by activityViewModels()

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentSmsblacklistBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        tabLayout = fragBinding.tabLayout
        viewPager = fragBinding.viewPager

        fragBinding.fab.setOnClickListener{
            val optionsDialog = SMSOptionsDialog()
            optionsDialog.show(parentFragmentManager,null)
        }
        tabLayoutSetup()

        return root
    }




    private fun tabLayoutSetup(){
        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText("Keywords"))
        tabLayout.addTab(tabLayout.newTab().setText("Regex"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        viewPager.adapter = SMSBlacklistTablayoutAdapter(app.applicationContext,childFragmentManager,tabLayout.tabCount)

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

}