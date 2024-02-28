package com.example.listcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, OnItemLongClickListener {
    public static SimpleDateFormat formatter;
    RecyclerView listTasks;
    ArrayList<TaskProperty> arrTasks = new ArrayList<>();
    TaskPropertyAdapter adapter;

    Spinner spinnerCategory;
    SpinnerAdapter adapterCategory;
    public static ArrayList<CategoryProperty> arrCategoryName;

    String comSelect;


    DatabaseHelper databaseHelper;
    public static SQLiteDatabase db;
    Cursor cursor;


    Animation animation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Setting
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_but_add);
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        //Setting spinner
        spinnerCategory = findViewById(R.id.spinnerCategory);
        arrCategoryName = new ArrayList<>();
        arrCategoryName.add(new CategoryProperty("Все"));
        adapterCategory = new SpinnerAdapter(this, R.layout.spinner_item, arrCategoryName);

        spinnerCategory.setAdapter(adapterCategory);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) comSelect = "select * from " + DatabaseHelper.TABLE_TASK;
                else
                {
                    comSelect = "select * from " + DatabaseHelper.TABLE_TASK + " where Id_Category = " + arrCategoryName.get(position).getId();
                }
                UpdateListTasks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //Setting DB
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();

        //Setting list
        listTasks = findViewById(R.id.listTasks);
        listTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskPropertyAdapter(this, arrTasks,this);
        listTasks.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, TaskPropertyAdapter.ViewHolder.class,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listTasks);
        comSelect = "select * from " + DatabaseHelper.TABLE_TASK;


    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(MainActivity.this, ContextActivityTask.class);
        String[] tempTask = new  String[6];
        tempTask[0] = String.valueOf(arrTasks.get(position).getId());
        tempTask[1] = String.valueOf(arrTasks.get(position).getCategory());
        tempTask[2] = arrTasks.get(position).getTitle();
        tempTask[3] = arrTasks.get(position).getDescription();
        tempTask[4] = formatter.format(arrTasks.get(position).getDate_create());
        if (arrTasks.get(position).getDate_deadline() != null) tempTask[5] = formatter.format(arrTasks.get(position).getDate_deadline());
        else tempTask[5] = "";

        intent.putExtra("task", tempTask);
        startActivity(intent);
        UpdateListTasks();
    }


    @Override
    protected void onResume() {
        super.onResume();
        UpdateListTasks();
        UpdateListCategory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        DeleteTask(arrTasks.get(position).getId());

    }



    public void butAddTask_Click(View view) {
        view.startAnimation(animation);
        Intent intent = new Intent(this, ContextActivityTask.class);
        intent.putExtra("task", "");
        startActivity(intent);
    }

    void UpdateListCategory()
    {
        arrCategoryName.clear();
        arrCategoryName.add(new CategoryProperty("Все"));
        db = databaseHelper.open();
        cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_CATEGORY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    CategoryProperty category = new CategoryProperty();
                    category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_CATEGORY_TASK)));
                    category.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE_CATEGORY)));
                    category.setBlob(cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOB_CATEGORY)));
                    arrCategoryName.add(category);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception ex)
        {
            cursor.close();
        }
        cursor.close();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterCategory.notifyDataSetChanged();
            }
        });

    }


    void UpdateListTasks()
    {
        arrTasks.clear();
        db = databaseHelper.open();
        cursor = db.rawQuery(comSelect, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    TaskProperty task = new TaskProperty();
                    task.setComplete(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMPLETE_TASK)) == 1);
                    task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE_TASK)));
                    task.setDate_create(formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_CREATE_TASK))));

                    int pos = (cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_DEAD_LINE_TASK));
                    if (!cursor.isNull(pos)) task.setDate_deadline(formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_DEAD_LINE_TASK))));
                    task.setCategory(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_CATEGORY_TASK)));
                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TASK)));
                    task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION_TASK)));
                    arrTasks.add(task);

                } while (cursor.moveToNext());
            }
        }
        catch (Exception ex)
        {

            cursor.close();
        }
        cursor.close();

        databaseHelper.close();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });



    }

    void DeleteTask(int idTask)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder
                .setTitle("Удаление элемента")
                .setMessage("Вы уверенны что хотите удалить задачу?")
                .setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateListTasks();
                    }
                })
                .setNegativeButton("Подтвердить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(DatabaseHelper.TABLE_TASK,"Id_Task = ?", new String[]{String.valueOf(idTask)});
                        UpdateListTasks();
                    }
                })
                .create()
                .show();


    }

    public void butAddCategory_Click(View view) {
        view.startAnimation(animation);
        Intent intent = new Intent(this, ContextActivityCategory.class);
        startActivity(intent);
        UpdateListTasks();
    }



}