package com.ab.anti_spam.ui.contactus

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.FragmentCallblacklistBinding
import com.ab.anti_spam.databinding.FragmentContactUsBinding
import com.ab.anti_spam.email.sendEmail
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.ui.callblacklist.CallblacklistViewModel
import com.ab.anti_spam.ui.callblacklist.OptionsDialog

class ContactUs : Fragment() {
    lateinit var app: Main
    private var _fragBinding: FragmentContactUsBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _fragBinding = FragmentContactUsBinding.inflate(inflater, container, false)

        val root = fragBinding.root

        fragBinding.submit.setOnClickListener{
            submit()
        }

        val adapter = ArrayAdapter(requireContext(),R.layout.list_countries,resources.getStringArray(R.array.subjects))
        fragBinding.subjectSelect.setAdapter(adapter)

        return root
    }


    fun submit(){
        val unfocusedColor = Color.RED
        val states = arrayOf(intArrayOf(android.R.attr.state_focused))
        val colors = intArrayOf(unfocusedColor)
        val colorStateList = ColorStateList(states, colors)

        //Subject
        val subjectLayout = fragBinding.textInputLayoutSubject
        val subjectText = fragBinding.subjectSelect.text.toString()
        //Email
        val emailLayout = fragBinding.textInputLayoutEmail
        val emailText = fragBinding.emailText.text.toString()
        //Name
        val nameLayout = fragBinding.textInputLayoutName
        val nameText = fragBinding.nameText.text.toString()
        //Description
        val descriptionLayout = fragBinding.textInputLayoutDescription
        val descriptionText = fragBinding.titleDescription.text.toString()

        var gate = 0

        //validation
        if(subjectText.isEmpty()){
            subjectLayout.setBoxStrokeColorStateList(colorStateList)
        }else{
            gate++
        }
        if (emailText.isEmpty() || emailText.length < 5){
            emailLayout.setBoxStrokeColorStateList(colorStateList)
        }else{
            gate++
        }
        if (nameText.isEmpty() || nameText.length < 5){
            nameLayout.setBoxStrokeColorStateList(colorStateList)
        }else{
            gate++
        }
        if(descriptionText.isEmpty() || descriptionText.length < 20){
            descriptionLayout.setBoxStrokeColorStateList(colorStateList)
        }else{
            gate++
        }

        if(gate == 4){
            sendEmail(subjectText,emailText,nameText,descriptionText,resources.getString(R.string.pss)) {sent ->
                if(sent==true){
                    val sentDialog = QuerySentDialog()
                    sentDialog.show(parentFragmentManager,null)
                }
            }
        }
    }

}