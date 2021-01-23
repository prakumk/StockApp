package com.pradeep.stockapp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.pradeep.stockapp.R;
import com.pradeep.stockapp.domain.Name;

import java.util.ArrayList;
import java.util.List;

public class AnimalsAdapter extends RecyclerView.Adapter<AnimalsAdapter.AnimalsViewHolder> implements Filterable {

    private Context context;
    private List<Name> nameList;
    private List<Name> filteredNameList;

    public AnimalsAdapter(Context context, List<Name> nameList) {
        super();
        this.context = context;
        this.nameList = nameList;
        this.filteredNameList = nameList;
    }

    @NonNull
    @Override
    public AnimalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new AnimalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalsViewHolder holder, int position) {
        holder.tvName.setText(filteredNameList.get(position).getName());
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
                    List<Name> filteredList = new ArrayList<>();
                    for (Name name : nameList) {
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
                filteredNameList = (List<Name>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class AnimalsViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvName;

        public AnimalsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
