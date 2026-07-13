package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class RegionSelectionActivity extends AppCompatActivity {

    private CheckBox cbKowloon, cbNewTerritories, cbHongKongIsland;
    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_selection);

        cbKowloon = findViewById(R.id.cbKowloon);
        cbNewTerritories = findViewById(R.id.cbNewTerritories);
        cbHongKongIsland = findViewById(R.id.cbHongKongIsland);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            if (!cbKowloon.isChecked() && !cbNewTerritories.isChecked() && !cbHongKongIsland.isChecked()) {
                Toast.makeText(this, "請至少選擇一個地區", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build selected regions string
            StringBuilder regions = new StringBuilder();
            if (cbKowloon.isChecked()) regions.append("九龍區 ");
            if (cbNewTerritories.isChecked()) regions.append("新界區 ");
            if (cbHongKongIsland.isChecked()) regions.append("港島區 ");

            Toast.makeText(this, "已選擇: " + regions.toString().trim(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegionSelectionActivity.this, JobListActivity.class);
            startActivity(intent);
        });
    }
}