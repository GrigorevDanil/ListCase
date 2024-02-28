package com.example.listcase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ContextActivityTask extends AppCompatActivity {
    Spinner spinnerCategory;
    SpinnerAdapter adapterCategory;
    ArrayList<CategoryProperty>  arrCategoryName;

    String[] tempTask;
    boolean flagEdit = false;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor cursor;

    EditText editTextTitle, editTextDescription;
    Button butDate;
    TextView tvDate, tvTitle;
    CheckBox checkBoxDeadLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_task);

        //Setting spinner
        spinnerCategory = findViewById(R.id.spinnerCategory);
        arrCategoryName = new ArrayList<>();
        adapterCategory = new SpinnerAdapter(this, R.layout.spinner_item, arrCategoryName);
        spinnerCategory.setAdapter(adapterCategory);

        //Setting DB
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.open();

        //Setting item layout
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        butDate = findViewById(R.id.butDate);
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);
        checkBoxDeadLine = findViewById(R.id.checkBoxDate);
        butDate.setText(MainActivity.formatter.format(new Date()));
        tvDate.setText(MainActivity.formatter.format(new Date()));
        UpdateListCategory();

        if (!getIntent().getExtras().get("task").equals(""))
        {
            tvTitle.setText("Изменение задачи");
            tempTask = (String[]) getIntent().getExtras().get("task");
            try {
                int pos = CategoryProperty.getPostionItem(arrCategoryName,Integer.parseInt(tempTask[1]) );
                spinnerCategory.setSelection(adapterCategory.getPosition(arrCategoryName.get(pos)));
                editTextTitle.setText(tempTask[2]);
                editTextDescription.setText(tempTask[3]);
                tvDate.setText(tempTask[4]);
                if (!tempTask[5].equals("")) butDate.setText(tempTask[5]);
                flagEdit = true;
            }
            catch (Exception ex)
            {
                Toast.makeText(this,"Произошла ошибка " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }

    }


    public void butCancel_Click(View view) {
        this.finish();
    }

    public void butEnter_Click(View view) {
        InsertUpdateTask();

    }

    void UpdateListCategory()
    {
        arrCategoryName.clear();
        arrCategoryName.add(new CategoryProperty("Отсутвует"));
        db = databaseHelper.open();
        cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_CATEGORY,null);
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


    void InsertUpdateTask()
    {
        if (editTextTitle.getText().toString().equals(""))
        {
            Toast.makeText(this, "Необходимые поля не заполнены!", Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues cv = new ContentValues();
        if (spinnerCategory.getSelectedItemPosition() != 0) cv.put(DatabaseHelper.COLUMN_ID_CATEGORY_CATEGORY, arrCategoryName.get(spinnerCategory.getSelectedItemPosition()).getId());
        else cv.put(DatabaseHelper.COLUMN_ID_CATEGORY_CATEGORY, (Integer) null);
        cv.put(DatabaseHelper.COLUMN_TITLE_TASK, editTextTitle.getText().toString());
        if (!editTextDescription.getText().toString().equals("")) cv.put(DatabaseHelper.COLUMN_DESCRIPTION_TASK, editTextDescription.getText().toString());
        else cv.put(DatabaseHelper.COLUMN_DESCRIPTION_TASK, (String) null);
        cv.put(DatabaseHelper.COLUMN_DATE_CREATE_TASK, tvDate.getText().toString());
        if (checkBoxDeadLine.isChecked()) cv.put(DatabaseHelper.COLUMN_DATE_DEAD_LINE_TASK, butDate.getText().toString());
        else cv.put(DatabaseHelper.COLUMN_DATE_DEAD_LINE_TASK, (String) null);
        cv.put(DatabaseHelper.COLUMN_COMPLETE_TASK,0);
        if (flagEdit) db.update(DatabaseHelper.TABLE_TASK, cv, DatabaseHelper.COLUMN_ID_TASK + "=? ", new String[] {tempTask[0]});
        else db.insert(DatabaseHelper.TABLE_TASK,null,cv);
        this.finish();
    }

    public void butDate_Click(
            View v) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    butDate.setText( MainActivity.formatter.format(calendar.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}