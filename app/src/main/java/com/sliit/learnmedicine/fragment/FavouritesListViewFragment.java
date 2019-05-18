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
import com.sliit.learnmedicine.util.ApiUrlHelper;
import com.sliit.learnmedicine.DTO.Medicine;
import com.sliit.learnmedicine.util.MedicineDatabaseHelper;
import com.sliit.learnmedicine.R;
import com.sliit.learnmedicine.ViewMedicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavouritesListViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavouritesListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesListViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "Fav Fragment";

    private ListView favouritesListView;
    private ArrayList<JSONObject> favMedicineList = new ArrayList<>();
    private List<Medicine> medicineList = new ArrayList<>();
    private RequestQueue queue;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FavouritesListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouritesListViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouritesListViewFragment newInstance(String param1, String param2) {
        FavouritesListViewFragment fragment = new FavouritesListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Created Fragment");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites_list_view, container,
                false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favouritesListView = getView().findViewById(R.id.favListView);

        favouritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String medicine = String.valueOf(parent.getItemAtPosition(position));
                String medicineId = medicineList.get(position).getId();
                startActivity(new Intent(getContext(), ViewMedicine.class)
                        .putExtra("medicineId", medicineId));
            }
        });
        String url = ApiUrlHelper.GET_FAVORITES_URL;
        queue = Volley.newRequestQueue(getContext());

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
