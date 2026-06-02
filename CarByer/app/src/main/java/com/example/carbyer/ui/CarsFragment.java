package com.example.carbyer.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carbyer.ApiClient;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class CarsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars, container, false);

        Log.d("CARS", "am logging");

        loadCars();

        return root;
    }

    private void loadCars() {

        String token = new SessionManager(requireContext()).getToken();

        ApiClient.get("cars", token, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                Log.d("CARS", body.toString());

                JSONArray cars = body.optJSONArray("cars");

                if (cars != null) {
                    Log.d("CARS", "Found " + cars.length() + " cars");
                }
            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("CARS", "Error: " + message);
            }
        });

        Toast.makeText(requireContext(),
                "CarsFragment loaded",
                Toast.LENGTH_LONG).show();
    }
}