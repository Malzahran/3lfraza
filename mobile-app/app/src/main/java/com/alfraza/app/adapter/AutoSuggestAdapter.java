package com.alfraza.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.models.PlacesSearch;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AutoSuggestAdapter extends ArrayAdapter<PlacesSearch> implements Filterable {
    private List<PlacesSearch> mlistData;
    private int viewResourceId;

    public AutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mlistData = new ArrayList<>();
        this.viewResourceId = resource;
    }

    public void setData(List<PlacesSearch> list) {
        mlistData.clear();
        mlistData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlistData.size();
    }

    @Nullable
    @Override
    public PlacesSearch getItem(int position) {
        return mlistData.get(position);
    }

    public PlacesSearch getObject(int position) {
        return mlistData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mlistData;
                    filterResults.count = mlistData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }


    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        PlacesSearch place = mlistData.get(position);
        if (place != null) {
            TextView placeLabel = v.findViewById(R.id.title);
            if (placeLabel != null) {
                placeLabel.setText(place.name);
            }
            TextView placeDesc = v.findViewById(R.id.desc);
            if (placeDesc != null) {
                placeDesc.setText(place.address);
            }
        }
        return v;
    }
}