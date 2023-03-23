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
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.FragmentCallblacklistBinding
import com.ab.anti_spam.databinding.FragmentCheckNumberBinding
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CommunityBlockingModel
import com.ab.anti_spam.ui.callblacklist.CallblacklistViewModel
import com.ab.anti_spam.ui.communityblocking.ReportExistsVisitDialog
import com.github.mikephil.charting.utils.ColorTemplate


class CheckNumber : Fragment() {
    private var _fragBinding: FragmentCheckNumberBinding? = null
    private val checkNumberViewModel: CheckNumberViewModel by activityViewModels()
    private val fragBinding get() = _fragBinding!!
    private var model: CommunityBlockingModel? = null
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
        viewCommunityReport()
        openCalllog()
        dialogCallback()

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
                    fragBinding.countryText.setText(getCountryAbbreviation(it.country))
                    fragBinding.riskText.setText(it.risk_Level)
                    setRiskColor(it.risk_Level)
                    fragBinding.ctiquesText.setText(it.user_comments.size.toString())
                    fragBinding.viewReport.visibility = View.VISIBLE
                    fragBinding.search.isEnabled = true
                    this.model = it
                } else {
                    checkNumberViewModel.checkFireStoreNumbers(number, {
                        if (it != null) {
                            //Found from Fire store (Large Dataset)
                            fragBinding.responseNumberText.setText(it.number)
                            if (it.country.equals("Unknown")) it.country = "??"
                            fragBinding.countryText.setText(getCountryAbbreviation(it.country))
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
                            fragBinding.riskText.setTextColor(ContextCompat.getColor(requireContext(),R.color.blueish))
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

    fun viewCommunityReport(){
        fragBinding.viewReport.setOnClickListener{
            if(this.model != null){
                val bundle = Bundle()
                bundle.putParcelable("model",this.model)
                findNavController().navigate(R.id.action_nav_checkNumber_to_communityViewReport,bundle)
            }
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

    fun getCountryAbbreviation(country: String): String {
        return when(country) {
            "Afghanistan" -> "AFG"
            "American Samoa" -> "ASM"
            "Anguilla" -> "AIA"
            "Antarctica" -> "ATA"
            "Antigua and Barbuda" -> "ATG"
            "Argentina" -> "ARG"
            "Australia" -> "AUS"
            "Azerbaijan" -> "AZE"
            "Bangladesh" -> "BGD"
            "Barbados" -> "BRB"
            "British Virgin Islands" -> "VGB"
            "British Indian Ocean Territory" -> "IOT"
            "Burkina Faso" -> "BFA"
            "Central African Republic" -> "CAF"
            "Christmas Island" -> "CXR"
            "Cocos Islands" -> "CCK"
            "Cook Islands" -> "COK"
            "Costa Rica" -> "CRI"
            "Czech Republic" -> "CZE"
            "Democratic Republic of the Congo" -> "COD"
            "Dominican Republic" -> "DOM"
            "East Timor" -> "TLS"
            "El Salvador" -> "SLV"
            "Equatorial Guinea" -> "GNQ"
            "Falkland Islands" -> "FLK"
            "Faroe Islands" -> "FRO"
            "French Polynesia" -> "PYF"
            "Gibraltar" -> "GIB"
            "Greenland" -> "GRL"
            "Guatemala" -> "GTM"
            "Guinea-Bissau" -> "GNB"
            "Hong Kong" -> "HKG"
            "Indonesia" -> "IDN"
            "Isle of Man" -> "IMN"
            "Ivory Coast" -> "CIV"
            "Kazakhstan" -> "KAZ"
            "Kyrgyzstan" -> "KGZ"
            "Liechtenstein" -> "LIE"
            "Lithuania" -> "LTU"
            "Luxembourg" -> "LUX"
            "Macedonia" -> "MKD"
            "Madagascar" -> "MDG"
            "Marshall Islands" -> "MHL"
            "Mauritania" -> "MRT"
            "Mauritius" -> "MUS"
            "Micronesia" -> "FSM"
            "Montenegro" -> "MNE"
            "Montserrat" -> "MSR"
            "Mozambique" -> "MOZ"
            "Netherlands" -> "NLD"
            "Netherlands Antilles" -> "ANT"
            "New Caledonia" -> "NCL"
            "New Zealand" -> "NZL"
            "Nicaragua" -> "NIC"
            "North Korea" -> "PRK"
            "Northern Mariana Islands" -> "MNP"
            "Papua New Guinea" -> "PNG"
            "Palestine" -> "PSE"
            "Philippines" -> "PHL"
            "Puerto Rico" -> "PRI"
            "Portugal" -> "PRT"
            "Republic of the Congo" -> "COG"
            "Saint Barthelemy" -> "BLM"
            "Saint Helena" -> "SHN"
            "Saint Kitts and Nevis" -> "KNA"
            "Saint Lucia" -> "LCA"
            "Saint Pierre and Miquelon" -> "SPM"
            "Saint Martin" -> "MAF"
            "Saint Vincent and the Grenadines" -> "VCT"
            "Sao Tome and Principe" -> "STP"
            "Saudi Arabia" -> "SAU"
            "Seychelles" -> "SYC"
            "Sierra Leone" -> "SLE"
            "Singapore" -> "SGP"
            "Sint Maarten" -> "SXM"
            "Solomon Islands" -> "SLB"
            "South Africa" -> "ZAF"
            "South Korea" -> "KOR"
            "South Sudan" -> "SSD"
            "Sri Lanka" -> "LKA"
            "Suriname" -> "SUR"
            "Svalbard and Jan Mayen" -> "SJM"
            "Swaziland" -> "SWZ"
            "Switzerland" -> "CHE"
            "Tajikistan" -> "TJK"
            "Tanzania" -> "TZA"
            "Trinidad and Tobago" -> "TTO"
            "Turkmenistan" -> "TKM"
            "Turks and Caicos Islands" -> "TCA"
            "U.S. Virgin Islands" -> "VIR"
            "United Arab Emirates" -> "ARE"
            "United Kingdom" -> "GBR"
            "United States" -> "USA"
            "Uzbekistan" -> "UZB"
            "Venezuela" -> "VEN"
            "Wallis and Futuna" -> "WLF"
            "Western Sahara" -> "ESH"
            "Zimbabwe" -> "ZWE"
            else -> {
                return country
            }
        }
    }

}