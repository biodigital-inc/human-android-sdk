package com.biodigital.kotlinapp

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.biodigital.humansdk.HKChapter
import com.biodigital.humansdk.HKColor
import com.biodigital.humansdk.HKHuman
import com.biodigital.humansdk.HKHumanInterface
import com.biodigital.humansdk.HKModel
import com.biodigital.humansdk.HKServices
import com.biodigital.humansdk.HKServicesInterface
import com.biodigital.humansdk.HumanUIOptions
import com.biodigital.kotlinapp.databinding.ActivityMainBinding

val MODEL_MESSAGE = "com.biodigital.com.MODEL_MESSAGE"

class MainActivity : AppCompatActivity(), HKServicesInterface, HKHumanInterface {

    private lateinit var binding: ActivityMainBinding

    private var modelAdapter : ModelAdapter? = null

    var humanView: RelativeLayout? = null
    var libraryView: RelativeLayout? = null

    private var human: HKHuman? = null

    private var xraymode = false
    private var isolatemode = false
    private var dissectmode = false
    private var paintmode = false

    internal var paintColor : HKColor? = null

    internal var redColor = HKColor()
    internal var greenColor = HKColor()
    internal var blueColor = HKColor()
    internal var yellowColor = HKColor()

    internal var expanded = false

    var chapterPager: ViewPager? = null
    var topMenu : Menu? = null

    // this toggles hiding and showing the viewer's built in UI
    // set NATIVE_UI to true if you want to show your own UI instead
    var NATIVE_UI = false

    var models : ArrayList<HKModel> = arrayListOf(
        HKModel("Alzheimers Disease", "production/femaleAdult/alzheimers_disease",  "",  "https://human.biodigital.com/media/images/469e0d37-6088-4b64-8269-833b89d77a5b/small/image.jpg"),
        HKModel("Esophageal Varices","production/maleAdult/esophageal_varices",  "", "https://human.biodigital.com/media/images/9b201b08-089e-44ac-8a80-9679453ffc3c/small/image.jpg"),
        HKModel("Hip Replacement", "production/maleAdult/posterior_total_hip_replacement", "", "https://human.biodigital.com/media/images/05ba4b17-b49f-4cd9-b8be-cada95f41698/small/image.jpg"),
        HKModel("Cell", "production/maleAdult/cell", "", "https://human.biodigital.com/media/images/b4f221d7-8863-4765-ade3-938dc248a18b/small/image.jpg"),
        HKModel("Carotid Sheath", "production/maleAdult/contents_of_carotid_sheath_guided", "", "https://human.biodigital.com/media/images/c3541b60-afaf-4705-9e03-a6106b606749/small/image.jpg"),
        HKModel("Breast Cancer", "production/femaleAdult/breast_cancer_dark_skin","", "https://human.biodigital.com/media/images/e556e58f-ca21-4b38-8161-7ea1dac46f95/small/image.jpg"),
        HKModel("Kidney Stones", "production/maleAdult/kidney_stones_03","", "https://human.biodigital.com/media/images/f3af546a-1699-4304-a5b1-5b46bf6a03bc/small/image.jpg"),
        HKModel("Thrombolytics", "production/maleAdult/thrombolytics","", "https://human.biodigital.com/media/images/521a294b-ba36-4725-b69e-db713c35b801/small/image.jpg"),
        HKModel("Brain", "production/maleAdult/male_region_brain_19","", "https://human.biodigital.com/media/images/298286aa-a126-4ea2-a902-3b6716536dae/small/image.jpg"),
        HKModel("Skin", "production/maleAdult/skin_tissue","", "https://human.biodigital.com/media/images/ee7db82f-2228-40c3-a427-769168bb98df/small/image.jpg"),
    )

    fun ui_visibility(): Int = if (NATIVE_UI) View.VISIBLE else View.INVISIBLE;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HKServices.getInstance().setup(this, this);
        // call getModels to pull your organization's saved library for display in your application
        //        HKServices.getInstance().getModels()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        humanView = binding.humanView
        libraryView = binding.libraryView
        humanView!!.visibility = View.GONE
        libraryView!!.visibility = View.VISIBLE
        supportActionBar!!.title = "BioDigital SDK Demo App"

