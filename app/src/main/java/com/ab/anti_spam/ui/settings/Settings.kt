package com.ab.anti_spam.ui.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.FragmentCommunityblockingBinding
import com.ab.anti_spam.databinding.FragmentSettingsBinding
import com.ab.anti_spam.firebase.FirebaseDBManager
import com.ab.anti_spam.localstorage.LocalBlockStorage
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.CallBlacklistModel
import com.ab.anti_spam.models.SettingsModel
import com.ab.anti_spam.ui.auth.LoggedInViewModel

class Settings : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentSettingsBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val settingsViewModel : SettingsViewModel by activityViewModels()
    private lateinit var uid: String
    private lateinit var localStorage : LocalBlockStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
        localStorage = LocalBlockStorage(app)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadFireUser()
        _fragBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        settingListeners()
        disableUntilLoaded()
        return root
    }


    @SuppressLint("SuspiciousIndentation")
    private fun loadFireUser(){
        loggedInViewModel.loadSettings(app)
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner,{
            if(it!=null){
            uid=it.uid
                settingsViewModel.updateGUISettings(fragBinding,app,it.uid)
                enable()
                }
        })
    }

    fun disableUntilLoaded(){
        fragBinding.toggleSmsScan.isEnabled = false
        fragBinding.toggleDbBlocking.isEnabled = false
        fragBinding.toggleLocalBlocking.isEnabled = false
        fragBinding.togglePersonalBlocking.isEnabled = false
        fragBinding.toggleTheme.isEnabled = false
        fragBinding.toggleUnknownBlocking.isEnabled = false
        fragBinding.commentSlider.isEnabled = false
        fragBinding.downlaodButton.isEnabled = false
    }

    fun enable(){
        fragBinding.toggleSmsScan.isEnabled = true
        fragBinding.toggleDbBlocking.isEnabled = true
        if(localStorage.checkIfExists() == false) {
            fragBinding.toggleLocalBlocking.isEnabled = false
        }else{
            fragBinding.toggleLocalBlocking.isEnabled = true
        }
        fragBinding.togglePersonalBlocking.isEnabled = true
        fragBinding.toggleTheme.isEnabled = true
        fragBinding.toggleUnknownBlocking.isEnabled = true
        fragBinding.commentSlider.isEnabled = true
        fragBinding.downlaodButton.isEnabled = true
    }


 fun update(){
     val model = SettingsModel()
     if(::uid.isInitialized) {
         model.uid = uid
         model.scan_sms = fragBinding.toggleSmsScan.isChecked
         model.database_block = fragBinding.toggleDbBlocking.isChecked
         model.local_store_block = fragBinding.toggleLocalBlocking.isChecked
         model.personal_block = fragBinding.togglePersonalBlocking.isChecked
         model.theme = fragBinding.toggleTheme.isChecked
         model.unknown_block = fragBinding.toggleUnknownBlocking.isChecked
         model.community_block_num = fragBinding.commentSlider.value.toInt()
         settingsViewModel.updateSettings(model, app)
     }
 }

    fun settingListeners(){
        fragBinding.toggleSmsScan.setOnCheckedChangeListener{_,isChecked ->
            if(isChecked){
                update()

            }else{
            update()
            }
        }
        //Database block
        fragBinding.toggleDbBlocking.setOnCheckedChangeListener{_,isChecked ->
            if(isChecked){
                //If database blocking is on, dont use local blocking
                fragBinding.toggleLocalBlocking.isChecked = false
                update()
            }else{
                update()
            }
        }
        fragBinding.toggleLocalBlocking.setOnCheckedChangeListener{_,isChecked ->
            if(isChecked){
                //If local block is on, dont use database blocking.
                fragBinding.toggleDbBlocking.isChecked = false
                update()
            }else{
                update()
            }
        }
        fragBinding.togglePersonalBlocking.setOnCheckedChangeListener{_,isChecked ->
            if(isChecked){
                update()
            }else{
                update()
            }
        }
        fragBinding.toggleTheme.setOnCheckedChangeListener{_,isChecked ->
            if(isChecked){
                update()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }else{
                update()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        fragBinding.toggleUnknownBlocking.setOnCheckedChangeListener{_,isChecked ->
            if(isChecked){
                update()
            }else{
                update()
            }
        }

        fragBinding.commentSlider.addOnChangeListener{slider,value,fromUser ->
            fragBinding.commentNum.setText(value.toString().replace(".0",""))
            update()
        }

        //Check if local storage exists
        if(localStorage.checkIfExists() == false) {
            fragBinding.downlaodButton.setBackgroundColor(Color.YELLOW)
            fragBinding.downlaodButton.setOnClickListener {
                fragBinding.loading.visibility = View.VISIBLE
            fragBinding.downlaodButton.isEnabled = false
                settingsViewModel.fetchBlocklist {
                    localStorage.serialize(it)
                    fragBinding.loading.visibility = View.INVISIBLE
                    fragBinding.toggleLocalBlocking.isEnabled = true
                    fragBinding.toggleDbBlocking.isChecked = false
                    fragBinding.downlaodButton.isEnabled = false
                    fragBinding.downlaodButton.setText("Updated")
                    fragBinding.downlaodButton.setTextColor(Color.GREEN)
                }
            }
        }else{
            fragBinding.downlaodButton.isEnabled = false
            fragBinding.downlaodButton.setText("Updated")
            fragBinding.downlaodButton.setTextColor(Color.GREEN)

        }
    }


}