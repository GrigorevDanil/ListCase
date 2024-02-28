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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class ContextActivityCategory extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, OnItemLongClickListener{

    RecyclerView listCategory;
    ArrayList<CategoryProperty> arrCategory = new ArrayList<>();
    CategoryPropertyAdapter adapter;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_category);
        //Setting list
        listCategory = findViewById(R.id.listCategories);
        listCategory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryPropertyAdapter(this, arrCategory,this);
        listCategory.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper<CategoryPropertyAdapter.ViewHolder>(0, ItemTouchHelper.LEFT, CategoryPropertyAdapter.ViewHolder.class,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listCategory);

        //Setting DB
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();
    }

    public void butCancel_Click(View view) {
        this.finish();
    }

    void UpdateListCategory()
    {
        arrCategory.clear();
        db = databaseHelper.open();
        cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_CATEGORY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    CategoryProperty category = new CategoryProperty();
                    category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_CATEGORY_TASK)));
                    category.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE_CATEGORY)));
                    int pos = (cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOB_CATEGORY));
                    if (!cursor.isNull(pos)) category.setBlob(cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOB_CATEGORY)));
                    arrCategory.add(category);
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
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateListCategory();
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(this, ContextActivityCategoryMenu.class);
        String[] tempCategory = new  String[6];
        tempCategory[0] = String.valueOf(arrCategory.get(position).getId());
        tempCategory[1] = String.valueOf(arrCategory.get(position).getTitle());

        intent.putExtra("category", tempCategory);

        byte[] blob = null;

        if (arrCategory.get(position).getBlob() != null) blob = arrCategory.get(position).getBlob();

        intent.putExtra("blob", blob);

        startActivity(intent);
        UpdateListCategory();
    }



    public void butAddCategory_Click(View view) {
        Intent intent = new Intent(this, ContextActivityCategoryMenu.class);
        intent.putExtra("category", "");
        intent.putExtra("blob", (String)null);
        startActivity(intent);
        UpdateListCategory();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        DeleteTask(arrCategory.get(position).getId());
    }

    void DeleteTask(int idCategory)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder
                .setTitle("Удаление элемента")
                .setMessage("Вы уверенны что хотите удалить задачу?")
                .setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateListCategory();
                    }
                })
                .setNegativeButton("Подтвердить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(DatabaseHelper.TABLE_CATEGORY,"Id_Category = ?", new String[]{String.valueOf(idCategory)});
                        UpdateListCategory();
                    }
                })
                .create()
                .show();


    }
}