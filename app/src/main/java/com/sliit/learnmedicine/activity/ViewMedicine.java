package com.sliit.learnmedicine.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.sliit.learnmedicine.R;
import com.sliit.learnmedicine.activity.UpdateMedicine;
import com.sliit.learnmedicine.util.ApiUrlHelper;
import com.sliit.learnmedicine.util.MedicineDatabaseHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains the logic of the Medicine View UI/Screen
 */
public class ViewMedicine extends AppCompatActivity {

    private RequestQueue queue;
    boolean isFavourite;
    private FloatingActionButton floatingActionButton;
    private TextView textView;
    private TextView descriptionView;
    private ImageView imageView;
    private String EmptyImageurl = "https://dwsinc.co/wp-content/uploads/2018/05/image-not-found.jpg";
    private String medicineId;

    private final static String TAG = "ViewMedicine";

    /**
     * 1 - Initialize UI elements (Buttons, TextViews, ...)
     * 2 - Retrieve medicine data from the API
     * 3 - Display the AddToFavorites or RemoveFromFavourites Button Based on whether
     * the medicine is already marked as favourite or not
     *
     * @param savedInstanceState
     */
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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = this.getIntent();
        final String medicineId = intent.getStringExtra("medicineId");
        this.medicineId = medicineId;
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


        getMedicineDataAndDisplay();

    }

    private void setupUI(Medicine medicine) {

    }

    /**
     * Called to mark a medicine as favorite
     *
     * @param medicineId The unique ID of the medicine
     * @param status     Whether the medicine should be marked as favourite or not
     * @param view       The view
     */
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
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    },
                                    1000);

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

    /**
     * Load the image from the URL
     *
     * @param url
     */
    private void loadImageFromURL(String url) {
        if (url == "null") {
            url = this.EmptyImageurl;
        }
        Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    /**
     * Set the menu to the toolbar on the top-right
     *
     * @param menu The layout of the menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_options_menu, menu);
        return true;
    }

    /**
     * Autogenerated; Will be called if an option in the toolbar menu is selected
     *
     * @param item The item selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.editOption) {
            Intent intent = new Intent(this, UpdateMedicine.class);
            intent.putExtra("medicineId", medicineId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 1 - Retrieve the medicine data from the API
     * 2 - Display the data
     */
    private void getMedicineDataAndDisplay() {
        String url = ApiUrlHelper.GET_ONE_URL.concat("/").concat(medicineId);
        final Activity activity = this;

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
                            String url = medicineDetails.getString("imageURL");
                            loadImageFromURL(url);
                            descriptionView.setText(medicineDetails.getString("description"));
                            if (isFavourite) {
                                floatingActionButton.show();
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
                        floatingActionButton.show();
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


    /**
     * Refresh the page data when resuming
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getMedicineDataAndDisplay();
    }
}
