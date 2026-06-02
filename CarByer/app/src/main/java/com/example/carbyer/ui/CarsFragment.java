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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbyer.ApiClient;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;
import com.example.carbyer.adapter.CarAdapter;
import com.example.carbyer.model.Car;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarsFragment extends Fragment {

    private CarAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars, container, false);

        RecyclerView rv = root.findViewById(R.id.carsRV);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new CarAdapter(car -> {
            Toast.makeText(requireContext(), "Loading cars..", Toast.LENGTH_SHORT).show();
        });
        rv.setAdapter(adapter);

        loadCars();

        return root;
    }

    private void loadCars() {

        String token = new SessionManager(requireContext()).getToken();


        ApiClient.get("cars", token, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                JSONArray carsJson = body.optJSONArray("cars");

                List<Car> cars = new ArrayList<>();

                for (int i = 0; i < carsJson.length(); i++) {

                    JSONObject c = carsJson.optJSONObject(i);

                    cars.add(new Car(
                            c.optInt("id"),
                            c.optString("brand"),
                            c.optString("model"),
                            c.optInt("productionYear"),
                            c.optString("imageURL"),
                            c.optInt("kilometers"),
                            c.optInt("price")
                    ));
                }

                Log.d("CARS", "Loaded cars: " + cars);

                adapter.setItems(cars);
            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("CARS", "Error: " + message);
            }
        });

    }
}