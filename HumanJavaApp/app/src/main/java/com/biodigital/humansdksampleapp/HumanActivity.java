package com.biodigital.humansdksampleapp;

import android.animation.LayoutTransition;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.biodigital.humansdk.*;

import java.util.Arrays;
import java.util.HashMap;

public class HumanActivity extends AppCompatActivity implements HKHumanInterface {

    private HKHuman body;

    private boolean xraymode = false;
    private boolean isolatemode = false;
    private boolean dissectmode = false;
    private boolean paintmode = false;

    ViewPager chapterPager;
    boolean expanded = false;
    HKColor paintColor = null;

    boolean nativeUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human);

        body = findViewById(R.id.humanbody);
        body.setInterface(this);

        if (nativeUI) {
            HashMap<HumanUIOptions, Boolean> uimap = new HashMap<>();
            uimap.put(HumanUIOptions.all, false);
            body.setUIoptions(uimap);
        }

        String moduleID = getIntent().getStringExtra(MainActivity.MODULE_MESSAGE);
        body.load(moduleID);

        final Button homebutton = (Button)findViewById(R.id.homebutton);
        final Button resetbutton = (Button)findViewById(R.id.resetbutton);
        final Button dissectbutton = (Button)findViewById(R.id.dissectbutton);
        final Button undobutton = (Button)findViewById(R.id.undobutton);
        final Button xraybutton = (Button)findViewById(R.id.xraybutton);
        final Button isolatebutton = (Button)findViewById(R.id.isolatebutton);
        final Button shareButton = (Button)findViewById(R.id.sharebutton);
        final Button paintbutton = (Button)findViewById(R.id.paintbutton);
        final Button redbutton = (Button)findViewById(R.id.redbutton);
        final Button greenbutton = (Button)findViewById(R.id.greenbutton);
        final Button bluebutton = (Button)findViewById(R.id.bluebutton);
        final Button yellowbutton = (Button)findViewById(R.id.yellowbutton);
        final Button undopaintbutton = (Button)findViewById(R.id.undopaintbutton);
        final View allpaintstuff = (View)findViewById(R.id.allpaintstuff);
        final View paintmenu = (View)findViewById(R.id.paintmenu);
        final ScrollView scroller = (ScrollView)findViewById(R.id.scrollView);
        chapterPager = (ViewPager)findViewById(R.id.humanChapterPager);

        if (!nativeUI) {
            resetbutton.setVisibility(View.INVISIBLE);
            dissectbutton.setVisibility(View.INVISIBLE);
            xraybutton.setVisibility(View.INVISIBLE);
            isolatebutton.setVisibility(View.INVISIBLE);
            shareButton.setVisibility(View.INVISIBLE);
            paintbutton.setVisibility(View.INVISIBLE);
            scroller.setVisibility(View.INVISIBLE);
            chapterPager.setVisibility(View.INVISIBLE);
        }

        final HKColor redColor = new HKColor();
        final HKColor greenColor = new HKColor();
        final HKColor blueColor = new HKColor();
        final HKColor yellowColor = new HKColor();

        ProgressBar progress = findViewById(R.id.progressBar1);
        progress.setVisibility(View.VISIBLE);

        homebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        resetbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                body.scene.resetScene();
                xraymode = false;
                isolatemode = false;
                dissectmode = false;
                xraybutton.getBackground().setColorFilter(null);
                dissectbutton.getBackground().setColorFilter(null);
                isolatebutton.getBackground().setColorFilter(null);
                undobutton.setVisibility(View.INVISIBLE);
            }
        });

        dissectbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (paintmode) {
                    doPaintButton();
                }
                doDissectButton();
            }
        });

        undobutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                body.scene.undo();
            }
        });

        xraybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                xraymode = !xraymode;
                body.scene.xray(xraymode);
                if (xraymode) {
                    xraybutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
                } else {
                    xraybutton.getBackground().setColorFilter(null);
                }
                if ( dissectmode ) {
                    body.scene.dissect(true);
                }
            }
        });

        isolatebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isolatemode = !isolatemode;
                body.scene.isolate(isolatemode);
