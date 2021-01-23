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
import com.pradeep.stockapp.custom_components.RoomItemClickListner;
import com.pradeep.stockapp.retrofit_api.RetroStockModel;

import java.util.ArrayList;
import java.util.List;

public class RetroStockAdapter extends RecyclerView.Adapter<RetroStockAdapter.stockViewHolder> implements Filterable {

    private Context context;
    private List<RetroStockModel> nameList;
    private List<RetroStockModel> filteredNameList;
    private RoomItemClickListner roomItemClickListner;

    public RetroStockAdapter(Context context, List<RetroStockModel> nameList, RoomItemClickListner roomItemClickListner) {
        super();
        this.context = context;
        this.nameList = nameList;
        this.filteredNameList = nameList;
        this.roomItemClickListner = roomItemClickListner;
    }

    @NonNull
    @Override
    public stockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.retro_list_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomItemClickListner.onItemClick((String)view.getTag());
            }
        });
        return new stockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull stockViewHolder holder, int position) {
        holder.tvName.setText(filteredNameList.get(position).getLongname());
        holder.type.setText(filteredNameList.get(position).getExchange());
        holder.view.setTag(filteredNameList.get(position).getSymbol());
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
                    List<RetroStockModel> filteredList = new ArrayList<>();
                    for (RetroStockModel name : nameList) {
                        if (name.getLongname().toLowerCase().contains(charSequenceString.toLowerCase())) {
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
                filteredNameList = (List<RetroStockModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class stockViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvName;
        private AppCompatTextView type;
        private View view;



        public stockViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvName = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);

        }
    }
}
