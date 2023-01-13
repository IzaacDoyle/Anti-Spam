package com.ab.anti_spam.ui.callblacklist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ab.anti_spam.databinding.CallblacklistNumberblockDialogBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel

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
    ): View? {

        _fragBinding = CallblacklistNumberblockDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fragBinding.BlockByNumber.setOnClickListener{
            addBlock()
        }

        return root
    }

    private fun addBlock(){
        if(!fragBinding.textInputLayout.editText?.text.toString().isEmpty()) {
            val data = fragBinding.textInputLayout.editText?.text.toString()
            val model = CallBlacklistModel()
            model.by_number = data
            blacklistViewModel.addBlacklist(model, app)
            dismiss()
            fragBinding.BlockByNumber.isEnabled = false
        }
    }
}