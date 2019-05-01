package com.sliit.learnmedicine;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements MedicineListViewFragment.OnFragmentInteractionListener, FavouritesListViewFragment.OnFragmentInteractionListener {

    private ActionBar toolbar;
    private FragmentManager fragmentManager;

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Started Activity");

        toolbar = getSupportActionBar();
        BottomNavigationView bottomNavigation = findViewById(R.id.navigationView);

        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MedicineListViewFragment medicineListViewFragment = new MedicineListViewFragment();
        fragmentTransaction.add(R.id.main_activity, medicineListViewFragment, "qwe");
        fragmentTransaction.commitNow();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch ((menuItem.getItemId())) {
                case (R.id.navigation_view_medicine_list):
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    MedicineListViewFragment medicineListViewFragment = new MedicineListViewFragment();
                    fragmentTransaction.replace(R.id.main_activity, medicineListViewFragment, "qwe");
                    fragmentTransaction.commitNow();
                    break;
            }
            switch ((menuItem.getItemId())) {
                case (R.id.navigation_view_medicine):
                    toolbar.setTitle("Favourites");
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    FavouritesListViewFragment favouritesListViewFragment = new FavouritesListViewFragment();
                    fragmentTransaction.replace(R.id.main_activity, favouritesListViewFragment, "qwe");
                    fragmentTransaction.commitNow();
                    break;
            }
            switch ((menuItem.getItemId())) {
                case (R.id.navigation_view_help):
                    toolbar.setTitle("Help");
                    Log.i(TAG, "Nav-Help Clicked");

                    break;
            }
            return true;
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "onFragIntrctin(), Uri=".concat(uri.toString()));
    }
}
