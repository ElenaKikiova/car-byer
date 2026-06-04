package com.example.carbyer.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbyer.ApiClient;
import com.example.carbyer.MainActivity;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;
import com.example.carbyer.adapter.CarAdapter;
import com.example.carbyer.model.Car;
import com.example.carbyer.model.Dealer;

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
                DeleteDialogFragment.newInstance("car", car.id)
                        .show(getParentFragmentManager(), "delete_dialog");

                getParentFragmentManager().setFragmentResultListener(
                        "delete_result",
                        CarsFragment.this,
                        (requestKey, result) -> {

                            boolean confirmed =
                                    result.getBoolean("confirmed");

                            if (confirmed) {
                                // call delete API
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


        ApiClient.get("cars", token, new ApiClient.Callback() {

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

                Log.d("CARS", "Loaded cars: ");

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

                        Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();

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