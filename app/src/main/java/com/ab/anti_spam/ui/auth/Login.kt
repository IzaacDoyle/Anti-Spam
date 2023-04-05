package com.ab.anti_spam.ui.auth

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ab.anti_spam.databinding.LoginBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

//

class Login: AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    val perms = arrayOf(Manifest.permission.RECEIVE_SMS,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.READ_CALL_LOG)
    val smsPermissions = arrayOf(Manifest.permission.RECEIVE_SMS)
    val windowPermissions = arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW)
    val callLogPermissions = arrayOf(Manifest.permission.READ_CALL_LOG)

    private lateinit var loginViewModel : LoginViewModel
    private lateinit var loginBinding: LoginBinding
    private lateinit var loggedInViewModel : LoggedInViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        loginBinding = LoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

    }

    public override fun onStart() {
        super.onStart()
        loginViewModel = ViewModelProvider(this,viewModelFactory { LoginViewModel(this.application,this) }).get(LoginViewModel::class.java)
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)
        loginListener()
    }



    private fun loginListener(){
       // loginBinding.welcomeButton.setOnClickListener{
            if(hasPermissions() ) {
                //If Default dialer is set, it ignores intent and responds with ACTIVITY OK, then continues.
                requestDefaultCallerIDPermissions()
            }else{
                requestPermissions()
            }
        //}
    }
    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun hasPermissions() = EasyPermissions.hasPermissions(applicationContext, *perms)
    private fun requestPermissions(){

    ActivityCompat.requestPermissions(this,perms,1)
    requestDefaultCallerIDPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        //Give the user an option to choose permissions
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            //AppSettingsDialog.Builder(this).build().show()
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
       // Toast.makeText(applicationContext,"Permissions Granted!", Toast.LENGTH_SHORT).show()
    }

    fun requestDefaultCallerIDPermissions(){
        startCallScreeningPermissionScreen(100)
    }

    //Requests permissions to change default spam app. (For Call blocking)
    fun Activity.startCallScreeningPermissionScreen(requestId: Int) {
        val roleManager = this.getSystemService(AppCompatActivity.ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        resultLauncher.launch(intent)
    }

    //Requests external overlay permissions (For SMS Warning)
    fun openOverlayActivityForResult(){
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            resultLauncher.launch(intent)
        }
    }

    //Handles default dialer app response result
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        // Call screening permissions requested first, if denied then requested again.
        // If permissions accepted then check if system can draw overlays externally,
        // If not then request overlay permissions.

        //if result code from
        if (result.resultCode == Activity.RESULT_OK) {
            if(Settings.canDrawOverlays(this)){
                    Thread.sleep(1500)
                    loginViewModel.login()
                    this.finish()
            }else{
                openOverlayActivityForResult()
            }
        }else{
            requestDefaultCallerIDPermissions()
        }


    }

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }
}