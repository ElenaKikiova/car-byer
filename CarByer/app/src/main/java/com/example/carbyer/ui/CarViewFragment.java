package com.example.carbyer.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.carbyer.ApiClient;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;
import com.example.carbyer.model.Car;
import com.example.carbyer.model.Dealer;

import org.json.JSONArray;
import org.json.JSONObject;

public class CarViewFragment extends Fragment {

    private int carId;

    private ImageView imageView;
    private TextView nameTV;
    private TextView yearTV;
    private TextView kilometersTV;
    private TextView dealerNameTV;
    private TextView engineTV;
    private TextView isManualTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_car_view, container, false);

        imageView = root.findViewById(R.id.imageView);
        nameTV = root.findViewById(R.id.nameTV);
        yearTV = root.findViewById(R.id.yearTV);
        kilometersTV = root.findViewById(R.id.kilometersTV);
        dealerNameTV = root.findViewById(R.id.dealerNameTV);
        engineTV = root.findViewById(R.id.engineTV);
        isManualTV = root.findViewById(R.id.isManualTV);

        if (getArguments() != null) {
            carId = getArguments().getInt("carId", -1);
            loadCar();
        }

        return root;
    }

    private void loadCar() {

        String token = new SessionManager(requireContext()).getToken();

        ApiClient.get("cars/" + carId, token, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject body) {

                JSONArray arr = body.optJSONArray("car");
                if (arr == null || arr.length() == 0) return;

                JSONObject c = arr.optJSONObject(0);

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

                bind(car);

                Log.d("CAR_VIEW", "Loaded car: " + car);
            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("CAR_VIEW", "Error: " + message);
            }
        });
    }

    private void bind(Car car) {

        nameTV.setText(car.brand + " " + car.model);
        yearTV.setText(String.valueOf(car.productionYear));
        kilometersTV.setText(getString(R.string.kilometers_suffix, car.kilometers));

        if (car.dealer != null) {
            dealerNameTV.setText(car.dealer.name + " (" + car.dealer.city + ")");
        }

        engineTV.setText(
                getString(
                        R.string.engine_label,
                        car.engine != null ? car.engine : getString(R.string.engine_na)
                )
        );
        isManualTV.setText(
                getString(
                        R.string.transmission_label,
                        car.automatic
                                ? getString(R.string.transmission_manual)
                                : getString(R.string.transmission_automatic)
                )
        );

        Glide.with(requireContext())
                .load(car.imageURL)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);
    }
}