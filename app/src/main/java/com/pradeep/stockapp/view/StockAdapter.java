package com.pradeep.stockapp.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.pradeep.stockapp.R;
import com.pradeep.stockapp.room_db.StockModel;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.stockViewHolder> implements Filterable {

    private Context context;
    private List<StockModel> nameList;
    private List<StockModel> filteredNameList;

    public StockAdapter(Context context, List<StockModel> nameList) {
        super();
        this.context = context;
        this.nameList = nameList;
        this.filteredNameList = nameList;
    }

    @NonNull
    @Override
    public stockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new stockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull stockViewHolder holder, int position) {
        holder.tvName.setText(filteredNameList.get(position).getName());
        holder.type.setText(filteredNameList.get(position).getType());
        holder.price.setText(String.valueOf(filteredNameList.get(position).getCurr_rate()));
        if (filteredNameList.get(position).getCurr_rate()>filteredNameList.get(position).getRate()){
            holder.price.setTextColor(Color.parseColor("#73BD73"));
        }
        else if(filteredNameList.get(position).getCurr_rate()==filteredNameList.get(position).getRate()){
            holder.price.setTextColor(Color.parseColor("#F7D438"));
        }
        else
        {
            holder.price.setTextColor(Color.parseColor("#E35E59"));
        }
        double diff = filteredNameList.get(position).getCurr_rate()-filteredNameList.get(position).getRate();
        double per = 0;
        try {
            per = diff * 100 / filteredNameList.get(position).getRate();
        }
        catch (Exception e){

        }
        if(diff>=0){
            String text = "+"+String.format("%.2f", diff)+" ("+String.format("%.2f", per)+"%)";
            holder.diff.setText(text);
        }
        else
        {
            String text = ""+String.format("%.2f", diff)+" ("+String.format("%.2f", per)+"%)";
            holder.diff.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return filteredNameList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredNameList = nameList;
                } else {
                    List<StockModel> filteredList = new ArrayList<>();
                    for (StockModel name : nameList) {
                        if (name.getName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(name);
                        }
                        filteredNameList = filteredList;
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredNameList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNameList = (List<StockModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class stockViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvName;
        private AppCompatTextView price;
        private AppCompatTextView type;
        private AppCompatTextView diff;



        public stockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            type = itemView.findViewById(R.id.type);
            diff = itemView.findViewById(R.id.diff);

        }
    }
}
