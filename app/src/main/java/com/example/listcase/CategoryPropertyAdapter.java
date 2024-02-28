package com.example.listcase;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryPropertyAdapter extends RecyclerView.Adapter<CategoryPropertyAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<CategoryProperty> categories;

    private OnItemLongClickListener longClickListener;

    public CategoryPropertyAdapter(Context context, List<CategoryProperty> categories, OnItemLongClickListener longClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.categories = categories;
        this.longClickListener = longClickListener;
    }

    @Override
    public CategoryPropertyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_list_category, parent, false);
        return new CategoryPropertyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryPropertyAdapter.ViewHolder holder, int position) {
        CategoryProperty category = categories.get(position);

        if (category.getBlob() != null) holder.imageCategory.setImageBitmap(BitmapFactory.decodeByteArray(category.getBlob(), 0, category.getBlob().length));
        else holder.imageCategory.setImageResource(R.drawable.icon_category);
        holder.txtCategory.setText(category.getTitle());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ForegroundViewHolder{
        final LinearLayout background, foreground;
        final ImageView imageCategory;
        final TextView txtCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            longClickListener.onItemLongClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });

            imageCategory = itemView.findViewById(R.id.imageCategory);
            txtCategory = itemView.findViewById(R.id.txtTitle);
            background  = itemView.findViewById(R.id.background);
            foreground  = itemView.findViewById(R.id.foreground);
        }

        @Override
        public View getForegroundView() {
            return foreground;
        }
    }
}

