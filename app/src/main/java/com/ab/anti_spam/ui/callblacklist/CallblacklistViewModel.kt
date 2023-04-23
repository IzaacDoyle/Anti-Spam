package com.ab.anti_spam.ui.callblacklist

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ab.anti_spam.firebase.FirebaseAuthManager
import com.ab.anti_spam.localstorage.CallBlacklistInterface
import com.ab.anti_spam.localstorage.CallBlacklistStorage
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel

class CallblacklistViewModel() : ViewModel()  {

    private val selectedTabIndex = MutableLiveData<Int>()

    val observableSelectedTabIndex: LiveData<Int>
        get() = selectedTabIndex

    private val blacklist = MutableLiveData<MutableList<CallBlacklistModel>>()

    val observableBlacklist: LiveData<MutableList<CallBlacklistModel>>
        get() = blacklist

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun changeTab(index: Int){
        selectedTabIndex.value = index
    }

    fun refresh(app: Main){
        var localStorage =  CallBlacklistStorage(app.applicationContext)
        blacklist.value = localStorage.getAll()
    }

    fun addBlacklist(model: CallBlacklistModel,app: Main){
        var localStorage =  CallBlacklistStorage(app.applicationContext)
        var size = localStorage.getAll().size
        model.id = (size+1).toLong()
        localStorage.add(model)
        refresh(app)
    }

    fun deleteModel(model: CallBlacklistModel,app: Main){
        var localStorage =  CallBlacklistStorage(app.applicationContext)
        localStorage.delete(model)
        refresh(app)
    }

}