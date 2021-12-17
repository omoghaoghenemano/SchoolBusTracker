package com.example.gpstracking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gpstracking.Model.Routes.Route;
import com.example.gpstracking.R;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Route> {

    public ListAdapter(Context context, ArrayList<Route> routeArrayList) {
        super(context, R.layout.list_item, routeArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

      return convertView;
    }
}