        binding.gridview.numColumns = 3
        binding.gridview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val model = models[position]
            humanView!!.visibility = View.VISIBLE
            libraryView!!.visibility = View.GONE
            topMenu!!.findItem(R.id.action_back).setVisible(true)
            // use presetBackgroundColor to set the desired background before loading
            // human!!.ui.presetBackgroundColor(Color.BLUE, Color.GREEN, "linear");
            human!!.load(model.id)
            if (NATIVE_UI) {
                val menu = humanView!!.findViewById<View>(R.id.uimenu)
                menu.visibility = View.VISIBLE
                menu.bringToFront()
                val chap = humanView!!.findViewById<View>(R.id.category)
                chap.bringToFront()
            }
        }
        modelAdapter = ModelAdapter(this, models)
        binding.gridview.adapter = modelAdapter

        val menu = humanView!!.findViewById<View>(R.id.uimenu)
        menu.visibility = View.INVISIBLE

        humanView!!.visibility = View.INVISIBLE
        if (NATIVE_UI) {
            val uimap = HashMap<HumanUIOptions, Boolean>()
            uimap[HumanUIOptions.all] = false
            human = HKHuman(humanView, uimap)
        } else {
            human = HKHuman(humanView)
        }
        human!!.setInterface(this)

        // Android native UI Sample Code
        val resetbutton = humanView!!.findViewById<Button>(R.id.resetbutton)
        val dissectbutton = humanView!!.findViewById<View>(R.id.dissectbutton) as Button
        val undobutton = humanView!!.findViewById<View>(R.id.undobutton) as Button
        val xraybutton = humanView!!.findViewById<View>(R.id.xraybutton) as Button
        val isolatebutton = humanView!!.findViewById<View>(R.id.isolatebutton) as Button
        val shareButton = humanView!!.findViewById<View>(R.id.sharebutton) as Button
        val paintbutton = humanView!!.findViewById<View>(R.id.paintbutton) as Button
        val redbutton = humanView!!.findViewById<View>(R.id.redbutton) as Button
        val greenbutton = humanView!!.findViewById<View>(R.id.greenbutton) as Button
        val bluebutton = humanView!!.findViewById<View>(R.id.bluebutton) as Button
        val yellowbutton = humanView!!.findViewById<View>(R.id.yellowbutton) as Button
        val undopaintbutton = humanView!!.findViewById<View>(R.id.undopaintbutton) as Button
        val paintmenu = humanView!!.findViewById(R.id.paintmenu) as View
        chapterPager = humanView!!.findViewById<ViewPager>(R.id.humanChapterPager)

        resetbutton.visibility = ui_visibility()
        dissectbutton.visibility = ui_visibility()
        undobutton.visibility = View.INVISIBLE
        xraybutton.visibility = ui_visibility()
        isolatebutton.visibility = ui_visibility()
        shareButton.visibility = ui_visibility()
        paintbutton.visibility = ui_visibility()
        chapterPager!!.visibility = ui_visibility()

        resetbutton.setOnClickListener {
            human!!.scene.reset()
            human!!.camera.reset()
            xraymode = false
            isolatemode = false
            dissectmode = false
            xraybutton.background.colorFilter = null
            dissectbutton.background.colorFilter = null
            isolatebutton.background.colorFilter = null
            undobutton.visibility = View.INVISIBLE
        }

        dissectbutton.setOnClickListener {
            println("dissect click")
            if (paintmode) {
                paintbutton.callOnClick()
            }
            dissectmode = !dissectmode
            human!!.scene.dissect(dissectmode)
            dissectbutton.isSelected = dissectmode
            if (dissectmode) {
                dissectbutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
                undobutton.visibility = View.VISIBLE
            } else {
                dissectbutton.background.colorFilter = null
                undobutton.visibility = View.INVISIBLE
            }
        }

        undobutton.setOnClickListener { human!!.scene.undo() }

        xraybutton.setOnClickListener {
            xraymode = !xraymode
            human!!.scene.xray(xraymode)
            if (xraymode) {
                xraybutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
            } else {
                xraybutton.background.colorFilter = null
            }
            if (dissectmode) {
                human!!.scene.dissect(true)
            }
        }

        isolatebutton.setOnClickListener {
            isolatemode = !isolatemode
            human!!.scene.isolate(isolatemode)
            //                if (isolatemode) {
            //                    isolatebutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            //                } else {
            //                    isolatebutton.getBackground().setColorFilter(null);
            //                }
            if (dissectmode) {
                human!!.scene.dissect(true)
            }
        }

        shareButton.setOnClickListener { human!!.scene.share() }

        paintbutton.setOnClickListener {
            if (dissectmode) {
                dissectbutton.callOnClick();
            }
            paintmode = !paintmode
            if (paintmenu.visibility == View.VISIBLE) {
                paintmenu.visibility = View.INVISIBLE
                human!!.scene.enableHighlight()
            } else {
                paintmenu.visibility = View.VISIBLE
                human!!.scene.disableHighlight()
            }
            if (paintmode) {
                paintbutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
            } else {
                paintbutton.background.colorFilter = null
            }
        }

        redColor.tint = doubleArrayOf(1.0,0.0,0.0)
        greenColor.tint = doubleArrayOf(0.0,1.0,0.0)
        greenColor.saturation = 0.5;
        blueColor.tint = doubleArrayOf(0.0,0.0,1.0)
        blueColor.opacity = 0.66;
        yellowColor.tint = doubleArrayOf(1.0,1.0,0.0)

        redbutton.setOnClickListener {
            paintColor = redColor
            paintmenu.setBackgroundColor(Color.RED)
        }

        greenbutton.setOnClickListener {
            paintColor = greenColor
            paintmenu.setBackgroundColor(Color.GREEN)
        }

        bluebutton.setOnClickListener {
            paintColor = blueColor
            paintmenu.setBackgroundColor(Color.BLUE)
        }

        yellowbutton.setOnClickListener {
            paintColor = yellowColor
            paintmenu.setBackgroundColor(Color.YELLOW)
        }

        undopaintbutton.setOnClickListener {
            paintColor = null
            paintmenu.setBackgroundColor(Color.TRANSPARENT)
        }

        chapterPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == human!!.timeline.currentChapter.index) {
                    println("already at chapter $position")
                    return
                }
                if (position > human!!.timeline.currentChapter.index) {
                    human!!.timeline.nextChapter()
                } else {
                    human!!.timeline.prevChapter()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        if (topMenu == null) {
            topMenu = menu
            topMenu!!.findItem(R.id.action_back).setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        human!!.unload()
        humanView!!.visibility = View.GONE
        libraryView!!.visibility = View.VISIBLE
        supportActionBar!!.title = "BioDigital SDK Demo App"
        topMenu!!.findItem(R.id.action_back).setVisible(false)
        return true
    }

    fun handleChapterClick() {
        val scale = applicationContext.resources.displayMetrics.density
//        (category as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//        if (expanded) {
//            println("shrink")
//            val px = (50 * scale + 0.5f).toInt()
//            humanbinding.category.layoutParams.height = px
//        } else {
//            println("expand")
//            val px = (160 * scale + 0.5f).toInt()
//            humanbinding.category.layoutParams.height = px
//        }
//        humanbinding.category.requestLayout()
        expanded = !expanded
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
    // Callback for getModels() completion
    override fun onModelsLoaded() {
        this@MainActivity.runOnUiThread {
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
                builder?.setMessage("Library is empty")
                builder?.setTitle("Please go to https://human.biodigital.com/ to add models to your Library")
                val dialog : AlertDialog? = builder?.create()
                dialog!!.show()
            }
        }
    }

    /**
     * API Callback - model load complete
     */
    override fun onModelLoaded(p0: String?) {
        println("MODEL LOADED CALLBACK " + p0)
        runOnUiThread {
            // build Chapter pager
            val chaptersarray = java.util.ArrayList<HKChapter>(human!!.timeline.chapterList.size)
            for (chapterid in human!!.timeline.chapterList) {
                var chapter = human!!.timeline.chapters[chapterid]
                if (chapter != null) {
                    chaptersarray.add(chapter)
                }
            }
            val adapter = ChapterAdapter(supportFragmentManager)
            adapter.setChapters(chaptersarray.toTypedArray())
            chapterPager!!.adapter = adapter
            supportActionBar!!.title = human!!.scene.title
            topMenu!!.findItem(R.id.action_back).setVisible(true);
        }
    }

    override fun onModelLoadError(p0: String?) {
        println("MODEL LOAD ERROR CALLBACK " + p0)    }

    /**
     * API Callback - object selected
     *
     * @param objectID the internal ID of the object
     */
    override fun onObjectSelected(objectId: String) {
        println("you picked " + human!!.scene.objects[objectId]!!)
//        if (paintmenu.visibility == View.INVISIBLE) {
//            return;
//        }
        if (paintColor != null) {
            human!!.scene.color(objectId, paintColor)
        } else {
            human!!.scene.uncolor(objectId)
        }
    }

    /**
     * API Callback - chapter transition to referenced chapter
     *
     * @param chapterID String ID of the Chapter, used to look up the Chapter object in
     * HumanBody's public HashMap<String></String>,Chapter> chapters
     */
    override fun onChapterTransition(objectId: String) {
        val chap = human!!.timeline.chapters[objectId]
        println("got chapter " + chap!!.title)
    }


}
