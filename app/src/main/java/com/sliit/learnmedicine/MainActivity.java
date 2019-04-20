package com.sliit.learnmedicine;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sliit.learnmedicine.DTO.Medicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<JSONObject> medicineList = new ArrayList<>();
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = getSupportActionBar();
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navigationView);

//        bottomNavigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener);

        initializeComponents();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String medicine = String.valueOf(parent.getItemAtPosition(position));
                try {
                    String medicineId = medicineList.get(position).getString("id");
                    startActivity(new Intent(getApplicationContext(), ViewMedicine.class).putExtra("medicineId", medicineId));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Invalid Medicine Object", Toast.LENGTH_LONG).show();
                }
            }
        });

        String url = "https://young-temple-33785.herokuapp.com/medicines/get-all";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Request Succeeded");
                        try {
                            JSONArray medicinesJsonArray = new JSONArray(response);
                            int medicineCount = medicinesJsonArray.length();
                            List<String> medicines = new ArrayList<>();
                            for (int x = 0; x < medicineCount; x++) {
                                Object medicineObj = medicinesJsonArray.get(x);
                                JSONObject medicineJson = new JSONObject(medicineObj.toString());
                                medicines.add(medicineJson.getString("name"));
                                medicineList.add(medicineJson);
                            }

                            ListAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, medicines.toArray());

                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve medicines", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }

    private void initializeComponents() {
        listView = findViewById(R.id.listView);
    }
    private BottomNavigationView.OnNavigationItemReselectedListener mOnNavigationItemReselectedListener=
            new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
//                    Fragment fragment;
                    switch ((menuItem.getItemId())){
                        case (R.id.navigation_view_medicine_list):
                            toolbar.setTitle("Medicines");
                            return;
                    }
                    switch ((menuItem.getItemId())){
                        case (R.id.navigation_view_medicine):
                            toolbar.setTitle("Medicine List");
                            return;
                    }
                    switch ((menuItem.getItemId())){
                        case (R.id.navigation_view_help):
                            toolbar.setTitle("Help");
                            return;
                    }
                    return;
                }
            };
}
