package com.ab.anti_spam.ui.callblacklist

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.viewpager.widget.ViewPager
import com.ab.anti_spam.R
import com.ab.anti_spam.adapters.CallBlacklistTablayoutAdapter
import com.ab.anti_spam.adapters.deleteListener
import com.ab.anti_spam.databinding.FragmentCallblacklistBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel
import com.google.android.material.tabs.TabLayout


class Callblacklist : Fragment(),deleteListener {
    lateinit var app: Main
    private var _fragBinding: FragmentCallblacklistBinding? = null
    private val blacklistViewModel: CallblacklistViewModel by activityViewModels()
    private val fragBinding get() = _fragBinding!!

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _fragBinding = FragmentCallblacklistBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        tabLayout = fragBinding.tabLayout
        viewPager = fragBinding.viewPager

        fragBinding.fab.setOnClickListener{
            val optionsDialog = OptionsDialog()
            optionsDialog.show(parentFragmentManager,null)
        }

        setupMenu()
        emptyStorageLayout()
        observer()
        tabLayoutSetup()
        return root
    }

    private fun observer(){
        blacklistViewModel.refresh(app)
        blacklistViewModel.observableBlacklist.observe(viewLifecycleOwner, Observer { blacklist ->
            blacklist?.let {
                emptyStorageLayout()
            }
        })
    }


    private fun tabLayoutSetup(){
        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText("Countries"))
        tabLayout.addTab(tabLayout.newTab().setText("Numbers"))
        tabLayout.addTab(tabLayout.newTab().setText("Regex"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        viewPager.adapter = CallBlacklistTablayoutAdapter(app.applicationContext,childFragmentManager,tabLayout.tabCount)

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    private fun emptyStorageLayout(){
        if(app.localCallBlacklist.getAll().size > 0){
            fragBinding.blockIcon.isVisible = false
            fragBinding.blockText.isVisible = false
        }else{
            fragBinding.blockIcon.isVisible = true
            fragBinding.blockText.isVisible = true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onDeleteClick(model: CallBlacklistModel) {
        blacklistViewModel.deleteModel(model,app)
    }

    private fun setupMenu(){
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                return menuInflater.inflate(R.menu.call_blacklist_menu,menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }

        },viewLifecycleOwner,Lifecycle.State.RESUMED)
    }

}