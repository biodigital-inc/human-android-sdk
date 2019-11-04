package com.biodigital.humansdksampleapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.biodigital.humansdk.*;

import java.util.Arrays;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HKServicesInterface {
    public static final String MODULE_MESSAGE = "com.biodigital.com.MODULE_MESSAGE";

    private HKServices mind;

    private ArrayList<HKModule> modules = new ArrayList<>(Arrays.asList(new HKModule("Head and Neck", "production/maleAdult/human_02_regional_male_head_neck.json", "", "human_02_regional_male_head_neck"),
            new HKModule("Thorax", "production/maleAdult/human_02_regional_male_thorax.json", "", "human_02_regional_male_thorax"),
            new HKModule("Ear: Coronal Cross Section", "production/maleAdult/ear_cross_section_coronal.json", "", "ear_cross_section_coronal"),
            new HKModule("Atheriosclerosis: Total Occlusion", "production/maleAdult/atherosclerosis_total_occlusion.json", "", "atherosclerosis_total_occlusion"),
            new HKModule("Hemorrhagic Stroke", "production/maleAdult/hemorrhagic_stroke.json", "", "hemorrhagic_stroke"),
            new HKModule("Breathing Dynamics", "production/maleAdult/breathing_dynamics.json", "", "breathing_dynamics")));

    private ModuleAdapter moduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("BioDigital SDK Content Library");

        mind = new HKServices(this, "<YOUR KEY>", "<YOUR SECRET>");
        mind.setInterface(this);
        mind.getModules();
//        mind.setLanguage(HKLanguage.FRENCH);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HKModule module = modules.get(position);
                if (mind.moduleDownloaded(module.id)) {
                    System.out.println("already downloaded");
                } else {
                    System.out.println("not downloaded");
                }
                Intent intent = new Intent(MainActivity.this, HumanActivity.class);
                intent.putExtra(MODULE_MESSAGE,module.id);
                startActivity(intent);
            }
        });
        moduleAdapter = new ModuleAdapter(this,modules);
        gridView.setAdapter(moduleAdapter);
    }

    public void onModulesLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mind.modules != null && mind.modules.size() > 0) {
                    modules.addAll(mind.modules);
                    moduleAdapter.notifyDataSetChanged();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("OK");
                        }
                    });
                    builder.setTitle("Dashboard is empty");
                    builder.setMessage("Please go to https://human.biodigital.com/ to add modules to your Dashboard");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                System.out.println("got modules " + modules.size());
            }
        });
    }

    /**
     * API Callback - SDK failed validation
     */
    public void onInvalidSDK() {
        System.out.println("error!  we aren't validated with BioDigital!");
    }

    /**
     * API Callback - SDK passed validation
     */
    public void onValidSDK() {
        System.out.println("success!  we are validated with BioDigital!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}