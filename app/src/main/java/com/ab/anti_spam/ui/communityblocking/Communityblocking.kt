package com.ab.anti_spam.ui.communityblocking

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.ab.anti_spam.adapters.CommunityBlockingTablayoutAdapter
import com.ab.anti_spam.databinding.FragmentCommunityblockingBinding
import com.ab.anti_spam.firebase.genUID
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CommunityBlockingCommentsModel
import com.ab.anti_spam.models.CommunityBlockingModel
import com.ab.anti_spam.ui.auth.LoggedInViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Communityblocking : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentCommunityblockingBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val communityViewModel: CommunityblockingViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private lateinit var uid: String
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadFireUser()
        _fragBinding = FragmentCommunityblockingBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        tabLayout = fragBinding.tabLayout
        viewPager = fragBinding.viewPager


        tabLayoutSetup()

        return root
    }

    private fun loadFireUser(){
    loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner,{
        uid=it.uid
        communityViewModel.UID.value = uid
    })
    }

    private fun tabLayoutSetup(){
        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText("Community Reports"))
        tabLayout.addTab(tabLayout.newTab().setText("My Reports"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        viewPager.adapter = CommunityBlockingTablayoutAdapter(app.applicationContext,childFragmentManager,tabLayout.tabCount)

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