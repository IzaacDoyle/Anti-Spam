package com.ab.anti_spam.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.ab.anti_spam.R
import com.ab.anti_spam.databinding.UserReportCardBinding
import com.ab.anti_spam.models.CommunityBlockingModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF


interface cardClickListener{
    fun onCardClick(model: CommunityBlockingModel)
}

class CommunityUserReportAdapter constructor(private var userReportModel: MutableList<CommunityBlockingModel>,private val cardClickListener: cardClickListener) : RecyclerView.Adapter<CommunityUserReportAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityUserReportAdapter.MainHolder {
        val binding = UserReportCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MainHolder(binding)
    }


    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val userReportModels = userReportModel[holder.adapterPosition]
        holder.bind(userReportModels,cardClickListener)
    }

    override fun getItemCount(): Int = userReportModel.size

inner class MainHolder(val binding: UserReportCardBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(model: CommunityBlockingModel,cardClickListener: cardClickListener){

        binding.root.tag = model
        binding.communitymodel = model

        binding.countryText.setText(shortenCountry(model.country))

        //Comment feedback variables
        var goodCommentCount: Float = 0F
        var mediumCommentCount: Float = 0F
        var badCommentCount: Float = 0F



        val colorLow = ColorTemplate.getHoloBlue()
        val colorMedium = Color.argb(90, 255, 165, 0)
        val colorHigh = Color.argb(90, 255, 0, 0)


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

        //Coloring and decorating report card based on warning label.
        if(model.risk_Level.equals("High")){
            binding.warningText.setTextColor(Color.RED)
            binding.background.setBackgroundResource(R.drawable.icon_gradient_high)
        }
        if(model.risk_Level.equals("Medium")){
            binding.warningText.setTextColor(ColorTemplate.COLORFUL_COLORS[1])
            binding.background.setBackgroundResource(R.drawable.icon_gradient_medium)
        }
        if(model.risk_Level.equals("Low")){
            binding.warningText.setTextColor(Color.GREEN)
            binding.background.setBackgroundResource(R.drawable.icon_gradient_low)
        }

        //No description
        binding.pieChart.description.isEnabled = false
       // binding.pieChart.legend.isEnabled = false
        binding.pieChart.setExtraOffsets(5F, 10F, 5F, 5F);

        //How fast the pie chart stops spinning.
        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);
        //Toggle pie chart hole visibility.
        binding.pieChart.setDrawHoleEnabled(true);
        //Set pie chart hole color.
        binding.pieChart.setHoleColor(Color.WHITE);

        //Sets the pie chart hole radius.
        binding.pieChart.setHoleRadius(60f);
        binding.pieChart.setTransparentCircleRadius(69f);

        //Disabling center text in pie chart.
        binding.pieChart.setDrawCenterText(false);

        binding.pieChart.setRotationAngle(0F);
        binding.pieChart.animateXY(600,600)
        //Enable rotation of the chart by touch
        binding.pieChart.setRotationEnabled(true);
        //Enabling highlighting of pie chart entry by touch.
        binding.pieChart.setHighlightPerTapEnabled(true);
        //Disabling entry labels on pie chart.
        binding.pieChart.setDrawEntryLabels(false)
        //Enabling percentage values
        binding.pieChart.setUsePercentValues(true)

        //Listener
        binding.userReportCard.setOnClickListener{cardClickListener.onCardClick(model)}
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


        if(goodCommentCount > 0){
            entries.add(PieEntry(goodCommentCount, "Low"))
            //Low
            colors.add(colorLow)
        }

        if(mediumCommentCount > 0){
            entries.add(PieEntry(mediumCommentCount, "Medium"))
            //Medium
            colors.add(colorMedium)
        }

        if(badCommentCount > 0){
            entries.add(PieEntry(badCommentCount, "High"))
            //High
            colors.add(colorHigh)
        }

        if(badCommentCount == 0F && goodCommentCount == 0F && mediumCommentCount == 0F) {
            entries.add(PieEntry(1F, "No Comments Available"))
            //Low
            colors.add(colorLow)
            binding.pieChart.isDrawHoleEnabled = false
            binding.pieChart.setDrawCenterText(true)
            binding.pieChart.setCenterTextSize(13f)
            binding.pieChart.setDrawSlicesUnderHole(true)
            dataSet.setDrawValues(false)
            binding.pieChart.setHoleRadius(80f);
            binding.pieChart.setExtraOffsets(2F, 5F, 2F, 2F);
            binding.pieChart.centerText = "No\nComments"
            binding.pieChart.setCenterTextColor(Color.WHITE)
        }

        if(entries.size == 1){
            binding.pieChart.setHoleRadius(15f);
            binding.pieChart.setTransparentCircleRadius(25f);
        }

        //Apply dataSet to PieData
        val data = PieData(dataSet)
        //To append % symbol on value entries - https://github.com/PhilJay/MPAndroidChart/issues/2124
        data.setValueFormatter(PercentFormatter())
        //Apply data to Pie chart.
        binding.pieChart.data = data

        //Setting value lines
        dataSet.valueLinePart1Length = 0.2F
        dataSet.valueLinePart2Length  = 0.4F

        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            dataSet.valueLineColor = Color.WHITE
            dataSet.valueTextColor = Color.WHITE
            binding.pieChart.legend.textColor = Color.WHITE
        }
        //Check if there are 3 entries
        if(goodCommentCount > 0 && mediumCommentCount > 0 && badCommentCount > 0) {
            //Automatic highlight of the highest comment value.
            if (goodCommentCount > badCommentCount && goodCommentCount > mediumCommentCount) {
                binding.pieChart.highlightValue(0F, 0);
            }
            if (mediumCommentCount > goodCommentCount && mediumCommentCount > badCommentCount) {
                binding.pieChart.highlightValue(1F, 0);
            }
            if (badCommentCount > mediumCommentCount && badCommentCount > goodCommentCount) {
                binding.pieChart.highlightValue(2F, 0);
            }
        }

        //Draw the pie chart.
        binding.pieChart.invalidate()
    }
}

    fun shortenCountry(country: String): String{
        return if(country.length > 12){
            "${country.substring(0,11)}..."
        }else{
            country
        }
    }
}
