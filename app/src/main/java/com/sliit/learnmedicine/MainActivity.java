package com.sliit.learnmedicine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), ViewMedicine.class));
            }
        });

        String[] foods = {"Mesalazine", "Acetaminophen", "Amoxicillin", "Hydrocodone",
                "Haldol", "Hytrin", "Hydrochlorothiazide", "Codeine", "Lisinopril", "Adderall", "Acetaminophen",
                "Oxycodone", "Prednisone"};
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foods);

        listView.setAdapter(adapter);

    }

    private void initializeComponents() {
        listView = findViewById(R.id.listView);
    }

    private void getMedicines() {

    }
}
