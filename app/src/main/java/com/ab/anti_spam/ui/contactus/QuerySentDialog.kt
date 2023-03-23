package com.ab.anti_spam.ui.contactus

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.CallblacklistCountryblockDialogBinding
import com.ab.anti_spam.databinding.FragmentQuerySentDialogBinding


class QuerySentDialog : DialogFragment() {

    private var _fragBinding: FragmentQuerySentDialogBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragBinding = FragmentQuerySentDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        root.minimumWidth = Resources.getSystem().displayMetrics.widthPixels/ 2 + 300
        darkTheme()

        returnHome()

        return root
    }


    fun returnHome(){
        fragBinding.returnHome.setOnClickListener{
            this.dismiss()
            findNavController().navigate(R.id.action_nav_contactus_to_nav_checkNumber)
        }
    }

    fun darkTheme(){
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            fragBinding.root.setBackgroundResource(R.drawable.dialog_background_dark)
        }else{
            fragBinding.root.setBackgroundResource(R.drawable.dialog_background)
        }
    }

}