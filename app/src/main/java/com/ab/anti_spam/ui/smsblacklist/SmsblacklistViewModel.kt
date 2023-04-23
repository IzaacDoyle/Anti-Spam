package com.ab.anti_spam.ui.smsblacklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ab.anti_spam.localstorage.SMSBlacklistStorage
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.SMSBlacklistModel

class SmsblacklistViewModel() : ViewModel()  {


    private val selectedTabIndex = MutableLiveData<Int>()

    val observableSelectedTabIndex: LiveData<Int>
        get() = selectedTabIndex

    private val blacklist = MutableLiveData<MutableList<SMSBlacklistModel>>()

    val observableBlacklist: LiveData<MutableList<SMSBlacklistModel>>
        get() = blacklist

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status


    fun changeTab(index: Int){
        selectedTabIndex.value = index
    }

    fun refresh(app: Main){
        var localStorage =  SMSBlacklistStorage(app.applicationContext)
        blacklist.value = localStorage.getAll()
    }

    fun addBlacklist(model: SMSBlacklistModel, app: Main){
        var localStorage =  SMSBlacklistStorage(app.applicationContext)
        var size = localStorage.getAll().size
        model.id = (size+1).toLong()
        localStorage.add(model)
        refresh(app)
    }

    fun deleteModel(model: SMSBlacklistModel, app: Main){
        var localStorage =  SMSBlacklistStorage(app.applicationContext)
        localStorage.delete(model)
        refresh(app)
    }

}