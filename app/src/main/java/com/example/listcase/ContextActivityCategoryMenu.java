package com.example.listcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ContextActivityCategoryMenu extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor cursor;

    String[] tempCategory;
    byte[] tempBlob;
    boolean flagEdit = false;

    ImageView imageView;
    EditText editTextTitle;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_category_menu);

        //Setting DB
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.open();

        //Setting item layout
        imageView = findViewById(R.id.imageView);
        editTextTitle = findViewById(R.id.editTextTitle);
        tvTitle = findViewById(R.id.tvTitle);

        if (!getIntent().getExtras().get("category").equals(""))
        {
            tempCategory = (String[]) getIntent().getExtras().get("category");
            tvTitle.setText("Изменение задачи");
            tempCategory = (String[]) getIntent().getExtras().get("category");
            try {
                editTextTitle.setText(tempCategory[1]);
                flagEdit = true;
                if (!getIntent().getExtras().get("blob").equals(null))
                {
                    tempBlob = (byte[]) getIntent().getExtras().get("blob");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(tempBlob, 0, tempBlob.length);
                    imageView.setBackground(null);
                    imageView.setImageBitmap(bitmap);
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(this,"Произошла ошибка " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }


    }



    public void butCancel_Click(View view) { this.finish();
    }



    void InsertUpdateCategory()
    {
        if (editTextTitle.getText().toString().equals(""))
        {
            Toast.makeText(this, "Необходимые поля не заполнены!", Toast.LENGTH_LONG).show();
            return;
        }


        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_TITLE_TASK, editTextTitle.getText().toString());
        if (imageView.getDrawable() != null)
        {
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] blob = byteArrayOutputStream.toByteArray();
            cv.put(DatabaseHelper.COLUMN_BLOB_CATEGORY, blob );
        }
        else cv.put(DatabaseHelper.COLUMN_BLOB_CATEGORY,(String) null );
        if (flagEdit) db.update(DatabaseHelper.TABLE_CATEGORY, cv, DatabaseHelper.COLUMN_ID_CATEGORY_CATEGORY + "=? ", new String[] {tempCategory[0]});
        else db.insert(DatabaseHelper.TABLE_CATEGORY,null,cv);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 512, 512, true);
                imageView.setBackground(null);
                imageView.setImageBitmap(resizedBitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void imageView_Click(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
    }


    public void butEnter_Click(View view) {
        InsertUpdateCategory();
        this.finish();
    }


}