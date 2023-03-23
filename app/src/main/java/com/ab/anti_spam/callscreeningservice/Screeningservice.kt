package com.ab.anti_spam.callscreeningservice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.telecom.Call
import android.telecom.Call.Details
import android.telecom.CallScreeningService
import android.telecom.CallScreeningService.CallResponse.Builder
import com.ab.anti_spam.firebase.FirebaseDBManager
import com.ab.anti_spam.helpers.SHA256
import com.ab.anti_spam.helpers.UIDsave
import com.ab.anti_spam.localstorage.CallBlacklistStorage
import com.ab.anti_spam.localstorage.LocalBlockStorage
import com.ab.anti_spam.localstorage.SettingsStorage
import com.ab.anti_spam.models.SettingsModel
import com.ab.anti_spam.smswarning.overlayservice


class Screeningservice: CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = getPhoneNumber(callDetails)
        val response = CallResponse.Builder()
        handlePhoneCall(response, phoneNumber, applicationContext,callDetails)

    }

    private fun handlePhoneCall(response: CallResponse.Builder, phoneNumber: String, context: Context,callDetails: Details) {
        println(String.format("Incoming call from %s", phoneNumber))
        //Gets the latest authenticated user UID from file.
        val latestUID = UIDsave.getUidFromFile(applicationContext)
        val settings = latestUID?.let { getSettings(it) }
        if(settings != null) {
            if(settings.unknown_block == true){
                if(phoneNumber.isEmpty()){
                    blockNumber(response, "Unknown", callDetails)
                }
            }
            if(phoneNumber.isNotEmpty()) {
                //Manual blocking
                if (settings.personal_block == true) {
                    personalBlacklistBlocking(response, phoneNumber, callDetails)
                }
                //Local firestore blocking (Downloaded from Firstore Database)
                if (settings.local_store_block == true) {
                    localDBlocking(response, phoneNumber, callDetails)
                }


                //Community Blocking (Realtime Database) callback
                FirebaseDBManager.getCommunityReportByNumber(phoneNumber, {
                    if (it != null) {
                        if (it.user_comments.size >= settings.community_block_num) {
                            blockNumber(response, phoneNumber, callDetails)
                        }

                    }
                })

                //Database Blocking (FireStore) callback
                if (settings.database_block == true) {
                    FirebaseDBManager.checkNumber(phoneNumber, {
                        if (it == true) {
                            blockNumber(response, phoneNumber, callDetails)
                        }
                    })
                }

            }

        }

    }

    private fun getPhoneNumber(callDetails: Call.Details): String {
        if(callDetails.handle != null) {
            return callDetails.handle.toString().replace("tel:", "").replace("%2B", "+")
            //.removeTelPrefix().parseCountryCode()
        }else return ""
    }


    fun getSettings(uid: String) : SettingsModel?{
        try {
            //Gets settings
            val settings = SettingsStorage(this.applicationContext)
            //Gets all the settings
            val allSettings = settings.getAll()
            //Local variable
            var userSetting: SettingsModel? = null
            //Gets the settings associated to the user ID
            for (i in allSettings) {
                if (i.uid.equals(uid)) {
                    userSetting = i
                    break
                }
            }
            return userSetting
        }catch (e: Exception){
            return null
        }
    }

    fun localDBlocking(response: CallResponse.Builder,phoneNumber: String,callDetails: Details){
        val localBlockStorage = LocalBlockStorage(applicationContext)

        if(localBlockStorage.checkIfExists()){

            val blacklist = localBlockStorage.getAll()
            val convertedNumber = SHA256.sha256(phoneNumber)
            for(i in blacklist){
                if(i.number.equals(convertedNumber)){
                    blockNumber(response,phoneNumber,callDetails)
                }
            }

        }
    }

    fun personalBlacklistBlocking(response: CallResponse.Builder,phoneNumber: String,callDetails: Details){
        //Gets the Callblacklists
        val localStorage = CallBlacklistStorage(this.applicationContext)

        val array = localStorage.getAll()
        val countryBlockArray = arrayListOf<String>()
        val numberBlockArray = arrayListOf<String>()
        val regexBlockArray = arrayListOf<String>()

        for(i in array){
            if(i.by_country.isNotEmpty()) {
                countryBlockArray.add(i.by_country)
            }
            if(i.by_number.isNotEmpty()) {
                numberBlockArray.add(i.by_number)
            }
            if(i.by_regex.isNotEmpty()) {
                regexBlockArray.add(i.by_regex)
            }
        }

        if(countryBlockArray.size > 0){
            val cleanArray = cleanCountry(countryBlockArray)

            for (i in cleanArray){
                if(i in phoneNumber){
                    blockNumber(response,phoneNumber,callDetails)
                }
            }
        }
        if(numberBlockArray.size > 0){
            val cleanArray = cleanNumber(numberBlockArray)
            for(i in cleanArray){
                if(i in phoneNumber){
                    blockNumber(response,phoneNumber,callDetails)
                }
            }
        }
        if(regexBlockArray.size > 0){
            val cleanArray = cleanRegex(regexBlockArray)
            for(i in cleanArray){
                if(phoneNumber.matches(i.toRegex())){
                    blockNumber(response,phoneNumber,callDetails)
                }
            }
        }
    }



    private fun cleanCountry(countryArray: ArrayList<String>): java.util.ArrayList<String>{
        val cleanArray = arrayListOf<String>()

        for (i in countryArray){
            cleanArray.add(i.split("(")[1].replace(")","").replace("(","").trim())
        }

        return cleanArray
    }

    private fun cleanNumber(numberArray: ArrayList<String>): java.util.ArrayList<String>{
        val cleanArray = arrayListOf<String>()

        for (i in numberArray){

            //If the first number is 0, drop the 0
            if(i.get(0).toString().equals("0")){
                cleanArray.add(i.drop(1).trim())
            }else{
                cleanArray.add(i.trim())
            }
        }
        return cleanArray
    }

    private fun cleanRegex(regexArray: ArrayList<String>): java.util.ArrayList<String>{
        val cleanArray = arrayListOf<String>()

        for(i in regexArray){
            cleanArray.add(i.trim())
        }
        return cleanArray
    }

    @SuppressLint("MissingPermission")
    private fun blockNumber(response: Builder, phoneNumber: String,callDetails: Details){

        response.apply {
            println((String.format("Rejected call from %s", phoneNumber)))

            //Trigger warning
            val overlayintent = Intent(applicationContext, overlayservice::class.java)
            overlayintent.putExtra("call_from", phoneNumber)
            applicationContext.startService(Intent(overlayintent))
            setRejectCall(true)
            setDisallowCall(true)
            setSkipCallLog(true)
            respondToCall(callDetails, response.build())
        }
    }

}