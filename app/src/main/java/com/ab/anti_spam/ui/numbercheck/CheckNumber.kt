package com.ab.anti_spam.ui.numbercheck

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.FragmentCallblacklistBinding
import com.ab.anti_spam.databinding.FragmentCheckNumberBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.ui.callblacklist.CallblacklistViewModel
import com.github.mikephil.charting.utils.ColorTemplate


class CheckNumber : Fragment() {
    lateinit var app: Main
    private var _fragBinding: FragmentCheckNumberBinding? = null
    private val checkNumberViewModel: CheckNumberViewModel by activityViewModels()
    private val fragBinding get() = _fragBinding!!
    val colorLow = ColorTemplate.getHoloBlue()
    val colorMedium = Color.argb(180, 255, 165, 0)
    val colorHigh = Color.argb(180, 255, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragBinding = FragmentCheckNumberBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        fragBinding.viewReport.visibility = View.INVISIBLE

        fragBinding.numberText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragBinding.responseNumberText.setText(s.toString())
                if(fragBinding.responseNumberText.length() == 0){
                    fragBinding.responseNumberText.setText("Search Number")
                }
            }



        })

        checkDatabase()

        return root
    }


@SuppressLint("SetTextI18n")
fun checkDatabase(){
    fragBinding.search.setOnClickListener{
        fragBinding.viewReport.visibility = View.INVISIBLE
        fragBinding.search.isEnabled = false
        fragBinding.responseNumberText.setText("Searching, please wait...")
        val number = "+"+fragBinding.numberText.text.toString()
        if(number.length > 5) {
            checkNumberViewModel.checkCommunityNumbers(number, {

                if (it != null) {
                    //Found from community reports
                    fragBinding.responseNumberText.setText(it.reported_phone_number)
                    fragBinding.countryText.setText(it.country)
                    fragBinding.riskText.setText(it.risk_Level)
                    setRiskColor(it.risk_Level)
                    fragBinding.ctiquesText.setText(it.user_comments.size.toString())
                    fragBinding.viewReport.visibility = View.VISIBLE
                    fragBinding.search.isEnabled = true

                } else {
                    checkNumberViewModel.checkFireStoreNumbers(number, {
                        if (it != null) {
                            //Found from Fire store (Large Dataset)
                            fragBinding.responseNumberText.setText(it.number)
                            if (it.country.equals("Unknown")) it.country = "??"
                            fragBinding.countryText.setText(it.country)
                            fragBinding.riskText.setText(it.risk)
                            setRiskColor(it.risk)
                            fragBinding.ctiquesText.setText(it.user_comments)
                            fragBinding.search.isEnabled = true

                        } else {
                            //Nothing found
                            fragBinding.responseNumberText.setText("Number not Found")
                            fragBinding.countryText.setText("-")
                            fragBinding.riskText.setText("-")
                            fragBinding.ctiquesText.setText("-")
                            fragBinding.search.isEnabled = true
                            fragBinding.riskText.setTextColor(ContextCompat.getColor(app.applicationContext,R.color.blueish))
                        }
                    })
                }
            })
        }else{
            fragBinding.responseNumberText.setText("Incorrect Number Format.")
            fragBinding.search.isEnabled = true
        }

    }
}

fun setRiskColor(risk: String){
    if(risk.equals("Low")){
        fragBinding.riskText.setTextColor(colorLow)
    }
    if(risk.equals("Medium")){
        fragBinding.riskText.setTextColor(colorMedium)
    }
    if(risk.equals("High")){
        fragBinding.riskText.setTextColor(colorHigh)
    }
}

}