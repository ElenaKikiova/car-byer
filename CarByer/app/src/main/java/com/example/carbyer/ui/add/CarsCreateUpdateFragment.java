package com.example.carbyer.ui.add;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.carbyer.ApiClient;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;
import com.example.carbyer.adapter.DealerSpinnerAdapter;
import com.example.carbyer.model.Car;
import com.example.carbyer.model.Dealer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CarsCreateUpdateFragment extends Fragment {

    private int carId;

    private EditText brandET;
    private EditText modelET;
    private EditText yearET;
    private EditText kilometersET;
    private EditText priceET;
    private EditText imageUrlET;

    private Spinner dealerSpinner;
    private List<Dealer> dealerList = new ArrayList<>();
    private int selectedDealerId = -1;

    private EditText engineLET;
    private Switch automaticSwitch;

    String mode;

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
        engineLET = root.findViewById(R.id.engineLET);
        automaticSwitch = root.findViewById(R.id.automaticSwitch);

        Button saveB = root.findViewById(R.id.saveB);
        saveB.setOnClickListener(v -> saveCar());

        if (getArguments() != null) {
            mode = getArguments().getString("mode", "create");
            carId = getArguments().getInt("carId", -1);

            if ("edit".equals(mode) && carId != -1) {
                loadCarData();
            }
        } else {
            mode = "create";
            carId = -1;
        }

        dealerSpinner = root.findViewById(R.id.dealerSpinner);
        loadDealers();

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
                        dealer,
                        c.optString("engineL"),
                        c.optBoolean("automatic")
                );
                selectedDealerId = car.dealer != null ? car.dealer.id : -1;

                brandET.setText(car.brand);
                modelET.setText(car.model);
                yearET.setText(String.valueOf(car.productionYear));
                kilometersET.setText(String.valueOf(car.kilometers));
                priceET.setText(String.valueOf(car.price));
                imageUrlET.setText(car.imageURL);
                engineLET.setText(c.optString("engineL"));
                int automatic = c.optInt("automatic", 0);
                automaticSwitch.setChecked(automatic == 1);

                Log.d("CARS", "Loaded car: " + car);

            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("CARS", "Error: " + message);
            }
        });

    }

    private void loadDealers() {

        String token = new SessionManager(requireContext()).getToken();


        ApiClient.get("dealers", token, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject body) {

                if (!isAdded() || dealerSpinner == null) return;

                int selectedDealerPosition = -1;

                JSONArray arr = body.optJSONArray("dealers");
                if (arr == null) return;

                dealerList.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject d = arr.optJSONObject(i);
                    int dealerId = d.optInt("id");

                    dealerList.add(new Dealer(
                            dealerId,
                            d.optString("name"),
                            d.optString("address"),
                            d.optString("city"),
                            d.optString("workingHours")
                    ));

                    if(dealerId == selectedDealerId){
                        selectedDealerPosition = i;
                    }
                }

                if (!isAdded()) return;

                DealerSpinnerAdapter adapter =
                        new DealerSpinnerAdapter(requireContext(), dealerList);

                dealerSpinner.setAdapter(adapter);

                dealerSpinner.setSelection(selectedDealerPosition);
            }

            @Override
            public void onError(int httpCode, String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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
            body.put("price", priceET.getText().toString());
            body.put("imageURL",
                    imageUrlET.getText().toString());
            body.put("engineL", engineLET.getText().toString());
            body.put("automatic", automaticSwitch.isChecked() ? 1 : 0);

            Dealer selected = (Dealer) dealerSpinner.getSelectedItem();
            body.put("dealerId", selected.id);

        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    R.string.invalid_data,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.Callback callback = new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject body) {

                String message;

                if ("create".equals(mode)) {
                    message = getString(R.string.car_created_successfully);
                } else {
                    message = getString(R.string.car_updated_successfully);
                }

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(CarsCreateUpdateFragment.this).navigate(R.id.nav_cars);

            }

            @Override
            public void onError(int httpCode, String message) {
                Toast.makeText(
                        requireContext(),
                        message,
                        Toast.LENGTH_SHORT
                ).show();
            }
        };

        if(carId == -1) {
            ApiClient.post(
                    "cars/",
                    body,
                    token,
                    callback
            );
        }
        else {
            ApiClient.put(
                    "cars/" + carId,
                    body,
                    token,
                    callback
            );
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CARS", "CarsCrFragment destroyed");
    }
}