package com.biodigital.kotlinapp

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.AdapterView
import com.biodigital.humansdk.*
import kotlinx.android.synthetic.main.activity_main.*

val MODEL_MESSAGE = "com.biodigital.com.MODEL_MESSAGE"

class MainActivity : AppCompatActivity(), HKServicesInterface {

    private var modelAdapter : ModelAdapter? = null

    var models : ArrayList<HKModel> = arrayListOf(HKModel("Head and Neck", "production/maleAdult/male_region_head_07",  "",  "human_02_regional_male_head_neck"),
        HKModel("Thorax","production/maleAdult/male_region_thorax_07",  "", "human_02_regional_male_thorax"),
        HKModel("Ear: Coronal Cross Section", "production/maleAdult/ear_cross_section_coronal", "", "ear_cross_section_coronal"),
        HKModel("Atheriosclerosis: Total Occlusion", "production/maleAdult/atherosclerosis_total_occlusion", "", "atherosclerosis_total_occlusion"),
        HKModel("Hemorrhagic Stroke", "production/maleAdult/hemorrhagic_stroke", "", "hemorrhagic_stroke"),
        HKModel( "Breathing Dynamics", "production/maleAdult/breathing_dynamics","", "breathing_dynamics"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HKServices.getInstance().setup(this, this)
        HKServices.getInstance().getModels()
        gridview.numColumns = 3
        gridview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val model = models[position]
            if (HKServices.getInstance().modelDownloaded(model.id)) {
                System.out.println("WE ALREADY DOWNLOADED THIS!");
            } else {
                System.out.println("this isn't downloaded yet!");
            }
            val intent = Intent(this@MainActivity, HumanActivity::class.java)
            intent.putExtra(MODEL_MESSAGE, model.id)
            startActivity(intent)
        }
        modelAdapter = ModelAdapter(this, models)
        gridview.adapter = modelAdapter
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
            System.out.println("got  " +  HKServices.getInstance().models.size + " models")
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

    override fun onModelDownloaded(p0: String?) {
        System.out.println("model loaded")
    }

}
