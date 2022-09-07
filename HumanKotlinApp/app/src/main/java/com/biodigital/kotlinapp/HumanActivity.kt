package com.biodigital.kotlinapp


import android.animation.LayoutTransition
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.biodigital.humansdk.*
import com.biodigital.kotlinapp.databinding.ActivityHumanBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableMap
import kotlin.collections.set
import kotlin.collections.toTypedArray

class HumanActivity : AppCompatActivity(), HKHumanInterface {

    private var xraymode = false
    private var isolatemode = false
    private var dissectmode = false
    private var paintmode = false

    private var humanbody: HKHuman? = null

    internal var paintColor : HKColor? = null

    internal var redColor = HKColor()
    internal var greenColor = HKColor()
    internal var blueColor = HKColor()
    internal var yellowColor = HKColor()

    internal var expanded = false
    private lateinit var binding: ActivityHumanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHumanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        val uiAll = false

        val modelId = intent.getStringExtra(MODEL_MESSAGE)
        System.out.println("load model $modelId")

        val uimap = HashMap<HumanUIOptions, Boolean>()
        uimap[HumanUIOptions.all] = uiAll

        humanbody = HKHuman(binding.humanview, uimap)

        humanbody!!.setInterface(this)

        humanbody!!.load(modelId)

        // turn the built in UI on, or off to show native UI elements
        val uiVisibility = if (uiAll) View.INVISIBLE else View.VISIBLE
        binding.homebutton.visibility = uiVisibility
        binding.resetbutton.visibility = uiVisibility
        binding.dissectbutton.visibility = uiVisibility
        binding.undobutton.visibility = View.INVISIBLE
        binding.xraybutton.visibility = uiVisibility
        binding.isolatebutton.visibility = uiVisibility
        binding.sharebutton.visibility = uiVisibility
        binding.allpaintstuff.visibility = uiVisibility
        binding.paintmenu.visibility = View.INVISIBLE
        binding.humanChapterPager.visibility = uiVisibility

        binding.homebutton.setOnClickListener { finish() }

        binding.resetbutton.setOnClickListener {
            humanbody!!.scene.reset()
            humanbody!!.camera.reset()
            xraymode = false
            isolatemode = false
            dissectmode = false
            binding.xraybutton.background.colorFilter = null
            binding.dissectbutton.background.colorFilter = null
            binding.isolatebutton.background.colorFilter = null
            binding.undobutton.visibility = View.INVISIBLE
        }

        binding.dissectbutton.setOnClickListener {
            if (paintmode) {
                binding.paintbutton.callOnClick();
            }
            dissectmode = !dissectmode
            humanbody!!.scene.dissect(dissectmode)
            binding.dissectbutton.isSelected = dissectmode
            if (dissectmode) {
                binding.dissectbutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
                binding.undobutton.visibility = View.VISIBLE
            } else {
                binding.dissectbutton.background.colorFilter = null
                binding.undobutton.visibility = View.INVISIBLE
            }
        }

        binding.undobutton.setOnClickListener { humanbody!!.scene.undo() }

        binding.xraybutton.setOnClickListener {
            xraymode = !xraymode
            humanbody!!.scene.xray(xraymode)
            if (xraymode) {
                binding.xraybutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
            } else {
                binding.xraybutton.background.colorFilter = null
            }
            if (dissectmode) {
                humanbody!!.scene.dissect(true)
            }
        }

        binding.isolatebutton.setOnClickListener {
            isolatemode = !isolatemode
            humanbody!!.scene.isolate(isolatemode)
            //                if (isolatemode) {
            //                    isolatebutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            //                } else {
            //                    isolatebutton.getBackground().setColorFilter(null);
            //                }
            if (dissectmode) {
                humanbody!!.scene.dissect(true)
            }
        }

        binding.sharebutton.setOnClickListener { humanbody!!.scene.share() }

        binding.paintbutton.setOnClickListener {
            if (dissectmode) {
                binding.dissectbutton.callOnClick();
            }
            paintmode = !paintmode
            if (paintmode) {
                binding.paintmenu.visibility = View.VISIBLE
                humanbody!!.scene.disableHighlight()
                binding.paintbutton.background.colorFilter = LightingColorFilter(-0x1, -0x560000)
            } else {
                binding.paintmenu.visibility = View.INVISIBLE
                humanbody!!.scene.enableHighlight()
                binding.paintbutton.background.colorFilter = null
            }
        }

