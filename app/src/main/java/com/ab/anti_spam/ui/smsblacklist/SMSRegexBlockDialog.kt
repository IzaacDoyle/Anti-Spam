package com.ab.anti_spam.ui.smsblacklist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.SmsblacklistRegexblockDialogBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.SMSBlacklistModel

class SMSRegexBlockDialog: DialogFragment() {

    private var _fragBinding: SmsblacklistRegexblockDialogBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val blacklistViewModel: SmsblacklistViewModel by activityViewModels()
    lateinit var app: Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = SmsblacklistRegexblockDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fragBinding.WarnByRegex.setOnClickListener{
            addWarning()
        }
        darkTheme()
        return root
    }

    private fun addWarning(){
        if(!fragBinding.textInputLayout.editText?.text.toString().isEmpty()) {
            val data = fragBinding.textInputLayout.editText?.text.toString()
            val model = SMSBlacklistModel()
            model.by_regex = data
            blacklistViewModel.addBlacklist(model, app)
            dismiss()
            fragBinding.WarnByRegex.isEnabled = false
        }
    }

    fun darkTheme(){
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            fragBinding.root.setBackgroundResource(R.drawable.dialog_background_dark)
            fragBinding.header.setTextColor(Color.WHITE)
        }else{
            fragBinding.root.setBackgroundResource(R.drawable.dialog_background)
        }
    }

}