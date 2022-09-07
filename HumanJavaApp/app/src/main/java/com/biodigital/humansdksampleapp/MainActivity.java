package com.biodigital.humansdksampleapp;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.biodigital.humansdk.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HKServicesInterface, HKHumanInterface {
    public static final String MODEL_MESSAGE = "com.biodigital.com.MODEL_MESSAGE";
    public static int stressIncrement = 0;
    public static boolean stressTestEnabled = false;

    private boolean xraymode = false;
    private boolean isolatemode = false;
    private boolean dissectmode = false;
    private boolean paintmode = false;

    ViewPager chapterPager;
    boolean expanded = false;
    HKColor paintColor = null;

    // set this to false to hide the built in UI and show native UI elements
    boolean uiAll = true;

    private ArrayList<HKModel> models = new ArrayList<>(Arrays.asList(
            new HKModel("Thorax", "production/maleAdult/human_02_regional_male_thorax.json", "", "human_02_regional_male_thorax"),
            new HKModel("Flu", "production/maleAdult/flu.json", "", "https://human.biodigital.com/thumbs/modules/production/maleAdult/flu/large/index.jpg"),
            new HKModel("Acne", "production/maleAdult/acne", "", "https://human.biodigital.com/thumbs/modules/production/maleAdult/acne/large/index.jpg"),
            new HKModel("Brain", "production/maleAdult/male_region_brain_13", "", "https://human.biodigital.com/thumbs/modules/production/maleAdult/male_region_brain_13/large/index.jpg"),
            new HKModel("Bladder", "production/maleAdult/bladder_cancer_v02", "", "https://human.biodigital.com/thumbs/modules/production/maleAdult/bladder_cancer_v02/large/index.jpg"),
            new HKModel("Breathing", "production/maleAdult/breathing_beating_heart_v02", "", "https://human.biodigital.com/thumbs/modules/production/maleAdult/breathing_beating_heart_v02/large/index.jpg")
    ));

    private ModelAdapter modelAdapter;
    private ArrayList<String> dlIds = new ArrayList<>();

    RelativeLayout humanLayout;

    private HKHuman human;
    int downloadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("BioDigital SDK Content Library");

        HKServices.getInstance().setup(this, this);
        HKServices.getInstance().getModels();
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HKModel model = models.get(position);
                humanLayout.setVisibility(View.VISIBLE);
                human.ui.presetBackgroundColor(Color.RED, Color.YELLOW);
                human.load(model.id);
                if (uiAll) {
                    View home = humanLayout.findViewById(R.id.homebutton);
                    ViewGroup homeparent = (ViewGroup)home.getParent();
                    homeparent.removeView(home);
                    humanLayout.addView(home);
                } else {
                    View menu = humanLayout.findViewById(R.id.menu);
                    menu.setVisibility(View.VISIBLE);
                    menu.bringToFront();
                    View chap = humanLayout.findViewById(R.id.category);
                    chap.bringToFront();
                }
            }
        });
        modelAdapter = new ModelAdapter(this,models);
        gridView.setAdapter(modelAdapter);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout mainView = findViewById(R.id.mainview);
        try {
            humanLayout = (RelativeLayout) inflater.inflate(R.layout.activity_human, mainView, false);
        } catch (Exception e) {
            System.out.println("exception in inflate " + e.getMessage());
        }
        mainView.addView(humanLayout);

        final Button homebutton = (Button)humanLayout.findViewById(R.id.homebutton);
        // Call unload() to reset the human view for the next load
        homebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                human.unload();
                humanLayout.setVisibility(View.INVISIBLE);
            }
        });

        View menu = humanLayout.findViewById(R.id.menu);
        menu.setVisibility(View.INVISIBLE);

        humanLayout.setVisibility(View.INVISIBLE);
        RelativeLayout rl = humanLayout.findViewById(R.id.humanbody);
        HashMap<HumanUIOptions,Boolean> uimap = new HashMap<>();
        uimap.put(HumanUIOptions.all,uiAll);
        human = new HKHuman(rl, uimap);
        human.setInterface(this);

        final Button resetbutton = (Button)humanLayout.findViewById(R.id.resetbutton);
        final Button dissectbutton = (Button)humanLayout.findViewById(R.id.dissectbutton);
        final Button undobutton = (Button)humanLayout.findViewById(R.id.undobutton);
        final Button xraybutton = (Button)humanLayout.findViewById(R.id.xraybutton);
        final Button isolatebutton = (Button)humanLayout.findViewById(R.id.isolatebutton);
        final Button shareButton = (Button)humanLayout.findViewById(R.id.sharebutton);
        final Button paintbutton = (Button)humanLayout.findViewById(R.id.paintbutton);
        final Button redbutton = (Button)humanLayout.findViewById(R.id.redbutton);
        final Button greenbutton = (Button)humanLayout.findViewById(R.id.greenbutton);
        final Button bluebutton = (Button)humanLayout.findViewById(R.id.bluebutton);
        final Button yellowbutton = (Button)humanLayout.findViewById(R.id.yellowbutton);
        final Button undopaintbutton = (Button)humanLayout.findViewById(R.id.undopaintbutton);
        final View paintmenu = (View)humanLayout.findViewById(R.id.paintmenu);
        chapterPager = (ViewPager)humanLayout.findViewById(R.id.humanChapterPager);

        final HKColor redColor = new HKColor();
        final HKColor greenColor = new HKColor();
        final HKColor blueColor = new HKColor();
        final HKColor yellowColor = new HKColor();

        resetbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                human.scene.reset();
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
                human.scene.undo();
            }
        });

        xraybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                xraymode = !xraymode;
                human.scene.xray(xraymode);
                if (xraymode) {
                    xraybutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
                } else {
                    xraybutton.getBackground().setColorFilter(null);
                }
                if ( dissectmode ) {
                    human.scene.dissect(true);
                }
            }
        });

        isolatebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isolatemode = !isolatemode;
                human.scene.isolate(isolatemode);
                if ( dissectmode ) {
                    human.scene.dissect(true);
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                human.scene.screenshot();
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
                if ( position == human.timeline.currentChapter.index ) {
                    System.out.println("already at chapter " + position);
                    return;
                }
                if ( position > human.timeline.currentChapter.index ) {
                    human.timeline.nextChapter();
                } else {
                    human.timeline.prevChapter();
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
            int px = (int) (50 * scale + 0.5f);
            rl.getLayoutParams().height = px;
        } else {
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
        if (paintmode) {
            paintbutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            paintmenu.setVisibility(View.VISIBLE);
            human.scene.disableHighlight();
        } else {
            paintbutton.getBackground().setColorFilter(null);
            paintmenu.setVisibility(View.INVISIBLE);
            human.scene.enableHighlight();
        }
    }

    void doDissectButton() {
        Button dissectbutton = (Button)findViewById(R.id.dissectbutton);
        Button undobutton = (Button)findViewById(R.id.undobutton);
        dissectmode = !dissectmode;
        human.scene.dissect(dissectmode);
        dissectbutton.setSelected(dissectmode);
        if (dissectmode) {
            dissectbutton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            undobutton.setVisibility(View.VISIBLE);
        } else {
            dissectbutton.getBackground().setColorFilter(null);
            undobutton.setVisibility(View.INVISIBLE);
        }
    }

    public void onModelsLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ( HKServices.getInstance().models != null &&  HKServices.getInstance().models.size() > 0) {
                    models.addAll( HKServices.getInstance().models);
                    modelAdapter.notifyDataSetChanged();

                    // set to a positive number to test out the download feature
                    int testDownloads = 0;
                    if (testDownloads > 0) {
                        int i = 0;
                        int j = 0;
                        while (i < testDownloads) {
                            HKModel model = models.get(j);
                            if (!HKServices.getInstance().modelDownloaded(model.id)) {
                                System.out.println("let's download " + model.id);
                                dlIds.add(model.id);
                                i++;
                                downloadCount++;
                            }
                            j++;
                        }
                        HKServices.getInstance().download(dlIds);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("OK");
                        }
                    });
                    builder.setTitle("Dashboard is empty");
                    builder.setMessage("Please go to https://human.biodigital.com/ to add models to your Dashboard");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                System.out.println("got " + models.size() + " models");
            }
        });
    }

    /**
     * API Callback - SDK failed validation
     */
    public void onInvalidSDK() {
        System.out.println("error!  we aren't authenticated with BioDigital!");
    }

    /**
     * API Callback - SDK passed validation
     */
    public void onValidSDK() {
        System.out.println("success!  we are authenticated with BioDigital!");
    }

    public void onModelDownloaded(String modelId, Integer count, Integer total) {
        final HKHumanInterface activity = this;
        System.out.println("SUCCESS!!  model downloaded! " + modelId);
        downloadCount--;
        if (downloadCount == 0) {
            System.out.println("done downloading");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout rl = humanLayout.findViewById(R.id.humanbody);
                    human = new HKHuman(rl);
                    human.setInterface(activity);
                }
            });
        }
    }

    public void onModelDownloadError(String modelId) {
        System.out.println("model download error! " + modelId);
    }

    public void onModelLoaded(String modelId) {
        System.out.println("sample app got model loaded message");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("model loaded " + modelId);
                // build Chapter pager
                HKChapter[] chaptersarray = new HKChapter[human.timeline.chapterList.size()];
                int i = 0;
                for ( String chapterid : human.timeline.chapterList ) {
                    chaptersarray[i] = human.timeline.chapters.get(chapterid);
                    i++;
                }
                ChapterAdapter adapter = new ChapterAdapter(getSupportFragmentManager());
                adapter.setChapters(chaptersarray);
                chapterPager.setAdapter(adapter);
            }
        });

        // stress testing
        if (MainActivity.stressTestEnabled) {
            // log event received
            System.out.println(
                    String.format("API Debug [%d]- SDK scene loaded", MainActivity.stressIncrement)
            );

            // log objects
            System.out.println(
                    String.format("API Debug [%d]- SDK objectIds (%d)", MainActivity.stressIncrement, human.scene.objectIds.size())
            );

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            human.unload();
                            humanLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }, 100);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nextStressTest();
                        }
                    });
                }
            }, 1500);
        }
    }

    public void onModelLoadError(String modelId) {
        System.out.println("model load error " + modelId);
        onBackPressed();
    }

    public void onSceneInit(String title) {
        System.out.println("got model title " + title);
    }

    /**
     * API Callback - object selected
     *
     * @param objectID the internal ID of the object
     */
    public void onObjectSelected(String objectID) {
        System.out.println("you selected " + human.scene.objects.get(objectID));
        View paintmenu = (View)findViewById(R.id.paintmenu);
        if (paintmenu.getVisibility() == View.VISIBLE) {
            if (paintColor == null) {
                human.scene.uncolor(objectID);
            } else {
                human.scene.color(objectID, paintColor);
            }
        }
    }

    public void onScreenshot(Bitmap image) {
        Uri bitmapUri = saveToInternalStorage(image);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        MainActivity.this.startActivity(intent);
    }

    private Uri saveToInternalStorage(Bitmap bitmapImage){
        File mypath = new File(getApplicationContext().getCacheDir(),"shareImage.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", mypath);
    }

    public void onObjectDeselected(String objectId) {}

    public void onObjectsShown(Map<String, Object> objects) {}

    public void onChapterTransition(String chapterId) {}

    public void onAnimationComplete() {}

    public void onXrayEnabled(Boolean isEnabled) {}

    public void onSceneRestore() {}

    public void onTimelineUpdated(HKTimeline timeline) {}

    public void onAnnotationCreated(String annotationId) {}

    public void onAnnotationDestroyed(String annotationId) {}

    public void onCameraUpdated(HKCamera camera) {}

    public void onObjectPicked(String objectId, double[] position) {}

    public void onAnnotationsShown(Boolean isShown) {}

    public void onAnnotationUpdated(HKAnnotation annotation) {}

    public void onObjectColor(String objectId, HKColor color) {}

    public void onSceneCapture(String captureString) {}

    @Override
    protected void onResume() {
        super.onResume();

        if (stressTestEnabled) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextStressTest();
                }
            }, 5000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startStressTest(View view) {
        stressTestEnabled = true;
        nextStressTest();
    }

    public void stopStressTest(View view) {
        stressTestEnabled = false;
    }

    private void nextStressTest() {
        HKModel model = models.get(MainActivity.stressIncrement % 8); // models.size());
        humanLayout.setVisibility(View.VISIBLE);
        human.load(model.id);
        MainActivity.stressIncrement += 1;
    }
}