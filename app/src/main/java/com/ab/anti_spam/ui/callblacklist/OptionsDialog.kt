package com.ab.anti_spam.ui.callblacklist

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.CallblacklistCountryblockDialogBinding
import com.ab.anti_spam.databinding.CallblacklistOptionsDialogBinding


class OptionsDialog: DialogFragment() {

    private var _fragBinding: CallblacklistOptionsDialogBinding? = null

    private val fragBinding get() = _fragBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = CallblacklistOptionsDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fragBinding.countryBlock.setOnClickListener{

            //Dismiss current dialog (OptionsDialog)
            dismiss()
            //Call blacklist is parent so we navigate from parent (CallBlacklist) to the desired dialog
            findNavController().navigate(R.id.action_nav_call_blacklist_to_nav_countryBlockDialog)

        }



        return root
    }
}