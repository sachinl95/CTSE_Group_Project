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
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MedicineListViewFragment.OnFragmentInteractionListener,
        FavouritesListViewFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener {

    private ActionBar toolbar;
    private FragmentManager fragmentManager;

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.reloadOption) {
            List<Fragment> fragments = fragmentManager.getFragments();
            Fragment fragment = fragments.get(0);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitNow();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_activity, fragment, "qwe");
            fragmentTransaction.commitNow();
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch ((menuItem.getItemId())) {
                case (R.id.navigation_view_medicine_list):
                    toolbar.setTitle("Medicine List");
                    Log.i(TAG, "Nav-Medicine List Clicked");
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    MedicineListViewFragment medicineListViewFragment =
                            new MedicineListViewFragment();
                    fragmentTransaction.replace(R.id.main_activity, medicineListViewFragment,
                            "qwe");
                    fragmentTransaction.commitNow();
                    break;
            }
            switch ((menuItem.getItemId())) {
                case (R.id.navigation_view_medicine):
                    toolbar.setTitle("Favourites");
                    Log.i(TAG, "Nav-Fav Clicked");
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    FavouritesListViewFragment favouritesListViewFragment =
                            new FavouritesListViewFragment();
                    fragmentTransaction.replace(R.id.main_activity, favouritesListViewFragment,
                            "qwe");
                    fragmentTransaction.commitNow();
                    break;
            }
            switch ((menuItem.getItemId())) {
                case (R.id.navigation_view_help):
                    toolbar.setTitle("Help");
                    Log.i(TAG, "Nav-Help Clicked");
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    HelpFragment helpFragment = new HelpFragment();
                    fragmentTransaction.replace(R.id.main_activity, helpFragment, "qwe");
                    fragmentTransaction.commitNow();
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
