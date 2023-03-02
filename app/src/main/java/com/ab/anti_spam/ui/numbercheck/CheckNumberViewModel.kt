package com.ab.anti_spam.ui.numbercheck

import androidx.lifecycle.ViewModel
import com.ab.anti_spam.firebase.FirebaseDBManager
import com.ab.anti_spam.models.CommunityBlockingModel
import com.ab.anti_spam.models.LocalBlockModel

class CheckNumberViewModel : ViewModel() {


    fun checkCommunityNumbers(phoneNumber: String,callback: (CommunityBlockingModel?) -> Unit ){
        FirebaseDBManager.getCommunityReportByNumber(phoneNumber,callback)
    }

    fun checkFireStoreNumbers(phoneNumber: String, callback: (LocalBlockModel?) -> Unit){
        FirebaseDBManager.getDocumentByNumber(phoneNumber,callback)
    }


}