package com.ab.anti_spam.smswarning

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class overlayservice: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val smsFrom = intent?.getStringExtra("msg_from").toString()
            val callFrom = intent?.getStringExtra("call_from").toString()

            if(!smsFrom.equals("null")) {
                display(this, smsFrom)
            }
            if(!callFrom.equals("null")) {
                callBlockDisplay(this, callFrom)
            }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}