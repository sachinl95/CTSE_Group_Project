package com.sliit.learnmedicine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MedicineListViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MedicineListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicineListViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "APP-ListFragment";

    private ListView listView;
    private ArrayList<JSONObject> medicineJsonList = new ArrayList<>();
    private List<Medicine> medicineList = new ArrayList<>();

    RequestQueue queue;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MedicineListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedicineListViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MedicineListViewFragment newInstance(String param1, String param2) {
        MedicineListViewFragment fragment = new MedicineListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medicine_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");
        listView = getView().findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String medicine = String.valueOf(parent.getItemAtPosition(position));
                String medicineId = medicineList.get(position).getId();
                startActivity(new Intent(getContext(), ViewMedicine.class)
                        .putExtra("medicineId", medicineId));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                Log.i(TAG, "Long Click");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final Medicine medicine = medicineList.get(position);

                        StringRequest request = new StringRequest(Request.Method.DELETE, ApiUrlHelper.DELETE_MEDICINE.concat("/").concat(medicine.getId()), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                refreshList();
                                Toast.makeText(getContext(), medicine.getName().concat(" Deleted"), Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_LONG).show();
                            }
                        });

                        queue.add(request);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });

                builder.setMessage("Are you sure want to delete the selected medicine?")
                        .setTitle("Confirm Delete");

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        queue = Volley.newRequestQueue(getContext());

        refreshList();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        medicineList.clear();
        final Context context = getContext();

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, ApiUrlHelper.GET_ALL_URL,
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
                                medicineJsonList.add(medicineJson);
                                String id = medicineJson.getString("id");
                                String name = medicineJson.getString("name");
                                String description = medicineJson.getString("description");
                                boolean favourite = medicineJson.getBoolean("favourite");
                                medicineList.add(new Medicine(id, name, description, favourite));
                            }

                            ListAdapter adapter = new ArrayAdapter<>(context,
                                    android.R.layout.simple_list_item_1, medicines.toArray());

                            listView.setAdapter(adapter);
                            MedicineDatabaseHelper dbHelper = new MedicineDatabaseHelper(context);
                            dbHelper.saveAll(medicineList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Failed to retrieve medicines",
                        Toast.LENGTH_LONG).show();
                MedicineDatabaseHelper dbHelper = new MedicineDatabaseHelper(context);
                medicineList = dbHelper.readAll();

                List<String> medicineNames = new ArrayList<>();
                for (Medicine medicine : medicineList) {
                    medicineNames.add(medicine.getName());
                }

                ListAdapter adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, medicineNames.toArray());
                listView.setAdapter(adapter);
            }
        });

        queue.add(stringRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach()");
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
