package com.ab.anti_spam.ui.smsblacklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ab.anti_spam.adapters.SMSBlacklistAdapter
import com.ab.anti_spam.adapters.deleteListenerSMS
import com.ab.anti_spam.databinding.FragmentSMSKeywordBlockViewPagerBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.SMSBlacklistModel


class SMSKeywordBlockViewPager : Fragment(),deleteListenerSMS {

    lateinit var app: Main
    private var _fragBinding: FragmentSMSKeywordBlockViewPagerBinding? = null
    lateinit var adapter: SMSBlacklistAdapter
    private val blacklistViewModel: SmsblacklistViewModel by activityViewModels()
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentSMSKeywordBlockViewPagerBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        emptyStorageLayout()
        observer()
        loadBlacklist()

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
        val blacklist = app.localSMSBlacklist.getAll()
        val anyCountryIsEmpty = blacklist.any { it.by_keyword.isNotEmpty() }

        if(anyCountryIsEmpty){
            fragBinding.blockIcon.isVisible = false
            fragBinding.blockText.isVisible = false
        }else{
            fragBinding.blockIcon.isVisible = true
            fragBinding.blockText.isVisible = true
        }
    }

    private fun loadBlacklist(){
        fragBinding.smsblacklistRecyclerview.adapter?.notifyDataSetChanged()
        blacklistViewModel.refresh(app)
        blacklistViewModel.observableBlacklist.observe(viewLifecycleOwner, Observer { blacklist ->
            blacklist?.let {
                renderRecyclerView(blacklist as ArrayList<SMSBlacklistModel>)
            }
        })
    }

    private fun renderRecyclerView(model: ArrayList<SMSBlacklistModel>){
        fragBinding.smsblacklistRecyclerview.layoutManager = LinearLayoutManager(activity)
        fragBinding.smsblacklistRecyclerview.adapter = SMSBlacklistAdapter(filterModelKeywordOnly(model) as ArrayList<SMSBlacklistModel>,this)
        adapter = fragBinding.smsblacklistRecyclerview.adapter as SMSBlacklistAdapter
    }

    private fun filterModelKeywordOnly(model: ArrayList<SMSBlacklistModel>) : ArrayList<SMSBlacklistModel>{
        val filteredArray = ArrayList<SMSBlacklistModel>()
        for(i in model){
            if(i.by_keyword.isNotEmpty()){
                filteredArray.add(i)
            }
        }
        return filteredArray
    }

    override fun onDeleteClick(model: SMSBlacklistModel) {
        blacklistViewModel.deleteModel(model,app)
    }


}