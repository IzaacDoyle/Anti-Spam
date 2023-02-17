package com.ab.anti_spam.main

import android.app.Application
import com.ab.anti_spam.localstorage.CallBlacklistStorage
import com.ab.anti_spam.localstorage.SMSBlacklistStorage
import com.ab.anti_spam.localstorage.SettingsStorage
import com.ab.anti_spam.models.CallBlacklistModel

class Main : Application() {
lateinit var localCallBlacklist : CallBlacklistStorage
lateinit var localSMSBlacklist : SMSBlacklistStorage
lateinit var settings : SettingsStorage
    override fun onCreate() {
        super.onCreate()
        localCallBlacklist = CallBlacklistStorage(applicationContext)
        localSMSBlacklist = SMSBlacklistStorage(applicationContext)
        settings = SettingsStorage(applicationContext)
    }
}