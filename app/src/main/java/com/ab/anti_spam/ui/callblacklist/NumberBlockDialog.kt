package com.ab.anti_spam.ui.callblacklist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.CallblacklistNumberblockDialogBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel
import com.ab.anti_spam.ui.numbercheck.CallLogDialog

class NumberBlockDialog: DialogFragment() {

    private var _fragBinding: CallblacklistNumberblockDialogBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val blacklistViewModel: CallblacklistViewModel by activityViewModels()
    lateinit var app: Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragBinding = CallblacklistNumberblockDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fragBinding.BlockByNumber.setOnClickListener{
            addBlock()
        }
        darkTheme()
        dialogCallback()
        openCalllog()
        return root
    }

    fun addBlock(){
        if(!fragBinding.textInputLayout.editText?.text.toString().isEmpty()) {
            val data = fragBinding.textInputLayout.editText?.text.toString()
            val model = CallBlacklistModel()
            model.by_number = data
            blacklistViewModel.addBlacklist(model, app)
            blacklistViewModel.changeTab(1)
            dismiss()
            fragBinding.BlockByNumber.isEnabled = false
        }
    }
    fun openCalllog(){
        fragBinding.callog.setOnClickListener{
            val calldialog = CallLogDialog()
            calldialog.show(parentFragmentManager,null)
        }
    }
    fun dialogCallback(){
        setFragmentResultListener("number") { _, bundleData ->
            val callLogNumber: String? = bundleData.getString("number")
            fragBinding.numberText.setText(callLogNumber)
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