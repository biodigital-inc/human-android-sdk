package com.biodigital.kotlinapp


import android.animation.LayoutTransition
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.ScrollView

import com.biodigital.humansdk.*
import com.biodigital.kotlinapp.R

import java.util.HashMap

import kotlinx.android.synthetic.main.activity_human.*
import kotlinx.android.synthetic.main.chapter_fragment.*

class HumanActivity : AppCompatActivity(), HKHumanInterface {

    private var xraymode = false
    private var isolatemode = false
    private var dissectmode = false
    private var paintmode = false

    internal var paintColor : HKColor? = null

    internal var redColor = HKColor()
    internal var greenColor = HKColor()
    internal var blueColor = HKColor()
    internal var yellowColor = HKColor()

//    internal var chapterPager : ViewPager? = null
    internal var expanded = false

    var nativeUI = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_human)

        val moduleID = intent.getStringExtra(MODULE_MESSAGE)
        System.out.println("load module " + moduleID)


        if (nativeUI) {
            val uimap = HashMap<HumanUIOptions, Boolean>()
            uimap[HumanUIOptions.all] = false
            humanbody.setUIoptions(uimap)
        }

        humanbody.setInterface(this)

        progressBar1.setVisibility(View.VISIBLE)

        //        body.load("/production/maleAdult/flu.json");
        humanbody.load(moduleID)

        homebutton.setOnClickListener { finish() }

        if (!nativeUI) {
            resetbutton.visibility = View.INVISIBLE
            dissectbutton.visibility = View.INVISIBLE
            xraybutton.visibility = View.INVISIBLE
            isolatebutton.visibility = View.INVISIBLE
            sharebutton.visibility = View.INVISIBLE
            paintbutton.visibility = View.INVISIBLE
            category.visibility = View.INVISIBLE
        }

        resetbutton.setOnClickListener {
            humanbody.scene.resetScene()
            xraymode = false
            isolatemode = false
            dissectmode = false
            xraybutton.background.colorFilter = null
            dissectbutton.background.colorFilter = null
            isolatebutton.background.colorFilter = null
            undobutton.visibility = View.INVISIBLE
        }

        dissectbutton.setOnClickListener {
            if (paintmode) {
                paintbutton.callOnClick();
            }
            dissectmode = !dissectmode
            humanbody.scene.dissect(dissectmode)
            dissectbutton.isSelected = dissectmode
            if (dissectmode) {
                dissectbutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
                undobutton.visibility = View.VISIBLE
            } else {
                dissectbutton.background.colorFilter = null
                undobutton.visibility = View.INVISIBLE
            }
        }

        undobutton.setOnClickListener { humanbody.scene.undo() }

        xraybutton.setOnClickListener {
            xraymode = !xraymode
            humanbody.scene.xray(xraymode)
            if (xraymode) {
                xraybutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
            } else {
                xraybutton.background.colorFilter = null
            }
            if (dissectmode) {
                humanbody.scene.dissect(true)
            }
        }

        isolatebutton.setOnClickListener {
            isolatemode = !isolatemode
            humanbody.scene.isolate(isolatemode)
            //                if (isolatemode) {
            //                    isolatebutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            //                } else {
            //                    isolatebutton.getBackground().setColorFilter(null);
            //                }
            if (dissectmode) {
                humanbody.scene.dissect(true)
            }
        }

        sharebutton.setOnClickListener { humanbody.scene.share() }

        paintbutton.setOnClickListener {
            if (dissectmode) {
                dissectbutton.callOnClick();
            }
            paintmode = !paintmode
            if (paintmenu.visibility == View.VISIBLE) {
                paintmenu.visibility = View.INVISIBLE
                humanbody.scene.enableHighlight()
            } else {
                paintmenu.visibility = View.VISIBLE
                humanbody.scene.disableHighlight()
            }
            if (paintmode) {
                paintbutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
            } else {
                paintbutton.background.colorFilter = null
            }
        }

        redColor.tint = floatArrayOf(1.0f,0.0f,0.0f)
        greenColor.tint = floatArrayOf(0.0f,1.0f,0.0f)
        greenColor.saturation = 0.5f;
        blueColor.tint = floatArrayOf(0.0f,0.0f,1.0f)
        blueColor.opacity = 0.66f;
        yellowColor.tint = floatArrayOf(1.0f,1.0f,0.0f)

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

        humanChapterPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == humanbody.timeline.currentChapter.index) {
                    println("already at chapter $position")
                    return
                }
                if (position > humanbody.timeline.currentChapter.index) {
                    humanbody.timeline.nextChapter()
                } else {
                    humanbody.timeline.prevChapter()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    fun handleChapterClick() {
        val scale = applicationContext.resources.displayMetrics.density
        (category as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        if (expanded) {
            println("shrink")
            val px = (50 * scale + 0.5f).toInt()
            category.layoutParams.height = px
        } else {
            println("expand")
            val px = (160 * scale + 0.5f).toInt()
            category.layoutParams.height = px
        }
        category.requestLayout()
        expanded = !expanded
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    //
    // API callbacks defined in HumanBodyInterface
    //
    /**
     * API Callback - module load initialized
     */
    override fun onModuleInit() {
        println("module init")
    }

    /**
     * API Callback - module load complete
     */
    override fun onModuleLoaded() {
        println("MODULE LOADED CALLBACK")
        progressBar1.setVisibility(View.INVISIBLE)
        runOnUiThread {
            // build Chapter pager
            val chaptersarray = ArrayList<HKChapter>(humanbody.timeline.chapterList.size)
            for (chapterid in humanbody.timeline.chapterList) {
                var chapter = humanbody.timeline.chapters[chapterid]
                if (chapter != null) {
                    chaptersarray.add(chapter)
                }
            }
            if (nativeUI) {
                val adapter = ChapterAdapter(supportFragmentManager)
                adapter.setChapters(chaptersarray.toTypedArray())
                humanChapterPager.adapter = adapter
            }
        }
    }

    /**
     * API Callback - object selected
     *
     * @param objectID the internal ID of the object
     */
    override fun onObjectSelected(objectID: String) {
        println("you picked " + humanbody.scene.objects[objectID]!!)
        if (paintmenu.visibility == View.INVISIBLE) {
            return;
        }
        if (paintColor != null) {
            humanbody.scene.colorObject(objectID, paintColor)
        } else {
            humanbody.scene.uncolorObject(objectID)
        }
    }

    override fun onObjectDeselected(objectID: String) {
    }

        /**
     * API Callback - chapter transition to referenced chapter
     *
     * @param chapterID String ID of the Chapter, used to look up the Chapter object in
     * HumanBody's public HashMap<String></String>,Chapter> chapters
     */
    override fun onChapterTransition(chapterID: String) {
        val chap = humanbody.timeline.chapters[chapterID]
        println("got chapter " + chap!!.title)
    }

    /**
     * API Callback - screenshot received
     *
     * @param image Bitmap object containing the screenshot image
     */
    override fun onScreenshot(image: Bitmap) {
        println("got screenshot")
    }

    /**
     * API Callback - animation ended signal to reset animation UI
     */
    override fun onAnimationComplete() {}

}