        redColor.tint = doubleArrayOf(1.0,0.0,0.0)
        greenColor.tint = doubleArrayOf(0.0,1.0,0.0)
        greenColor.saturation = 0.5;
        blueColor.tint = doubleArrayOf(0.0,0.0,1.0)
        blueColor.opacity = 0.66;
        yellowColor.tint = doubleArrayOf(1.0,1.0,0.0)

        binding.redbutton.setOnClickListener {
            paintColor = redColor
            binding.paintmenu.setBackgroundColor(Color.RED)
        }

        binding.greenbutton.setOnClickListener {
            paintColor = greenColor
            binding.paintmenu.setBackgroundColor(Color.GREEN)
        }

        binding.bluebutton.setOnClickListener {
            paintColor = blueColor
            binding.paintmenu.setBackgroundColor(Color.BLUE)
        }

        binding.yellowbutton.setOnClickListener {
            paintColor = yellowColor
            binding.paintmenu.setBackgroundColor(Color.YELLOW)
        }

        binding.undopaintbutton.setOnClickListener {
            paintColor = null
            binding.paintmenu.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.humanChapterPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == humanbody!!.timeline.currentChapter.index) {
                    println("already at chapter $position")
                    return
                }
                if (position > humanbody!!.timeline.currentChapter.index) {
                    humanbody!!.timeline.nextChapter()
                } else {
                    humanbody!!.timeline.prevChapter()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun handleChapterClick() {
        val scale = applicationContext.resources.displayMetrics.density
        (binding.category as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        if (expanded) {
            val px = (50 * scale + 0.5f).toInt()
            binding.category.layoutParams.height = px
        } else {
            val px = (160 * scale + 0.5f).toInt()
            binding.category.layoutParams.height = px
        }
        binding.category.requestLayout()
        expanded = !expanded
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    //
    // API callbacks defined in HumanBodyInterface
    //
    /**
     * API Callback - model load complete
     */
    override fun onModelLoaded(title: String) {
        println("MODEL LOADED CALLBACK $title")
        runOnUiThread {
            // build Chapter pager
            val chaptersarray = ArrayList<HKChapter>(humanbody!!.timeline.chapterList.size)
            for (chapterid in humanbody!!.timeline.chapterList) {
                var chapter = humanbody!!.timeline.chapters[chapterid]
                if (chapter != null) {
                    chaptersarray.add(chapter)
                }
            }
            val adapter = ChapterAdapter(supportFragmentManager)
            adapter.setChapters(chaptersarray.toTypedArray())
            binding.humanChapterPager.adapter = adapter
        }
    }

    override fun onModelLoadError(title: String) {
        println("load error")
    }

    /**
     * API Callback - object selected
     *
     * @param objectID the internal ID of the object
     */
    override fun onObjectSelected(objectId: String) {
        println("you picked " + humanbody!!.scene.objects[objectId]!!)
        if (binding.paintmenu.visibility == View.INVISIBLE) {
            return;
        }
        if (paintColor != null) {
            humanbody!!.scene.color(objectId, paintColor)
        } else {
            humanbody!!.scene.uncolor(objectId)
        }
    }

    override fun onObjectDeselected(objectId: String) {
    }

    /**
     * API Callback - chapter transition to referenced chapter
     *
     * @param chapterID String ID of the Chapter, used to look up the Chapter object in
     * HumanBody's public HashMap<String></String>,Chapter> chapters
     */
    override fun onChapterTransition(chapterID: String) {
        val chap = humanbody!!.timeline.chapters[chapterID]
        if (chap != null) {
            println("got chapter ${chap.title}")
        }
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

    override fun onAnnotationCreated(p0: String?) {
    }

    override fun onAnnotationDestroyed(p0: String?) {
    }

    override fun onAnnotationUpdated(p0: HKAnnotation?) {
    }

    override fun onAnnotationsShown(p0: Boolean?) {
    }

    override fun onCameraUpdated(p0: HKCamera?) {
    }

    override fun onObjectColor(p0: String?, p1: HKColor?) {
    }

    override fun onObjectPicked(p0: String?, p1: DoubleArray?) {
    }

    override fun onObjectsShown(p0: MutableMap<String, Any>?) {
    }

    override fun onSceneCapture(p0: String?) {
    }

    override fun onSceneInit(p0: String?) {
    }

    override fun onSceneRestore() {
    }

    override fun onTimelineUpdated(p0: HKTimeline?) {
    }

    override fun onXrayEnabled(p0: Boolean?) {
    }
}
