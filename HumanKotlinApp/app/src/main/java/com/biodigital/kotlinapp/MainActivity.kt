package com.biodigital.kotlinapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.biodigital.humansdk.*
import com.biodigital.kotlinapp.databinding.ActivityMainBinding

val MODEL_MESSAGE = "com.biodigital.com.MODEL_MESSAGE"

class MainActivity : AppCompatActivity(), HKServicesInterface {

    private var modelAdapter : ModelAdapter? = null

    var models : ArrayList<HKModel> = arrayListOf(
        HKModel("Head and Neck", "production/maleAdult/male_region_head_07",  "",  "human_02_regional_male_head_neck"),
        HKModel("Thorax","production/maleAdult/male_region_thorax_07",  "", "human_02_regional_male_thorax"),
        HKModel("Ear: Coronal Cross Section", "production/maleAdult/ear_cross_section_coronal", "", "ear_cross_section_coronal"),
        HKModel("Atheriosclerosis: Total Occlusion", "production/maleAdult/atherosclerosis_total_occlusion", "", "atherosclerosis_total_occlusion"),
        HKModel("Hemorrhagic Stroke", "production/maleAdult/hemorrhagic_stroke", "", "hemorrhagic_stroke"),
        HKModel( "Breathing Dynamics", "production/maleAdult/breathing_dynamics","", "breathing_dynamics"))

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        HKServices.getInstance().setup(this, this)
        HKServices.getInstance().getModels()
        binding.gridview.numColumns = 3
        binding.gridview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val model = models[position]
            if (HKServices.getInstance().modelDownloaded(model.id)) {
                System.out.println("model already downloaded");
            } else {
                System.out.println("begin model download");
            }
            val intent = Intent(this@MainActivity, HumanActivity::class.java)
            intent.putExtra(MODEL_MESSAGE, model.id)
            startActivity(intent)
        }
        modelAdapter = ModelAdapter(this, models)
        binding.gridview.adapter = modelAdapter
    }

    /**
     * API Callback - SDK failed validation
     */
    override fun onInvalidSDK() {
        println("error!  we aren't authenticated with BioDigital!")
    }

    /**
     * API Callback - SDK succeeded validation
     */
    override fun onValidSDK() {
        println("success!  we are authenticated with BioDigital!")
    }

    /**
     * API Callback - dashboard models loaded
     */
    override fun onModelsLoaded() {
        this@MainActivity.runOnUiThread {
            System.out.println("received  " +  HKServices.getInstance().models.size + " models")
            if (HKServices.getInstance().models.size > 0) {
                models.addAll(HKServices.getInstance().models)
                modelAdapter!!.notifyDataSetChanged()
            } else {
                val builder : AlertDialog.Builder? = this.let {
                    AlertDialog.Builder(it)
                }
                builder?.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    System.out.println("OK pressed")
                })
                builder?.setMessage("Dashboard is empty")
                builder?.setTitle("Please go to https://human.biodigital.com/ to add models to your Dashboard")
                val dialog : AlertDialog? = builder?.create()
                dialog!!.show()
            }
        }
    }

    override fun onModelDownloaded(p0: String?, p1: Int, p2: Int) {
        System.out.println("model download success")
    }

    override fun onModelDownloadError(p0: String?) {
        System.out.println("model download error")
    }

}
