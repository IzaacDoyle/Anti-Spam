package com.ab.anti_spam.ui.communityblocking

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.FragmentAddBlacklistQuestionDialogBinding
import com.ab.anti_spam.databinding.FragmentReportExistsVisitDialogBinding
import com.ab.anti_spam.models.CommunityBlockingModel

class ReportExistsVisitDialog : DialogFragment() {

    private var _fragBinding: FragmentReportExistsVisitDialogBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var model: CommunityBlockingModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments

        val model: CommunityBlockingModel? = bundle?.getParcelable("model")
        if (model != null) {
            this.model = model
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragBinding = FragmentReportExistsVisitDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        root.minimumWidth = Resources.getSystem().displayMetrics.widthPixels/ 2 + 300
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        listener()

        return root
    }

    fun listener(){
        fragBinding.viewReport.setOnClickListener{
            val bundle = Bundle()
            bundle.putParcelable("model",this.model)
            findNavController().navigate(R.id.action_nav_community_blocking_to_communityViewReport,bundle)
            this.dismiss()

        }
        fragBinding.goBack.setOnClickListener{
            val chooseNumberDialogDialog = AddUserReportChooseNumberDialog()
            chooseNumberDialogDialog.show(parentFragmentManager,null)
        }
        fragBinding.cancel.setOnClickListener{
            this.dismiss()
        }

    }

}