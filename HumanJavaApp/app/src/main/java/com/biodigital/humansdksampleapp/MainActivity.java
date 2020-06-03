package com.biodigital.humansdksampleapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.biodigital.humansdk.*;

import java.util.Arrays;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HKServicesInterface {
    public static final String MODEL_MESSAGE = "com.biodigital.com.MODEL_MESSAGE";

    private ArrayList<HKModel> models = new ArrayList<>(Arrays.asList(new HKModel("Head and Neck", "production/maleAdult/male_region_head_07", "", "human_02_regional_male_head_neck"),
            new HKModel("Thorax", "production/maleAdult/male_region_thorax_07", "", "human_02_regional_male_thorax"),
            new HKModel("Ear: Coronal Cross Section", "production/maleAdult/ear_cross_section_coronal", "", "ear_cross_section_coronal"),
            new HKModel("Atheriosclerosis: Total Occlusion", "production/maleAdult/atherosclerosis_total_occlusion", "", "atherosclerosis_total_occlusion"),
            new HKModel("Hemorrhagic Stroke", "production/maleAdult/hemorrhagic_stroke", "", "hemorrhagic_stroke"),
            new HKModel("Breathing Dynamics", "production/maleAdult/breathing_dynamics", "", "breathing_dynamics")));

    private ModelAdapter modelAdapter;

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
                if (HKServices.getInstance().modelDownloaded(model.id)) {
                    System.out.println("already downloaded");
                } else {
                    System.out.println("not downloaded");
                }
                Intent intent = new Intent(MainActivity.this, HumanActivity.class);
                intent.putExtra(MODEL_MESSAGE,model.id);
                startActivity(intent);
            }
        });
        modelAdapter = new ModelAdapter(this,models);
        gridView.setAdapter(modelAdapter);
    }

    public void onModelDownloaded(String title) {

    }

    public void onModelsLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (HKServices.getInstance().models != null && HKServices.getInstance().models.size() > 0) {
                    models.addAll(HKServices.getInstance().models);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}