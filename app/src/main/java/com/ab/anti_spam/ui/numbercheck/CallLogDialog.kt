package com.ab.anti_spam.ui.numbercheck

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CallLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.ab.anti_spam.adapters.ReportChooseNumberAdapter
import com.ab.anti_spam.adapters.chooseNumberListener
import com.ab.anti_spam.databinding.FragmentCallLogDialogBinding
import com.ab.anti_spam.models.ChooseNumberModel
import java.text.SimpleDateFormat
import java.util.*

class CallLogDialog : DialogFragment(), chooseNumberListener {

    private var _fragBinding: FragmentCallLogDialogBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var adapter: ReportChooseNumberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragBinding = FragmentCallLogDialogBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        root.minimumWidth = Resources.getSystem().displayMetrics.widthPixels/ 2 + 300
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        renderRecyclerView(getRecentCalls())

        return root
    }


    private fun renderRecyclerView(model: ArrayList<ChooseNumberModel>){
        fragBinding.choosenumberRecyclerview.adapter?.notifyDataSetChanged()
        fragBinding.choosenumberRecyclerview.layoutManager = LinearLayoutManager(activity)
        fragBinding.choosenumberRecyclerview.adapter = ReportChooseNumberAdapter(model,this)
        adapter = fragBinding.choosenumberRecyclerview.adapter as ReportChooseNumberAdapter
    }

    @SuppressLint("Range")
    fun getRecentCalls() : ArrayList<ChooseNumberModel> {
        val recentCalls  = ArrayList<ChooseNumberModel>()

        val callLogUri = CallLog.Calls.CONTENT_URI
        val variables = arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE)

        val cursor = context?.contentResolver?.query(callLogUri,variables,null,null, CallLog.Calls.DEFAULT_SORT_ORDER)

        if(cursor != null && cursor.count > 0){
            while (cursor.moveToNext()){
                val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                var cached_name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
                val date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))

                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateString = dateFormatter.format(Date(date))

                if(cached_name == null || cached_name.equals("null")){
                    cached_name = "Contact not saved"
                }

                val model = ChooseNumberModel(number,dateString, cached_name)
                if(!recentCalls.any { it.number == number }) {
                    recentCalls.add(model)
                }
            }
            cursor.close()
        }
        return recentCalls
    }


    override fun onChoose(model: ChooseNumberModel) {
        val bundle = Bundle()
        bundle.putString("number",model.number.replace("+",""))
        setFragmentResult("number",bundle)
        this.dismiss()
    }


}