package com.ab.anti_spam.ui.callblacklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ab.anti_spam.R
import com.ab.anti_spam.adapters.CallBlacklistAdapter
import com.ab.anti_spam.adapters.deleteListener
import com.ab.anti_spam.databinding.FragmentCountryBlockViewPagerBinding
import com.ab.anti_spam.databinding.FragmentNumberBlockViewPagerBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel

class NumberBlockViewPager : Fragment(),deleteListener {

    lateinit var app: Main
    private var _fragBinding: FragmentNumberBlockViewPagerBinding? = null
    lateinit var adapter: CallBlacklistAdapter
    private val blacklistViewModel: CallblacklistViewModel by activityViewModels()
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentNumberBlockViewPagerBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        emptyStorageLayout()
        loadBlacklist()
        observer()
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


    private fun emptyStorageLayout(){
        val blacklist = app.localCallBlacklist.getAll()
        val anyNumberIsEmpty = blacklist.any { it.by_number.isNotEmpty() }

        if(anyNumberIsEmpty){
            fragBinding.blockIcon.isVisible = false
            fragBinding.blockText.isVisible = false
        }else{
            fragBinding.blockIcon.isVisible = true
            fragBinding.blockText.isVisible = true
        }
    }

    private fun loadBlacklist(){
        fragBinding.callblacklistRecyclerview.adapter?.notifyDataSetChanged()
        blacklistViewModel.refresh(app)
        blacklistViewModel.observableBlacklist.observe(viewLifecycleOwner, Observer { blacklist ->
            blacklist?.let {
                renderRecyclerView(blacklist as ArrayList<CallBlacklistModel>)
            }
        })
    }

    private fun renderRecyclerView(model: ArrayList<CallBlacklistModel>){
        fragBinding.callblacklistRecyclerview.layoutManager = LinearLayoutManager(activity)
        fragBinding.callblacklistRecyclerview.adapter = CallBlacklistAdapter(filterModelCountryOnly(model) as ArrayList<CallBlacklistModel>,this)
        adapter = fragBinding.callblacklistRecyclerview.adapter as CallBlacklistAdapter
    }

    private fun filterModelCountryOnly(model: ArrayList<CallBlacklistModel>) : ArrayList<CallBlacklistModel>{
        val filteredArray = ArrayList<CallBlacklistModel>()
        for(i in model){
            if(i.by_number.isNotEmpty()){
                filteredArray.add(i)
            }
        }
        return filteredArray
    }

    override fun onDeleteClick(model: CallBlacklistModel) {
        blacklistViewModel.deleteModel(model,app)
    }
}