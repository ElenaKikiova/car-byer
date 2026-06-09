package com.example.carbyer.ui.add;

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
import androidx.navigation.fragment.NavHostFragment;

import com.example.carbyer.ApiClient;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;
import com.example.carbyer.model.Dealer;

import org.json.JSONArray;
import org.json.JSONObject;


public class DealersCreateUpdateFragment extends Fragment {

    private int dealerId;

    private EditText nameET;
    private EditText addressET;
    private EditText cityET;
    private EditText workingHoursET;

    private String mode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dealers_create_update, container, false);

        nameET = root.findViewById(R.id.nameET);
        addressET = root.findViewById(R.id.addressET);
        cityET = root.findViewById(R.id.cityET);
        workingHoursET = root.findViewById(R.id.workingHoursET);

        Button saveB = root.findViewById(R.id.saveB);
        saveB.setOnClickListener(v -> saveDealer());

        if (getArguments() != null) {
            dealerId = getArguments().getInt("dealerId", -1);
            mode = getArguments().getString("mode", "edit");

            if(dealerId != -1){
                loadDealerData();
            }
        } else {
            mode = "create";
            dealerId = -1;
        }

        return root;
    }

    private void loadDealerData() {

        String token = new SessionManager(requireContext()).getToken();


        ApiClient.get("dealers/" + dealerId, token, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                JSONArray dealerJson = body.optJSONArray("dealer");
                JSONObject d = dealerJson.optJSONObject(0);

                Dealer dealer = new Dealer(
                        d.optInt("id"),
                        d.optString("name"),
                        d.optString("address"),
                        d.optString("city"),
                        d.optString("workingHours")
                );

                nameET.setText(dealer.name);
                addressET.setText(dealer.address);
                cityET.setText(dealer.city);
                workingHoursET.setText(dealer.workingHours);

                Log.d("DEALERS", "Loaded dealer: " + dealer);

            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("DEALERS", "Error: " + message);
            }
        });

    }

    private void saveDealer() {
        JSONObject body = new JSONObject();
        String token = new SessionManager(requireContext()).getToken();

        try {
            body.put("name", nameET.getText().toString());
            body.put("address", addressET.getText().toString());
            body.put("city", cityET.getText().toString());
            body.put("workingHours", workingHoursET.getText().toString());
        }
        catch (Exception e) {
            Toast.makeText(
                    requireContext(),
                    R.string.invalid_data,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        ApiClient.Callback callback = new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject body) {

                String message;

                if ("create".equals(mode)) {
                    message = getString(R.string.dealer_created_successfully);
                } else {
                    message = getString(R.string.dealer_updated_successfully);
                }

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(DealersCreateUpdateFragment.this).navigate(R.id.nav_dealers);

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

        if(dealerId == -1) {
            ApiClient.post(
                    "dealers/",
                    body,
                    token,
                    callback
            );
        }
        else {
            ApiClient.put(
                    "dealers/" + dealerId,
                    body,
                    token,
                    callback
            );
        }
    }


}