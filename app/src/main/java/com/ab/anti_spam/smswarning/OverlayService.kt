package com.ab.anti_spam.smswarning

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class overlayservice: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val smsFrom = intent?.getStringExtra("msg_from").toString()
            val callFrom = intent?.getStringExtra("call_from").toString()
            val naiveFrom = intent?.getStringExtra("naiveFrom").toString()

            if(!smsFrom.equals("null")) {
                display(this, smsFrom)
            }
            if(!callFrom.equals("null")){
               callBlockDisplay(this,callFrom)
            }
            if(!naiveFrom.equals("null")){
                naive(naiveFrom, this.applicationContext)
            }

        return super.onStartCommand(intent, flags, startId)
    }


    fun naive(message: String,context: Context){
        var msg = message.split("@@@@")

        val naive = NaiveClassifier()
        val isScam = naive.isFraudulent(context, msg[0])

        if(isScam){
            callBlockDisplay(this,msg[1])
        }

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}