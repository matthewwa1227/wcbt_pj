package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RegionSelectionActivity extends AppCompatActivity {

    private CheckBox cbKowloon;
    private CheckBox cbNewTerritories;
    private CheckBox cbHongKongIsland;

    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_selection);

        bindViews();
        configureRegionCards();
        configureNextButton();
        configureBottomNavigation();
    }

    private void bindViews() {
        cbKowloon = findViewById(R.id.cbKowloon);
        cbNewTerritories = findViewById(R.id.cbNewTerritories);
        cbHongKongIsland = findViewById(R.id.cbHongKongIsland);

        btnNext = findViewById(R.id.btnNext);
    }

    private void configureRegionCards() {
        findViewById(R.id.cardKowloon).setOnClickListener(v ->
                cbKowloon.setChecked(!cbKowloon.isChecked())
        );

        findViewById(R.id.cardNewTerritories).setOnClickListener(v ->
                cbNewTerritories.setChecked(
                        !cbNewTerritories.isChecked()
                )
        );

        findViewById(R.id.cardHongKongIsland).setOnClickListener(v ->
                cbHongKongIsland.setChecked(
                        !cbHongKongIsland.isChecked()
                )
        );
    }

    private void configureNextButton() {
        btnNext.setOnClickListener(v -> {
            if (!hasSelectedRegion()) {
                Toast.makeText(
                        RegionSelectionActivity.this,
                        "請至少選擇一個地區",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String selectedRegions = buildSelectedRegions();

            Toast.makeText(
                    RegionSelectionActivity.this,
                    "已選擇: " + selectedRegions,
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    RegionSelectionActivity.this,
                    JobListActivity.class
            );

            /*
             * The selected regions are passed forward so JobListActivity
             * can use them later when region filtering is implemented.
             */
            intent.putExtra(
                    "selectedRegions",
                    selectedRegions
            );

            startActivity(intent);
        });
    }

    private void configureBottomNavigation() {
        findViewById(R.id.tabWorkList).setOnClickListener(v -> {
            // This screen is already part of the work-list flow.
        });

        findViewById(R.id.tabHistory).setOnClickListener(v ->
                Toast.makeText(
                        RegionSelectionActivity.this,
                        "工作記錄 coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        findViewById(R.id.tabMyJobs).setOnClickListener(v -> {
            Intent intent = new Intent(
                    RegionSelectionActivity.this,
                    MyJobsLandingActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        findViewById(R.id.tabProfile).setOnClickListener(v ->
                Toast.makeText(
                        RegionSelectionActivity.this,
                        "個人檔案 coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private boolean hasSelectedRegion() {
        return cbKowloon.isChecked()
                || cbNewTerritories.isChecked()
                || cbHongKongIsland.isChecked();
    }

    private String buildSelectedRegions() {
        StringBuilder regions = new StringBuilder();

        if (cbKowloon.isChecked()) {
            regions.append("九龍區");
        }

        if (cbNewTerritories.isChecked()) {
            appendSeparator(regions);
            regions.append("新界區");
        }

        if (cbHongKongIsland.isChecked()) {
            appendSeparator(regions);
            regions.append("港島區");
        }

        return regions.toString();
    }

    private void appendSeparator(StringBuilder regions) {
        if (regions.length() > 0) {
            regions.append(", ");
        }
    }
}