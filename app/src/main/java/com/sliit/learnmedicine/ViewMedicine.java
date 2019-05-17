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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sliit.learnmedicine.DTO.Medicine;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewMedicine extends AppCompatActivity {

    private RequestQueue queue;
    boolean isFavourite;
    private FloatingActionButton floatingActionButton;
    private TextView textView;
    private TextView descriptionView;
    private ImageView imageView;
    private String url = "https://images.pexels.com/photos/415825/pexels-photo-415825.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500";

    private final static String TAG = "ViewMedicine";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medicine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.textView);
        descriptionView = findViewById(R.id.descriptionView);

        setSupportActionBar(toolbar);
        isFavourite = false;
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabRem);
        floatingActionButton.setVisibility(View.INVISIBLE);
        imageView = (ImageView) findViewById(R.id.imageView);
        loadImageFromURL(url);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = this.getIntent();
        final String medicineId = intent.getStringExtra("medicineId");
        queue = Volley.newRequestQueue(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addToFavourite(medicineId, true, view);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addToFavourite(medicineId, false, view);
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
                            textView.setText(medicineName);
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

                MedicineDatabaseHelper dbHelper =
                        new MedicineDatabaseHelper(getApplicationContext());
                try {
                    Medicine medicine = dbHelper.readOne(medicineId);
                    String medicineName = medicine.getName();
                    activity.setTitle(medicineName);
                    textView.setText(medicineName);
                    descriptionView = findViewById(R.id.descriptionView);
                    descriptionView.setText(medicine.getDescription());
                    if (medicine.isFavourite()) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    Log.i(TAG, "Null Pointer Exception");
                    finish();

                }
                Toast.makeText(getApplicationContext(),
                        "Failed to retrieve medicine information", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);

    }

    private void setupUI(Medicine medicine) {

    }

    public void addToFavourite(String medicineId, final boolean status, final View view) {
        String url = ApiUrlHelper.UPDATE_FAVORITES_URL.concat("/").concat(medicineId) + "/"
                + status;
        try {
            StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            if (status) {
                                Snackbar.make(view, "Added to Favourites",
                                        Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                Snackbar.make(view, "Removed from Favourites",
                                        Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            finish();
                            startActivity(getIntent());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't update favourites: Connection Error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            ) {


            };

            queue.add(putRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromURL(String url)
    {
        Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
                .into(imageView,new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

}
