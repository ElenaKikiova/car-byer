package com.example.carbyer.ui;

import android.graphics.Rect;
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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carbyer.ApiClient;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;
import com.example.carbyer.adapter.CarAdapter;
import com.example.carbyer.model.Car;
import com.example.carbyer.model.Dealer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarsFragment extends Fragment {

    private CarAdapter adapter;

    private EditText brandET;
    private EditText fromYearET;
    private EditText toYearET;
    private SwipeRefreshLayout swipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars, container, false);
        swipe = root.findViewById(R.id.swipeRefresh);
        RecyclerView rv = root.findViewById(R.id.carsRV);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        brandET = root.findViewById(R.id.filterBrandET);
        fromYearET = root.findViewById(R.id.filterFromYearET);
        toYearET = root.findViewById(R.id.filterToYearET);

        Button filterBtn = root.findViewById(R.id.filterApplyB);
        Button clearB = root.findViewById(R.id.filterClearB);

        filterBtn.setOnClickListener(v -> loadCars());

        clearB.setOnClickListener(v -> {
            brandET.setText("");
            fromYearET.setText("");
            toYearET.setText("");
            loadCars();
        });

        swipe.setOnRefreshListener(this::loadCars);

        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(
                    @NonNull Rect outRect,
                    @NonNull View view,
                    @NonNull RecyclerView parent,
                    @NonNull RecyclerView.State state) {

                if (parent.getChildAdapterPosition(view)
                        == state.getItemCount() - 1) {

                    outRect.bottom = 130;
                }
            }
        });

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
                        CarsFragment.this,
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
        rv.setAdapter(adapter);

        loadCars();

        return root;
    }

    private void loadCars() {

        String token = new SessionManager(requireContext()).getToken();

        Map<String, String> query = new HashMap<>();

        String brand = brandET.getText().toString().trim();
        String fromYear = fromYearET.getText().toString().trim();
        String toYear = toYearET.getText().toString().trim();

        if (!brand.isEmpty()) {
            query.put("carMake", brand);
        }

        if (!fromYear.isEmpty()) {
            query.put("fromYear", fromYear);
        }

        if (!toYear.isEmpty()) {
            query.put("toYear", toYear);
        }

        ApiClient.get("cars?", query, token, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                JSONArray carsJson = body.optJSONArray("cars");

                List<Car> cars = new ArrayList<>();

                for (int i = 0; i < carsJson.length(); i++) {

                    JSONObject c = carsJson.optJSONObject(i);

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

                    cars.add(new Car(
                            c.optInt("id"),
                            c.optString("brand"),
                            c.optString("model"),
                            c.optInt("productionYear"),
                            c.optString("imageURL"),
                            c.optInt("kilometers"),
                            c.optInt("price"),
                            dealer
                    ));
                }
                swipe.setRefreshing(false);
                adapter.setItems(cars);
            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("CARS", "Error: " + message);
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

                        loadCars();

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