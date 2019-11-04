package com.biodigital.kotlinapp

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import com.biodigital.humansdk.*
import kotlinx.android.synthetic.main.activity_main.*

val MODULE_MESSAGE = "com.biodigital.com.MODULE_MESSAGE"

class MainActivity : AppCompatActivity(), HKServicesInterface {

    private var mind : HKServices? = null
    private var moduleAdapter : ModuleAdapter? = null

    var modules : ArrayList<HKModule> = arrayListOf(HKModule("Head and Neck", "production/maleAdult/human_02_regional_male_head_neck.json",  "",  "human_02_regional_male_head_neck"),
                                                    HKModule("Thorax","production/maleAdult/human_02_regional_male_thorax.json",  "", "human_02_regional_male_thorax"),
                                                    HKModule("Ear: Coronal Cross Section", "production/maleAdult/ear_cross_section_coronal.json", "", "ear_cross_section_coronal"),
                                                    HKModule("Atheriosclerosis: Total Occlusion", "production/maleAdult/atherosclerosis_total_occlusion.json", "", "atherosclerosis_total_occlusion"),
                                                    HKModule("Hemorrhagic Stroke", "production/maleAdult/hemorrhagic_stroke.json", "", "hemorrhagic_stroke"),
                                                    HKModule( "Breathing Dynamics", "production/maleAdult/breathing_dynamics.json","", "breathing_dynamics"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mind = HKServices(this,"<YOUR KEY>", "<YOUR SECRET>")
        mind!!.setInterface(this)
        mind!!.getModules()
        gridview.numColumns = 3
        gridview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val module = modules[position]
            if (mind!!.moduleDownloaded(module.id)) {
                System.out.println("WE ALREADY DOWNLOADED THIS!");
            } else {
                System.out.println("this isn't downloaded yet!");
            }
            val intent = Intent(this@MainActivity, HumanActivity::class.java)
            intent.putExtra(MODULE_MESSAGE, module.id)
            startActivity(intent)
        }
        moduleAdapter = ModuleAdapter(this, modules)
        gridview.adapter = moduleAdapter
    }

    /**
     * API Callback - SDK failed validation
     */
    override fun onInvalidSDK() {
        println("error!  we aren't validated with BioDigital!")
    }

    /**
     * API Callback - SDK succeeded validation
     */
    override fun onValidSDK() {
        println("success!  we are validated with BioDigital!")
    }

    /**
     * API Callback - dashboard modules loaded
     */
    override fun onModulesLoaded() {
        this@MainActivity.runOnUiThread {
            System.out.println("got modules " + mind!!.modules.size)
            if (mind!!.modules.size > 0) {
                modules.addAll(mind!!.modules)
                moduleAdapter!!.notifyDataSetChanged()
            } else {
                val builder : AlertDialog.Builder? = this.let {
                    AlertDialog.Builder(it)
                }
                builder?.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        System.out.println("OK pressed")
                    })
                builder?.setMessage("Dashboard is empty")
                builder?.setTitle("Please go to https://human.biodigital.com/ to add modules to your Dashboard")
                val dialog : AlertDialog? = builder?.create()
                dialog!!.show()
            }
        }
    }

}
