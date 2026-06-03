package com.example.carbyer.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.carbyer.model.Dealer;

import org.json.JSONArray;
import org.json.JSONObject;


public class CarsCreateUpdateFragment extends Fragment {

    private int carId;

    private EditText brandET;
    private EditText modelET;
    private EditText yearET;
    private EditText kilometersET;
    private EditText priceET;
    private EditText imageUrlET;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars_create_update, container, false);

        brandET = root.findViewById(R.id.brandET);
        modelET = root.findViewById(R.id.modelET);
        yearET = root.findViewById(R.id.yearET);
        kilometersET = root.findViewById(R.id.kilometersET);
        priceET = root.findViewById(R.id.priceET);
        imageUrlET = root.findViewById(R.id.imageUrlET);

        Button saveB = root.findViewById(R.id.saveB);
        saveB.setOnClickListener(v -> saveCar());

        if (getArguments() != null) {
            carId = getArguments().getInt("carId");
        }

        loadCarData();

        return root;
    }

    private void loadCarData() {

        String token = new SessionManager(requireContext()).getToken();


        ApiClient.get("cars/" + carId, token, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                JSONArray carsJson = body.optJSONArray("car");
                JSONObject c = carsJson.optJSONObject(0);


                JSONArray dealerArr = c.optJSONArray("dealer");

                Dealer dealer = null;

                if (dealerArr != null && dealerArr.length() > 0) {
                    JSONObject d = dealerArr.optJSONObject(0);

                    dealer = new Dealer(
                            d.optInt("id"),
                            d.optString("name"),
                            d.optString("address"),
                            d.optString("city"),
                            d.optString("workingHours")
                    );
                }

                Car car = new Car(
                        c.optInt("id"),
                        c.optString("brand"),
                        c.optString("model"),
                        c.optInt("productionYear"),
                        c.optString("imageURL"),
                        c.optInt("kilometers"),
                        c.optInt("price"),
                        dealer
                );

                brandET.setText(car.brand);
                modelET.setText(car.model);
                yearET.setText(String.valueOf(car.productionYear));
                kilometersET.setText(String.valueOf(car.kilometers));
                priceET.setText(String.valueOf(car.price));
                imageUrlET.setText(car.imageURL);

                Log.d("CARS", "Loaded car: " + car);

            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("CARS", "Error: " + message);
            }
        });

    }

    private void saveCar() {
        JSONObject body = new JSONObject();
        String token = new SessionManager(requireContext()).getToken();

        try {
            body.put("brand", brandET.getText().toString());
            body.put("model", modelET.getText().toString());
            body.put("productionYear",
                    Integer.parseInt(yearET.getText().toString()));
            body.put("kilometers",
                    Integer.parseInt(kilometersET.getText().toString()));
            body.put("price",
                    Integer.parseInt(priceET.getText().toString()));
            body.put("imageURL",
                    imageUrlET.getText().toString());

        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Invalid data",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.put(
                "cars/" + carId,
                body,
                token,
                new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject body) {
                        Toast.makeText(
                                requireContext(),
                                "Car updated",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onError(int httpCode, String message) {
                        Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}