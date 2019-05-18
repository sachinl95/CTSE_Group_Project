package com.sliit.learnmedicine.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sliit.learnmedicine.R;
import com.sliit.learnmedicine.util.ApiUrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * This class contains the logic of the Update Medicine Screen
 */
public class UpdateMedicine extends AppCompatActivity {

    private final static String TAG = "UpdateMedicineActivity";

    private TextView nameTxtView, descriptionTxtView, urlTxtView;
    private Button updateButton;
    private RequestQueue queue;
    private String medicineId;

    /**
     * Initialize UI elements - Find all the UI elements
     */
    private void initialize() {
        updateButton = findViewById(R.id.updateMedicineBtn);
        nameTxtView = findViewById(R.id.nameTxt);
        descriptionTxtView = findViewById(R.id.descriptionTxt);
        urlTxtView = findViewById(R.id.imageUrlTxt);
    }

    /**
     * 1 - Get Medicine ID from the intent
     * 2 - Create HTTP Request
     * 3 - Add Request to queue
     * 4 - Get response data and set them to text views
     * 5 - Set the Update Button Click Listener
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medicine);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("Update Medicine");

        initialize();

        // Load Data

        Intent intent = getIntent();
        medicineId = intent.getStringExtra("medicineId");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ApiUrlHelper.GET_ONE_URL.concat(medicineId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Request Succeeded");
                        try {
                            JSONObject medicineDetails = new JSONObject(response);


                            String medicineName = medicineDetails.getString("name");
                            nameTxtView.setText(medicineName);
                            descriptionTxtView.setText(medicineDetails.getString("description"));
                            urlTxtView.setText(medicineDetails.getString("imageURL"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),
                        "Failed to retrieve medicine information", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        queue = Volley.newRequestQueue(this);


        queue.add(stringRequest);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    /**
     * 1 - Get the data from the text views
     * 2 - Create JSON Body with the data
     * 3 - Create HTTP Request with data
     * 4 - Add request to the queue
     */
    private void update() {

        final String name = nameTxtView.getText().toString();
        final String description = descriptionTxtView.getText().toString();
        final String imageUrl = urlTxtView.getText().toString();

        if (name.length() < 1 || description.length() < 1 || imageUrl.length() < 1) {
            Toast.makeText(getApplicationContext(), "Fields Cannot Be Empty", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("description", description);
            if (!imageUrl.trim().equals("")) {
                if (Patterns.WEB_URL.matcher(imageUrl).matches()) {
                    jsonBody.put("imageURL", imageUrl);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Image URL", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        } catch (JSONException exc) {
            exc.printStackTrace();
        }
        final String mRequestBody = jsonBody.toString();
        Log.i(TAG, "JSON Body ".concat(mRequestBody));

        String url = ApiUrlHelper.UPDATE_MEDICINE.concat("/").concat(medicineId);
        StringRequest request = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        finish();
                        Toast.makeText(getApplicationContext(), "Medicine Updated", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

        };

        queue.add(request);
    }
}
