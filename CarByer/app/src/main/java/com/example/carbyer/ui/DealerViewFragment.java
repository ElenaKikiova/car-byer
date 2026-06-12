package com.example.carbyer.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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

public class DealerViewFragment extends Fragment {

    private int dealerId;

    private TextView nameTV, cityTV, addressTV, workingHoursTV, carsCountTV;
    private RecyclerView carsRV;
    private CarAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dealer_view, container, false);

        nameTV = root.findViewById(R.id.nameTV);
        cityTV = root.findViewById(R.id.cityTV);
        addressTV = root.findViewById(R.id.addressTV);
        workingHoursTV = root.findViewById(R.id.workingHoursTV);
        carsCountTV = root.findViewById(R.id.carsCountTV);

        carsRV = root.findViewById(R.id.carsRV);
        carsRV.setLayoutManager(new LinearLayoutManager(requireContext()));


        adapter = new CarAdapter(new CarAdapter.OnClick() {
            @Override
            public void onCarClick(Car car) {
                Bundle args = new Bundle();
                args.putInt("carId", car.id);

                Navigation.findNavController(requireView()).navigate(R.id.nav_car_view, args);
            }

            @Override
            public void onCarEdit(Car car) {
                Bundle args = new Bundle();
                args.putInt("carId", car.id);
                Navigation.findNavController(requireView()).navigate(R.id.nav_car_create_update, args);
            }

            @Override
            public void onCarDelete(Car car) {
                DeleteDialogFragment.newInstance(getString(R.string.car), car.id)
                        .show(getParentFragmentManager(), "delete_dialog");

                getParentFragmentManager().setFragmentResultListener(
                        "delete_result",
                        DealerViewFragment.this,
                        (requestKey, result) -> {

                            boolean confirmed =
                                    result.getBoolean("confirmed");

                            if (confirmed) {
                                deleteCar(car.id);
                            }
                        }
                );
            }
        });

        carsRV.setAdapter(adapter);

        if (getArguments() != null) {
            dealerId = getArguments().getInt("dealerId", -1);
            loadDealer();
        }

        return root;
    }

    private void loadDealer() {

        String token = new SessionManager(requireContext()).getToken();

        ApiClient.get("dealers/" + dealerId, token, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                JSONArray arr = body.optJSONArray("dealer");
                if (arr == null || arr.length() == 0) return;

                JSONObject d = arr.optJSONObject(0);

                JSONArray carsJson = d.optJSONArray("cars");

                List<Car> cars = new ArrayList<>();

                for (int i = 0; carsJson != null && i < carsJson.length(); i++) {

                    JSONObject c = carsJson.optJSONObject(i);

                    cars.add(new Car(
                            c.optInt("id"),
                            c.optString("brand"),
                            c.optString("model"),
                            c.optInt("productionYear"),
                            c.optString("imageURL"),
                            c.optInt("kilometers"),
                            c.optInt("price"),
                            null
                    ));
                }

                nameTV.setText(d.optString("name"));
                cityTV.setText(d.optString("city"));
                addressTV.setText(d.optString("address"));
                workingHoursTV.setText(d.optString("workingHours"));
                carsCountTV.setText(
                        getString(R.string.cars_count, cars.size())
                );

                adapter.setItems(cars);
            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("DEALER_VIEW", message);
            }
        });
    }


    public void deleteCar(int carId){

        String token = new SessionManager(requireContext()).getToken();

        ApiClient.delete(
                "cars/" + carId,
                token,
                new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject body) {

                        Toast.makeText(requireContext(), R.string.delete_successful, Toast.LENGTH_SHORT).show();

                        loadDealer();

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