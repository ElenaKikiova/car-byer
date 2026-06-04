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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbyer.ApiClient;
import com.example.carbyer.R;
import com.example.carbyer.SessionManager;
import com.example.carbyer.adapter.DealerAdapter;
import com.example.carbyer.model.Dealer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DealersFragment extends Fragment {

    private DealerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dealers, container, false);

        RecyclerView rv = root.findViewById(R.id.dealersRV);
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

        adapter = new DealerAdapter(new DealerAdapter.OnClick() {
            @Override
            public void onDealerClick(Dealer Dealer) {
                Bundle args = new Bundle();
                args.putInt("dealerId", Dealer.id);

//                Navigation.findNavController(requireView()).navigate(R.id.nav_dealer_view, args);
            }

            @Override
            public void onDealerEdit(Dealer Dealer) {
                Bundle args = new Bundle();
                args.putInt("dealerId", Dealer.id);
                Navigation.findNavController(requireView()).navigate(R.id.nav_dealer_create_update, args);
            }

            @Override
            public void onDealerDelete(Dealer Dealer) {
                DeleteDialogFragment.newInstance("Dealer", Dealer.id)
                        .show(getParentFragmentManager(), "delete_dialog");

                getParentFragmentManager().setFragmentResultListener(
                        "delete_result",
                        DealersFragment.this,
                        (requestKey, result) -> {

                            boolean confirmed =
                                    result.getBoolean("confirmed");

                            if (confirmed) {
                                // call delete API
                                deleteDealer(Dealer.id);
                            }
                        }
                );
            }
        });
        rv.setAdapter(adapter);

        loadDealers();

        return root;
    }

    private void loadDealers() {

        String token = new SessionManager(requireContext()).getToken();


        ApiClient.get("dealers", token, new ApiClient.Callback() {

            @Override
            public void onSuccess(JSONObject body) {

                JSONArray dealersJson = body.optJSONArray("dealers");

                List<Dealer> dealers = new ArrayList<>();

                for (int i = 0; i < dealersJson.length(); i++) {

                    JSONObject c = dealersJson.optJSONObject(i);

                    JSONArray dealerArr = c.optJSONArray("dealer");

                    Dealer dealer = null;

                    dealers.add(new Dealer(
                            c.optInt("id"),
                            c.optString("name"),
                            c.optString("address"),
                            c.optString("city"),
                            c.optString("workingHours")
                    ));
                }

                Log.d("DEALERS", "Loaded dealers: ");

                adapter.setItems(dealers);
            }

            @Override
            public void onError(int httpCode, String message) {
                Log.e("DEALERS", "Error: " + message);
            }
        });

    }

    public void deleteDealer(int dealerId){

        String token = new SessionManager(requireContext()).getToken();

        ApiClient.delete(
                "dealers/" + dealerId,
                token,
                new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject body) {

                        Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();

                        loadDealers();

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