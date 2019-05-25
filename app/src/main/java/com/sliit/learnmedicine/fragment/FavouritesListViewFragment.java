package com.sliit.learnmedicine.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sliit.learnmedicine.R;
import com.sliit.learnmedicine.activity.ViewMedicine;
import com.sliit.learnmedicine.util.ApiUrlHelper;
import com.sliit.learnmedicine.util.MedicineDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Contains the logic of the Favourites List Fragment
 */
public class FavouritesListViewFragment extends Fragment {

    private static final String TAG = "Fav Fragment";

    private ListView favouritesListView;
    private ArrayList<JSONObject> favMedicineList = new ArrayList<>();
    private List<Medicine> medicineList = new ArrayList<>();
    private RequestQueue queue;

    private OnFragmentInteractionListener mListener;

    public FavouritesListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Created Fragment");
    }

    /**
     * Used to inflate the UI layout for this fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites_list_view, container, false);
    }

    /**
     * 1.1 - Retrieve medicine data from the API and save to database (cache)
     * 1.2 - If retrieving fails get data from the database (cached)
     * 2 - Display the medicine data
     * 3 - Set On click listener to see that specific medicine details in the ViewMedicine Activity
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favouritesListView = getView().findViewById(R.id.favListView);

        favouritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String medicineId = medicineList.get(position).getId();
                startActivity(new Intent(getContext(), ViewMedicine.class)
                        .putExtra("medicineId", medicineId));
            }
        });
        String url = ApiUrlHelper.GET_FAVORITES_URL;
        queue = Volley.newRequestQueue(getContext());
        try {
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "Request Succeeded");
                            try {
                                JSONArray medicinesJsonArray = new JSONArray(response);
                                int medicineCount = medicinesJsonArray.length();
                                List<String> medicines = new ArrayList<>();
                                for (int x = 0; x < medicineCount; x++) {
                                    Object medicineObj = medicinesJsonArray.get(x);
                                    JSONObject medicineJson = new JSONObject(medicineObj.toString());
                                    medicines.add(medicineJson.getString("name"));
                                    favMedicineList.add(medicineJson);
                                    String id = medicineJson.getString("id");
                                    String name = medicineJson.getString("name");
                                    String description = medicineJson.getString("description");
                                    boolean favourite = medicineJson.getBoolean("favourite");
                                    medicineList.add(new Medicine(id, name, description, favourite));
                                }

                                ListAdapter adapter = new ArrayAdapter<>(getContext(),
                                        android.R.layout.simple_list_item_1, medicines.toArray());

                                favouritesListView.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Failed to retrieve favourites",
                            Toast.LENGTH_LONG).show();

                    MedicineDatabaseHelper db = new MedicineDatabaseHelper(getContext());
                    medicineList = db.readFavourites();

                    List<String> medicineNames = new ArrayList<>();
                    for (Medicine medicine : medicineList) {
                        medicineNames.add(medicine.getName());
                    }

                    ListAdapter adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_1, medicineNames.toArray());

                    favouritesListView.setAdapter(adapter);
                }
            });

            queue.add(stringRequest);
        }
        catch (Exception ex)
        {
            Toast.makeText(getContext(), "Failed to retrieve favourites",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
