package com.example.carbyer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbyer.R;
import com.example.carbyer.model.Dealer;

import java.util.ArrayList;
import java.util.List;

public class DealerAdapter extends RecyclerView.Adapter<DealerAdapter.VH>{

    public interface OnClick {
        void onDealerClick(Dealer dealer);
        void onDealerEdit(Dealer dealer);
        void onDealerDelete(Dealer dealer);
    }

    private List<Dealer> items = new ArrayList<>();
    private OnClick onClick;

    public DealerAdapter(OnClick onClick){
        this.onClick = onClick;
    }

    public void setItems(List<Dealer> list){
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dealer, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Dealer dealer = items.get(position);

        holder.title.setText(dealer.name);
        holder.address.setText(dealer.address);
        holder.city.setText(dealer.city);
        holder.workingHours.setText(dealer.workingHours);

        holder.dealerInfoLayout.setOnClickListener(
                v -> onClick.onDealerClick(dealer));

        holder.editB.setOnClickListener(
                v -> onClick.onDealerEdit(dealer));

        holder.deleteB.setOnClickListener(
                v -> onClick.onDealerDelete(dealer));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView title;
        TextView address;
        TextView city;
        TextView workingHours;

        ImageButton editB;
        ImageButton deleteB;

        LinearLayout dealerInfoLayout;

        public VH(@NonNull View itemView) {
            super(itemView);

            dealerInfoLayout = itemView.findViewById(R.id.dealerInfoLayout);

            title = itemView.findViewById(R.id.nameTV);
            address = itemView.findViewById(R.id.addressTV);
            city = itemView.findViewById(R.id.cityTV);
            workingHours = itemView.findViewById(R.id.workingHoursTV);

            editB = itemView.findViewById(R.id.editB);
            deleteB = itemView.findViewById(R.id.deleteB);
        }
    }
}
