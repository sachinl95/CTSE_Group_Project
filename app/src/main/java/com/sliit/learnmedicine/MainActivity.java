package com.sliit.learnmedicine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        String[] foods = {"Mesalazine", "Acetaminophen", "Amoxicillin", "Hydrocodone",
        "Haldol", "Hytrin", "Hydrochlorothiazide", "Codeine", "Lisinopril", "Adderall", "Acetaminophen",
        "Oxycodone", "Prednisone"};
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foods);

        listView.setAdapter(adapter);
    }

    private void initializeComponents() {
        listView = findViewById(R.id.listView);
    }

    private void getMedicines() {

    }
}
