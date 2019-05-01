package com.sliit.learnmedicine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewMedicine extends AppCompatActivity {

    RequestQueue queue;
    boolean isFavourite;
    FloatingActionButton floatingActionButton;

    private final static String TAG = "ViewMedicine";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medicine);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isFavourite = false;
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabRem);
        floatingActionButton.setVisibility(View.INVISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = this.getIntent();
        final String medicineId = intent.getStringExtra("medicineId");
        queue = Volley.newRequestQueue(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addToFavourite(medicineId, true);
                Snackbar.make(view, "Added to Favourites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addToFavourite(medicineId, false);
                Snackbar.make(v, "Removed from Favourites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Activity activity = this;
        String url = ApiUrlHelper.GET_ONE_URL.concat("/").concat(medicineId);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Request Succeeded");
                        try {
                            JSONObject medicineDetails = new JSONObject(response);

                            String medicineName = medicineDetails.getString("name");
                            isFavourite = medicineDetails.getBoolean("favourite");
                            activity.setTitle(medicineName);
                            TextView textView = findViewById(R.id.textView);
                            textView.setText(medicineName);
                            TextView descriptionView = findViewById(R.id.descriptionView);
                            descriptionView.setText(medicineDetails.getString("description"));
                            if (isFavourite) {
                                floatingActionButton.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve medicine information", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);

    }

    public void addToFavourite(String medicineId, boolean status) {
        String url = ApiUrlHelper.UPDATE_FAVORITES_URL.concat("/").concat(medicineId) + "/" + status;
        try {
            StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            finish();
                            startActivity(getIntent());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                            finish();
                            startActivity(getIntent());
                        }
                    }
            ) {


            };

            queue.add(putRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
