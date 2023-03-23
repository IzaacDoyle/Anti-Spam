package com.ab.anti_spam.ui.communityblocking

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.provider.CallLog
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.marginBottom
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.ab.anti_spam.R
import com.ab.anti_spam.adapters.*
import com.ab.anti_spam.databinding.FragmentCommunityUserReportViewPagerBinding
import com.ab.anti_spam.databinding.FragmentCommunityViewReportBinding
import com.ab.anti_spam.helpers.UIDsave
import com.ab.anti_spam.main.Main
import com.ab.anti_spam.models.ChooseNumberModel
import com.ab.anti_spam.models.CommunityBlockingCommentsModel
import com.ab.anti_spam.models.CommunityBlockingModel
import com.ab.anti_spam.ui.auth.LoggedInViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommunityViewReport : Fragment(),cardCommentClickListener,deleteCommentClickListener {

    lateinit var app: Main
    private var _fragBinding: FragmentCommunityViewReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val communityViewModel: CommunityblockingViewModel by activityViewModels()
    lateinit var adapter: CommunityUserReportCommentsAdapter
    private lateinit var model: CommunityBlockingModel
    private var pieHeight : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
        val bundle = arguments

        var model: CommunityBlockingModel? = bundle?.getParcelable("model")
        if (model != null) {
            this.model = model
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragBinding = FragmentCommunityViewReportBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        pieHeight = fragBinding.pieChart.layoutParams.height

        setupMenu()
        renderRecyclerView()



        setupPieChart(this.model)
        fragBinding.header.setText(model.reported_phone_number)
        fragBinding.description.setText(model.report_Description)
        fragBinding.date.setText(model.date)
        fragBinding.country.setText(model.country)

        return root
    }

    private fun renderRecyclerView(){
        fragBinding.commentRecyclerview.layoutManager = LinearLayoutManager(activity)
        val currentUID = communityViewModel.UID.value
        if(currentUID != null) {
            fragBinding.commentRecyclerview.adapter = CommunityUserReportCommentsAdapter(
                this.model.user_comments as ArrayList<CommunityBlockingCommentsModel>,
                this,this,currentUID
            )
        }else{
            //Repeat whats above from get UID from file.
            val currentUIDFromFile = UIDsave.getUidFromFile(app.applicationContext)
            fragBinding.commentRecyclerview.adapter = CommunityUserReportCommentsAdapter(
                this.model.user_comments as ArrayList<CommunityBlockingCommentsModel>,
                this,this,currentUIDFromFile!!)
        }
        adapter = fragBinding.commentRecyclerview.adapter as CommunityUserReportCommentsAdapter
    }

    private fun renderRecyclerViewAfterAddingComment(){
        communityViewModel.getUserReportById(model.id.toString())
        communityViewModel.observableCommunityReportComments.observe(viewLifecycleOwner) { it ->
            it.let {
                if (it != null) {
                    if (it.user_comments.size > 0) {
                        this.model = it
                        val currentUID = UIDsave.getUidFromFile(requireActivity().applicationContext).toString()
                            fragBinding.commentRecyclerview.layoutManager =
                                LinearLayoutManager(activity)
                            fragBinding.commentRecyclerview.adapter =
                                CommunityUserReportCommentsAdapter(
                                    it.user_comments as ArrayList<CommunityBlockingCommentsModel>, this,this, currentUID)


                        adapter = fragBinding.commentRecyclerview.adapter as CommunityUserReportCommentsAdapter
                        setupPieChart(it)
                        fragBinding.commentRecyclerview.visibility = View.VISIBLE
                        fragBinding.pieChart.legend.position = Legend.LegendPosition.RIGHT_OF_CHART
                        fragBinding.pieChart.legend.textSize = 15F
                        fragBinding.pieChart.setCenterTextSize(13f)
                        fragBinding.pieChart.layoutParams.height = pieHeight

                    }
                }
            }
        }
    }


    private fun setupPieChart(model: CommunityBlockingModel){

        //Comment feedback variables
        var goodCommentCount: Float = 0F
        var mediumCommentCount: Float = 0F
        var badCommentCount: Float = 0F


        if(model.risk_Level.equals("High")){
            fragBinding.root.setBackgroundResource(R.drawable.icon_gradient_high_flipped)
        }
        if(model.risk_Level.equals("Medium")){
            fragBinding.root.setBackgroundResource(R.drawable.icon_gradient_medium_flipped)
        }
        if(model.risk_Level.equals("Low")){
            fragBinding.root.setBackgroundResource(R.drawable.icon_gradient_low_flipped)
        }

        //Incrementing feedback variables based on comment warning levels.
        for(i in model.user_comments){
            if(i.risk_Level.equals("High")){
                badCommentCount++
            }
            if(i.risk_Level.equals("Medium")){
                mediumCommentCount++
            }
            if(i.risk_Level.equals("Low")){
                goodCommentCount++
            }
        }

        //Preventing outliers
        if(goodCommentCount > 10){
            goodCommentCount = mediumCommentCount+badCommentCount+2F
        }
        if(mediumCommentCount > 10){
            mediumCommentCount = badCommentCount+goodCommentCount+2F
        }
        if(badCommentCount > 10){
            badCommentCount = goodCommentCount+mediumCommentCount+2F
        }


        val colorLow = ColorTemplate.getHoloBlue()
        val colorMedium = Color.argb(90, 255, 165, 0)
        val colorHigh = Color.argb(90, 255, 0, 0)

        //No description
        fragBinding.pieChart.description.isEnabled = false
        // binding.pieChart.legend.isEnabled = false
        fragBinding.pieChart.setExtraOffsets(5F, 10F, 5F, 5F);

        //How fast the pie chart stops spinning.
        fragBinding.pieChart.setDragDecelerationFrictionCoef(0.95f);
        //Toggle pie chart hole visibility.
        fragBinding.pieChart.setDrawHoleEnabled(true);
        //Set pie chart hole color.
        fragBinding.pieChart.setHoleColor(Color.WHITE);

        //Sets the pie chart hole radius.
        fragBinding.pieChart.setHoleRadius(30f);
        fragBinding.pieChart.setTransparentCircleRadius(37f);

        //Disabling center text in pie chart.
        fragBinding.pieChart.setDrawCenterText(false);

        fragBinding.pieChart.setRotationAngle(0F);
        fragBinding.pieChart.animateXY(600,600)
        //Enable rotation of the chart by touch
        fragBinding.pieChart.setRotationEnabled(true);
        //Enabling highlighting of pie chart entry by touch.
        fragBinding.pieChart.setHighlightPerTapEnabled(true);
        //Disabling entry labels on pie chart.
        fragBinding.pieChart.setDrawEntryLabels(false)
        //Enabling percentage values
        fragBinding.pieChart.setUsePercentValues(true)

        val entries = ArrayList<PieEntry>()
        val dataSet = PieDataSet(entries, "")

        //Toggle values
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 10F

        dataSet.sliceSpace = 8f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.setSelectionShift(5f);
        val colors: ArrayList<Int> = ArrayList()

        //Set colors to Dataset
        dataSet.colors = colors

        if(badCommentCount > 0){
            entries.add(PieEntry(badCommentCount, "High"))
            //High
            colors.add(colorHigh)

        }

        if(mediumCommentCount > 0){
            entries.add(PieEntry(mediumCommentCount, "Medium"))
            //Medium
            colors.add(colorMedium)

        }

        if(goodCommentCount > 0){
            entries.add(PieEntry(goodCommentCount, "Low"))
            //Low
            colors.add(colorLow)
        }

        if(badCommentCount == 0F && goodCommentCount == 0F && mediumCommentCount == 0F) {
            entries.add(PieEntry(1F, "No Comments Available"))
            //Low
            colors.add(ColorTemplate.getHoloBlue())
            fragBinding.pieChart.isDrawHoleEnabled = false
            fragBinding.pieChart.setDrawCenterText(true)
            fragBinding.pieChart.setCenterTextSize(13f)
            fragBinding.pieChart.setDrawSlicesUnderHole(true)
            dataSet.setDrawValues(false)
            fragBinding.pieChart.setHoleRadius(80f);
            fragBinding.pieChart.setExtraOffsets(2F, 5F, 2F, 2F);
            fragBinding.pieChart.centerText = "No\nComments"
            fragBinding.pieChart.setCenterTextColor(Color.WHITE)
        }


        if(entries.size == 1){
            fragBinding.pieChart.setHoleRadius(15f);
            fragBinding.pieChart.setTransparentCircleRadius(25f);
        }

        //Apply dataSet to PieData
        val data = PieData(dataSet)
        //To append % symbol on value entries - https://github.com/PhilJay/MPAndroidChart/issues/2124
        data.setValueFormatter(PercentFormatter())
        //Apply data to Pie chart.
        fragBinding.pieChart.data = data

        //Setting value lines
        dataSet.valueLinePart1Length = 0.6F
        dataSet.valueLinePart2Length  = 0.6F
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        //Check if there are 3 entries
        if(entries.size == 3) {
            //Automatic highlight of the highest comment value.
            if (goodCommentCount > badCommentCount && goodCommentCount > mediumCommentCount) {
                fragBinding.pieChart.highlightValue(2F, 0);
            }
            if (mediumCommentCount > goodCommentCount && mediumCommentCount > badCommentCount) {
                fragBinding.pieChart.highlightValue(1F, 0);
            }
            if (badCommentCount > mediumCommentCount && badCommentCount > goodCommentCount) {
                fragBinding.pieChart.highlightValue(0F, 0);
            }
        }

        //Draw legend to the right if there is more than 0 comments
        if(this.model.user_comments.size == 0) {

            fragBinding.commentRecyclerview.visibility = View.INVISIBLE
            fragBinding.pieChart.legend.position = Legend.LegendPosition.BELOW_CHART_CENTER
            fragBinding.pieChart.legend.textSize = 19F
            fragBinding.pieChart.setCenterTextSize(18.5F)
            fragBinding.pieChart.layoutParams.height = Resources.getSystem().displayMetrics.heightPixels /2
           // val margin = fragBinding.pieChart.layoutParams as ViewGroup.MarginLayoutParams
            //margin.bottomMargin = 200
        }else{
            fragBinding.pieChart.legend.position = Legend.LegendPosition.RIGHT_OF_CHART
            fragBinding.pieChart.legend.textSize = 15F
            fragBinding.pieChart.setCenterTextSize(13f)
        }

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            dataSet.valueLineColor = Color.WHITE
            dataSet.valueTextColor = Color.WHITE
            fragBinding.pieChart.legend.textColor = Color.WHITE
        }

        //Draw the pie chart.
        fragBinding.pieChart.invalidate()
    }


    private fun setupMenu(){

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (menu is MenuBuilder){ (menu as MenuBuilder).setOptionalIconsVisible(true)}

                menuInflater.inflate(R.menu.community_view_report_menu,menu)
                menu.findItem(R.id.delete_report).setVisible(false)
                menu.findItem(R.id.edit_report).setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when(menuItem.itemId){
                    R.id.add_comment ->{
                        addComment()
                    }
                }

                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun addComment(){
            if(checkIfNumberAssociated(model.reported_phone_number.replace("+","").trim()) == true){

                if(checkCommentedAlready() == false) {
                    val bundle = Bundle()
                    bundle.putParcelable("model", model)
                    val addCommentDialog = AddCommentDialog()
                    addCommentDialog.arguments = bundle
                    findNavController().navigate(R.id.action_communityViewReport_to_addCommentDialog, bundle)
                    renderRecyclerViewAfterAddingComment()
                }else{
                    AlreadyCommentedErrorDialog().show(parentFragmentManager,null)
                }

            }else{
                AddCommentErrorDialog().show(parentFragmentManager,null)
            }

    }



    @SuppressLint("Range")
    fun checkIfNumberAssociated(report_number: String) : Boolean{
        val callLogUri = CallLog.Calls.CONTENT_URI
        val variables = arrayOf(CallLog.Calls.NUMBER)

        val cursor = context?.contentResolver?.query(callLogUri,variables,null,null, CallLog.Calls.DEFAULT_SORT_ORDER)

        if(cursor != null && cursor.count > 0){
            while (cursor.moveToNext()){
                val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)).replace("+","").trim()
                if(number.equals(report_number)){
                    return true
                }
            }
            cursor.close()
        }
        return false
    }

    fun checkCommentedAlready() : Boolean{
        var currentUID : String? = communityViewModel.UID.value
        if(currentUID == null){
            currentUID = UIDsave.getUidFromFile(app.applicationContext)
        }
        for(i in model.user_comments){
            if(currentUID!!.trim().equals(i.user_Id_comment.toString().trim())){
                return true
            }
        }
        return false
    }

    override fun onCardClick(model: CommunityBlockingCommentsModel) {
        val bundle = Bundle()
        bundle.putParcelable("commentsmodel",model)
        val viewCommentDialog = ViewCommentDialog()
        viewCommentDialog.arguments = bundle
        viewCommentDialog.show(parentFragmentManager,null)
        //findNavController().navigate(R.id.action_communityViewReport_to_viewCommentDialog,bundle)
    }

    fun updateAfterDelete(model: CommunityBlockingCommentsModel){
        this.model.user_comments.remove(model)
        setupPieChart(this.model)
    }

    override fun onDeleteClick(model: CommunityBlockingCommentsModel) {
        communityViewModel.deleteComment(model,this.model.user_Id,this.model.id.toString())
        adapter.removeItem(model)
        updateAfterDelete(model)
    }


}