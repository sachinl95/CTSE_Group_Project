package com.sliit.learnmedicine;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AddMedicine extends AppCompatActivity {

    private Button addButton;
    private TextView nameTxtView, descriptionTxtView, imageUrlTxtView;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Medicine");

        queue = Volley.newRequestQueue(this);
        nameTxtView = findViewById(R.id.nameTxt);
        descriptionTxtView = findViewById(R.id.descriptionTxt);
        imageUrlTxtView = findViewById(R.id.imageUrlTxt);
        addButton = findViewById(R.id.addMedicineBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameTxtView.getText().toString();
                final String description = descriptionTxtView.getText().toString();
                final String imageUrl = imageUrlTxtView.getText().toString();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name", name);
                    jsonBody.put("description", description);
                    if (!imageUrl.trim().equals("")) {
                        if (Patterns.WEB_URL.matcher(imageUrl).matches()) {
                            jsonBody.put("imageUrl", imageUrl);
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Image URL", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } catch (JSONException exc) {
                    exc.printStackTrace();
                }
                final String mRequestBody = jsonBody.toString();

                String url = ApiUrlHelper.ADD_MEDICINE;
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                finish();
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
        });
    }
}
