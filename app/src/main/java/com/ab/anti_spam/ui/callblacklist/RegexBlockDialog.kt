package com.ab.anti_spam.ui.callblacklist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.CallblacklistRegexblockDialogBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel

class RegexBlockDialog: DialogFragment() {

    private var _fragBinding: CallblacklistRegexblockDialogBinding? = null
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

        _fragBinding = CallblacklistRegexblockDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val adapter = ArrayAdapter(requireContext(),R.layout.list_countries,resources.getStringArray(R.array.regexes))
        fragBinding.regexSelect.setAdapter(adapter)


        fragBinding.BlockByRegex.setOnClickListener{
            addBlock()
        }

        darkTheme()

        return root
    }

    private fun addBlock(){
        if(!fragBinding.textInputLayout.editText?.text.toString().isEmpty()) {
            val regex = fragBinding.textInputLayout2.editText?.text.toString()
            val number = fragBinding.textInputLayout.editText?.text.toString()
            if (regex.isNotEmpty() || number.isNotEmpty()) {
                val data = regex + " : " + number
                val model = CallBlacklistModel()
                model.by_regex = data
                blacklistViewModel.addBlacklist(model, app)
                dismiss()
                fragBinding.BlockByRegex.isEnabled = false
            }
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