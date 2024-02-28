package com.example.listcase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskPropertyAdapter extends RecyclerView.Adapter<TaskPropertyAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<TaskProperty> tasks;

    private OnItemLongClickListener longClickListener;





    TaskPropertyAdapter(Context context, List<TaskProperty> tasks, OnItemLongClickListener longClickListener) {
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
        this.longClickListener = longClickListener;

    }

    @Override
    public TaskPropertyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_list_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskPropertyAdapter.ViewHolder holder, int position) {
        TaskProperty task = tasks.get(position);

        holder.checkBoxComplete.setChecked(task.isComplete());
        if (task.isComplete()) holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        holder.checkBoxComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_ID_CATEGORY_CATEGORY, task.getCategory());
                cv.put(DatabaseHelper.COLUMN_TITLE_TASK, task.getTitle());
                cv.put(DatabaseHelper.COLUMN_DESCRIPTION_TASK, task.getDescription());
                cv.put(DatabaseHelper.COLUMN_DATE_CREATE_TASK, MainActivity.formatter.format(task.getDate_create()));
                if (task.getDate_deadline() != null) cv.put(DatabaseHelper.COLUMN_DATE_DEAD_LINE_TASK, MainActivity.formatter.format(task.getDate_deadline()));
                else cv.put(DatabaseHelper.COLUMN_DATE_DEAD_LINE_TASK, (String) null);
                if (task.isComplete())
                {
                    cv.put(DatabaseHelper.COLUMN_COMPLETE_TASK, 0);
                    task.setComplete(false);
                }
                else
                {
                    cv.put(DatabaseHelper.COLUMN_COMPLETE_TASK, 1);
                    task.setComplete(true);
                }


                MainActivity.db.update(DatabaseHelper.TABLE_TASK, cv, DatabaseHelper.COLUMN_ID_TASK + " = " + task.getId(), null);

                if (task.isComplete())
                {
                    holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            }
        });



        holder.txtTitle.setText(task.getTitle());

        holder.txtDateCreate.setText(MainActivity.formatter.format(task.getDate_create()));
        if (task.getDate_deadline() != null) holder.txtDateDeadLine.setText(MainActivity.formatter.format(task.getDate_deadline()));
        else holder.txtDateDeadLine.setVisibility(View.INVISIBLE);

        if (task.getCategory() != 0)
        {
            int pos = CategoryProperty.getPostionItem(MainActivity.arrCategoryName,task.getCategory());
            holder.txtCategory.setText(MainActivity.arrCategoryName.get(pos).getTitle());
        }
        else holder.txtCategory.setText("Отсутвует");

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements ForegroundViewHolder {
        final LinearLayout background, foreground;
        final CheckBox checkBoxComplete;
        final TextView txtTitle, txtDateDeadLine, txtDateCreate, txtCategory;

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

            checkBoxComplete = itemView.findViewById(R.id.checkboxComplete);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDateDeadLine = itemView.findViewById(R.id.txtDeadLine);
            txtDateCreate = itemView.findViewById(R.id.txtDateCreate);
            txtCategory = itemView.findViewById(R.id.txtCategoryName);
            background  = itemView.findViewById(R.id.background);
            foreground  = itemView.findViewById(R.id.foreground);
        }

        @Override
        public View getForegroundView() {
            return foreground;
        }
    }


}

