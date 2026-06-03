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

import com.example.carbyer.R;
import com.example.carbyer.model.Car;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.VH>{

    public interface OnClick {
        void onCarClick(Car car);
        void onCarEdit(Car car);
        void onCarDelete(Car car);
    }

    private List<Car> items = new ArrayList<>();
    private OnClick onClick;

    public CarAdapter(OnClick onClick){
        this.onClick = onClick;
    }

    public void setItems(List<Car> list){
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Car car = items.get(position);

        holder.title.setText(car.brand + " " + car.model);
        holder.year.setText(String.valueOf(car.productionYear));
        holder.kilometers.setText(car.kilometers + "km");
        holder.price.setText(car.price + "€");
        if(car.dealer != null) {
            holder.dealer.setText("Sold by " + car.dealer.name + " " + car.dealer.city);
        }

        String imageURL = car.imageURL;

        if(imageURL == null){
            holder.image.setImageDrawable(null);
        }
        else {
            try {
                Glide.with(holder.itemView.getContext())
                        .load(imageURL)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.placeholder)
                        .into(holder.image);
            }catch (Exception e){
                Log.e("CARS", e.toString());
            }
        }

        holder.image.setOnClickListener(v -> onClick.onCarClick(car));
        holder.carInfoLayout.setOnClickListener(v -> onClick.onCarClick(car));

        holder.editB.setOnClickListener(v -> {
            onClick.onCarEdit(car);
        });
        holder.deleteB.setOnClickListener(v -> onClick.onCarDelete(car));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        TextView price;
        TextView kilometers;
        TextView dealer;
        TextView year;

        ImageButton editB;
        ImageButton deleteB;

        LinearLayout carInfoLayout;

        public VH(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView);
            carInfoLayout = itemView.findViewById(R.id.carInfoLayout);
            title = itemView.findViewById(R.id.nameTV);
            price = itemView.findViewById(R.id.priceTV);
            kilometers = itemView.findViewById(R.id.kilometersTV);
            dealer = itemView.findViewById(R.id.dealerNameTV);
            year = itemView.findViewById(R.id.yearTV);

            editB = itemView.findViewById(R.id.editB);
            deleteB = itemView.findViewById(R.id.deleteB);
        }
    }
}
