package com.ab.anti_spam.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.ActivityMainBinding
import com.ab.anti_spam.databinding.NavHeaderMainBinding
import com.ab.anti_spam.localstorage.SettingsStorage
import com.ab.anti_spam.models.SettingsModel
import com.ab.anti_spam.ui.auth.LoggedInViewModel
import com.ab.anti_spam.ui.auth.Login
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import kotlin.math.sign

class Home : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var loggedInViewModel : LoggedInViewModel
    private lateinit var navHeaderBinding : NavHeaderMainBinding
    private lateinit var settings : SettingsStorage
    private lateinit var allSettings : MutableList<SettingsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        settings = SettingsStorage(applicationContext)
        allSettings = settings.getAll()


        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_checkNumber,
            R.id.nav_call_blacklist,
            R.id.nav_sms_blacklist,
            R.id.nav_community_blocking,
            R.id.nav_learnscampatterns,
            R.id.nav_settings
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        signOut(navView)
    }

    override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)

        loggedInViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null) {
                val currentUser = loggedInViewModel.liveFirebaseUser.value
                if (currentUser != null){
                    updateNavHeader(loggedInViewModel.liveFirebaseUser.value!!)
                    for (i in allSettings){
                        if(i.uid.equals(currentUser.uid)){
                            if(i.theme == false){
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                        }
                    }
                }

            }
        })
        loggedInViewModel.loggedOut.observe(this, Observer { loggedout ->
            if (loggedout) {
                startActivity(Intent(this, Login::class.java))
                this.finish()
            }
        })
    }

    fun updateNavHeader(currentUser: FirebaseUser){
        //NavHeaderBinding
        val navigationHeader = binding.navView.getHeaderView(0)
        navHeaderBinding = NavHeaderMainBinding.bind(navigationHeader)

        //Check if email verified.
        if(currentUser.isEmailVerified){
            navHeaderBinding.username.text = currentUser.email
            try{
                //Setting image
                Glide.with(this).load(currentUser.photoUrl).into(navHeaderBinding.imageView)
            }catch (e: Exception){
                println("Email image setting failed")
            }
        }
        //Check if signed in as guest
        if(currentUser.isAnonymous){
            navHeaderBinding.username.setText("Logged in as Guest")
        }
        //Check if phone verified.
        if(!currentUser.isAnonymous && !currentUser.isEmailVerified){
            navHeaderBinding.username.setText(currentUser.phoneNumber)
        }
    }

    fun signOut(navView: NavigationView){
        val menu: Menu = navView.menu
        val signOutMenuItem = menu.findItem(R.id.nav_logout)
        signOutMenuItem.setOnMenuItemClickListener {
            loggedInViewModel.logOut()
            this.finish()
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}