//                if (isolatemode) {
//                    isolatebutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
//                } else {
//                    isolatebutton.getBackground().setColorFilter(null);
//                }
                if ( dissectmode ) {
                    body.scene.dissect(true);
                }

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                body.scene.share();
            }
        });

        paintbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dissectmode) {
                    doDissectButton();
                }
                doPaintButton();
            }
        });

        redColor.tint[0] = 1.0f;
        redColor.tint[1] = 0.0f;
        redColor.tint[2] = 0.0f;
        greenColor.tint[0] = 0.0f;
        greenColor.tint[1] = 1.0f;
        greenColor.tint[2] = 0.0f;
        blueColor.tint[0] = 0.0f;
        blueColor.tint[1] = 0.0f;
        blueColor.tint[2] = 1.0f;
        yellowColor.tint[0] = 1.0f;
        yellowColor.tint[1] = 1.0f;
        yellowColor.tint[2] = 0.0f;
        greenColor.saturation = 0.5f;
        blueColor.opacity = 0.66f;

        redbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintColor = redColor;
                paintmenu.setBackgroundColor(Color.RED);
            }
        });

        greenbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintColor = greenColor;
                paintmenu.setBackgroundColor(Color.GREEN);
            }
        });

        bluebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintColor = blueColor;
                paintmenu.setBackgroundColor(Color.BLUE);
            }
        });

        yellowbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintColor = yellowColor;
                paintmenu.setBackgroundColor(Color.YELLOW);
            }
        });

        undopaintbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paintColor = null;
                paintmenu.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        chapterPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if ( position == body.timeline.currentChapter.index ) {
                    System.out.println("already at chapter " + position);
                    return;
                }
                if ( position > body.timeline.currentChapter.index ) {
                    body.timeline.nextChapter();
                } else {
                    body.timeline.prevChapter();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void handleChapterClick() {
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.category);
        ((ViewGroup) rl).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        if (expanded) {
            System.out.println("shrink");
            int px = (int) (50 * scale + 0.5f);
            rl.getLayoutParams().height = px;
        } else {
            System.out.println("expand");
            int px = (int) (160 * scale + 0.5f);
            rl.getLayoutParams().height = px;
        }
        rl.requestLayout();
        expanded = !expanded;
    }
    void doPaintButton() {
        Button paintbutton = (Button)findViewById(R.id.paintbutton);
        View paintmenu = (View)findViewById(R.id.paintmenu);
        paintmode = !paintmode;
        if (paintmenu.getVisibility() == View.VISIBLE) {
            paintmenu.setVisibility(View.INVISIBLE);
            body.scene.enableHighlight();
        } else {
            paintmenu.setVisibility(View.VISIBLE);
            body.scene.disableHighlight();
        }
        if (paintmode) {
            paintbutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        } else {
            paintbutton.getBackground().setColorFilter(null);
        }
    }

    void doDissectButton() {
        Button dissectbutton = (Button)findViewById(R.id.dissectbutton);
        Button undobutton = (Button)findViewById(R.id.undobutton);
        dissectmode = !dissectmode;
        body.scene.dissect(dissectmode);
        dissectbutton.setSelected(dissectmode);
        if (dissectmode) {
            dissectbutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            undobutton.setVisibility(View.VISIBLE);
        } else {
            dissectbutton.getBackground().setColorFilter(null);
            undobutton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //
    // API callbacks defined in HumanBodyInterface
    //
    /**
     * API Callback - module load complete
     */
    public void onModuleLoaded() {
        System.out.println("MODULE LOADED CALLBACK");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progress = findViewById(R.id.progressBar1);
                progress.setVisibility(View.GONE);
                // build Chapter pager
                HKChapter[] chaptersarray = new HKChapter[body.timeline.chapterList.size()];
                int i = 0;
                for ( String chapterid : body.timeline.chapterList ) {
                    chaptersarray[i] = body.timeline.chapters.get(chapterid);
                    i++;
                }
                ChapterAdapter adapter = new ChapterAdapter(getSupportFragmentManager());
                adapter.setChapters(chaptersarray);
                chapterPager.setAdapter(adapter);
            }
        });
    }

    /**
     * API Callback - object selected
     *
     * @param objectID the internal ID of the object
     */
    public void onObjectSelected(String objectID) {
        System.out.println("you picked " + body.scene.objects.get(objectID));
        View paintmenu = (View)findViewById(R.id.paintmenu);
        if (paintmenu.getVisibility() == View.VISIBLE) {
            if (paintColor == null) {
                body.scene.uncolorObject(objectID);
            } else {
                body.scene.colorObject(objectID, paintColor);
            }
        }
    }

    /**
     * API Callback - object deselected
     *
     * @param objectID the internal ID of the object
     */
    public void onObjectDeselected(String objectID) {
    }

        /**
         * API Callback - chapter transition to referenced chapter
         *
         * @param chapterID String ID of the Chapter, used to look up the Chapter object in
         *                  HumanBody's public HashMap<String,Chapter> chapters
         */
    public void onChapterTransition(String chapterID) {
        HKChapter chap = body.timeline.chapters.get(chapterID);
        System.out.println("got chapter " + chap.title);
    }

    /**
     *  API Callback - screenshot received
     *
     * @param image Bitmap object containing the screenshot image
     */
    public void onScreenshot(Bitmap image) {
        System.out.println("got screenshot");
    }

    /**
     *  API Callback - animation ended signal to reset animation UI
     */
    public void onAnimationComplete() {
    }

}
