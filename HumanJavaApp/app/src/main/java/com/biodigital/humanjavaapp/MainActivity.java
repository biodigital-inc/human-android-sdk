package com.biodigital.humanjavaapp;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import com.biodigital.humansdk.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements HKServicesInterface, HKHumanInterface {
    public static final String MODEL_MESSAGE = "com.biodigital.com.MODEL_MESSAGE";
    public static int stressIncrement = 0;
    public static boolean stressTestEnabled = false;
    public static int stressCounter = 0;

    // turn this ON to disable the built in UI and display native Android UI
    private boolean native_ui = false;

    private boolean xraymode = false;
    private boolean isolatemode = false;
    private boolean dissectmode = false;
    private boolean paintmode = false;

    ViewPager chapterPager;
    boolean expanded = false;
    HKColor paintColor = null;

    private ArrayList<HKModel> models = new ArrayList<>(Arrays.asList(
            new HKModel("Alzheimers Disease", "production/femaleAdult/alzheimers_disease",  "",  "https://human.biodigital.com/media/images/469e0d37-6088-4b64-8269-833b89d77a5b/small/image.jpg"),
            new HKModel("Esophageal Varices","production/maleAdult/esophageal_varices",  "", "https://human.biodigital.com/media/images/9b201b08-089e-44ac-8a80-9679453ffc3c/small/image.jpg"),
            new HKModel("Hip Replacement", "production/maleAdult/posterior_total_hip_replacement", "", "https://human.biodigital.com/media/images/05ba4b17-b49f-4cd9-b8be-cada95f41698/small/image.jpg"),
            new HKModel("Cell", "production/maleAdult/cell", "", "https://human.biodigital.com/media/images/b4f221d7-8863-4765-ade3-938dc248a18b/small/image.jpg"),
            new HKModel("Carotid Sheath", "production/maleAdult/contents_of_carotid_sheath_guided", "", "https://human.biodigital.com/media/images/c3541b60-afaf-4705-9e03-a6106b606749/small/image.jpg"),
            new HKModel("Breast Cancer", "production/femaleAdult/breast_cancer_dark_skin","", "https://human.biodigital.com/media/images/e556e58f-ca21-4b38-8161-7ea1dac46f95/small/image.jpg"),
            new HKModel("Kidney Stones", "production/maleAdult/kidney_stones_03","", "https://human.biodigital.com/media/images/f3af546a-1699-4304-a5b1-5b46bf6a03bc/small/image.jpg"),
            new HKModel("Thrombolytics", "production/maleAdult/thrombolytics","", "https://human.biodigital.com/media/images/521a294b-ba36-4725-b69e-db713c35b801/small/image.jpg"),
            new HKModel("Brain", "production/maleAdult/male_region_brain_19","", "https://human.biodigital.com/media/images/298286aa-a126-4ea2-a902-3b6716536dae/small/image.jpg"),
            new HKModel("Skin", "production/maleAdult/skin_tissue","", "https://human.biodigital.com/media/images/ee7db82f-2228-40c3-a427-769168bb98df/small/image.jpg")
    ));

    private ModelAdapter modelAdapter;
    private ArrayList<String> dlIds = new ArrayList<>();

    RelativeLayout humanView;
    RelativeLayout libraryView;

    private HKHuman human;
    int downloadCount = 0;
    int repeatCount = 0;

    Menu topMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("BioDigital SDK Demo App");

        HKServices.getInstance().setup(this, this);
        // use getModels to pull your Library from the Content Service for use in your application
//        HKServices.getInstance().getModels();

        GridView gridView = (GridView) findViewById(R.id.gridview);
        libraryView = findViewById(R.id.libraryView);
        humanView = findViewById(R.id.humanView);
        humanView.setVisibility(View.GONE);

        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HKModel model = models.get(position);
                humanView.setVisibility(View.VISIBLE);
                libraryView.setVisibility(View.GONE);
                topMenu.findItem(R.id.action_back).setVisible(true);
                // use presetBackgroundColor to set the desired background before loading
                //                human.ui.presetBackgroundColor(Color.BLUE, Color.GREEN, "linear");
                human.load(model.id);
                if (native_ui) {
                    View menu = humanView.findViewById(R.id.menu);
                    menu.setVisibility(View.VISIBLE);
                    menu.bringToFront();
                    View chap = humanView.findViewById(R.id.category);
                    chap.bringToFront();
                }
            }
        });
        modelAdapter = new ModelAdapter(this,models);
        gridView.setAdapter(modelAdapter);

        View menu = humanView.findViewById(R.id.menu);
        menu.setVisibility(View.INVISIBLE);

        humanView.setVisibility(View.INVISIBLE);
        if (native_ui) {
            HashMap<HumanUIOptions, Boolean> uimap = new HashMap<>();
            uimap.put(HumanUIOptions.all, false);
            human = new HKHuman(humanView, uimap);
        } else {
            human = new HKHuman(humanView);
        }
        human.setInterface(this);

        // Android native UI Sample Code
        final Button resetbutton = humanView.findViewById(R.id.resetbutton);
        final Button dissectbutton = humanView.findViewById(R.id.dissectbutton);
        final Button undobutton = humanView.findViewById(R.id.undobutton);
        final Button xraybutton = humanView.findViewById(R.id.xraybutton);
        final Button isolatebutton = humanView.findViewById(R.id.isolatebutton);
        final Button shareButton = humanView.findViewById(R.id.sharebutton);
        final Button paintbutton = humanView.findViewById(R.id.paintbutton);
        final Button redbutton = humanView.findViewById(R.id.redbutton);
        final Button greenbutton = humanView.findViewById(R.id.greenbutton);
        final Button bluebutton = humanView.findViewById(R.id.bluebutton);
        final Button yellowbutton = humanView.findViewById(R.id.yellowbutton);
        final Button undopaintbutton = humanView.findViewById(R.id.undopaintbutton);
        final View paintmenu = humanView.findViewById(R.id.paintmenu);
        chapterPager = humanView.findViewById(R.id.humanChapterPager);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        topMenu = menu;
        topMenu.findItem(R.id.action_back).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        human.unload();
        humanView.setVisibility(View.GONE);
        libraryView.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("BioDigital SDK Demo App");
        topMenu.findItem(R.id.action_back).setVisible(false);
        return true;
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
            human.scene.enableHighlight();
        } else {
            paintmenu.setVisibility(View.VISIBLE);
            human.scene.disableHighlight();
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
                    repeatCount = 4;
//                    testDownloads();
                    models.addAll( HKServices.getInstance().models);
                    modelAdapter.notifyDataSetChanged();
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

    public void onModelDownloadError(String modelId) {
        System.out.println("** GOT DOWNLOAD ERROR " + modelId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onModelLoaded(String modelId) {
        System.out.println("sample app got model loaded message");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setTitle(human.scene.title);
                System.out.println("%%% MODEL LOADED");
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

    @Override
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
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", mypath);
    }
}