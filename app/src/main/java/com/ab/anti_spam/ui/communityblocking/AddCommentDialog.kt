package com.ab.anti_spam.ui.communityblocking

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.FragmentAddCommentDialogBinding
import com.ab.anti_spam.databinding.FragmentAddUserReportDialogBinding
import com.ab.anti_spam.firebase.genUID
import com.ab.anti_spam.helpers.UIDsave
import com.ab.anti_spam.models.ChooseNumberModel
import com.ab.anti_spam.models.CommunityBlockingCommentsModel
import com.ab.anti_spam.models.CommunityBlockingModel
import com.ab.anti_spam.ui.auth.LoggedInViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddCommentDialog : DialogFragment() {

    private var _fragBinding: FragmentAddCommentDialogBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private lateinit var uid: String
    private lateinit var communityModel: CommunityBlockingModel
    private val communityViewModel: CommunityblockingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        var model: CommunityBlockingModel? = bundle?.getParcelable("model")
        if (model != null) {
            communityModel = model
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        loadFireUID()
        _fragBinding = FragmentAddCommentDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        root.minimumWidth = Resources.getSystem().displayMetrics.widthPixels/ 2 + 300
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        fragBinding.AddComment.visibility = View.INVISIBLE
        populateDropDownMenus()
        listeners()

        darkTheme()
        return root
    }

    fun loadFireUID(){
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner,{
            uid=it.uid
            fragBinding.AddComment.visibility = View.VISIBLE
        })
    }

    fun populateDropDownMenus(){
        val warningAdapter = ArrayAdapter(requireContext(),R.layout.list_countries,resources.getStringArray(R.array.warning))
        fragBinding.warningSelect.setAdapter(warningAdapter)
    }

    @SuppressLint("SimpleDateFormat")
    fun listeners(){
        fragBinding.AddComment.setOnClickListener {
            val unfocusedColor = Color.RED
            val states = arrayOf(intArrayOf(android.R.attr.state_focused))
            val colors = intArrayOf(unfocusedColor)
            val colorStateList = ColorStateList(states, colors)


            val warning = fragBinding.warningSelect.text.toString()
            val description = fragBinding.commentDescription.text.toString()
            if (warning.isEmpty()) {
                fragBinding.textInputLayoutWarning.setBoxStrokeColorStateList(colorStateList)
            }
            if (description.isEmpty() || description.length < 10) {
                fragBinding.textInputLayoutDescription.setBoxStrokeColorStateList(colorStateList)
            }

            if (warning.isNotEmpty() && description.isNotEmpty() && description.length > 10) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val date = dateFormat.format(Date())

                val reportID = communityModel.id.toString()
                val reportUID = communityModel.user_Id
                val currentUID = UIDsave.getUidFromFile(requireActivity().applicationContext).toString()

                val addSizeToID = communityModel.user_comments.size
                val comment = CommunityBlockingCommentsModel(genUID()+addSizeToID,currentUID,description,warning,date)
                communityViewModel.updateCommunityReportComments(comment,currentUID,reportID,reportUID)
                dismiss()

            }
        }

        fragBinding.cancel.setOnClickListener{
            this.dismiss()
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