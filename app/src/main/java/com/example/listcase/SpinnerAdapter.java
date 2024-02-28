package com.example.listcase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<CategoryProperty> implements AdapterView.OnItemClickListener {
    private Context context;
    private ArrayList<CategoryProperty> items;


    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CategoryProperty> objects) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    private View createCustomView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        TextView textView = view.findViewById(R.id.tvTitleSpinner);
        ImageView imageView = view.findViewById(R.id.imgSpinner);
        textView.setText(items.get(position).getTitle());
        if (items.get(position).getBlob() != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(items.get(position).getBlob(), 0, items.get(position).getBlob().length);
            imageView.setImageBitmap(bitmap);
        }
        else imageView.setImageResource(R.drawable.icon_category);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

