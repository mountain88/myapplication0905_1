package com.example.calltext02;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.calltext02.db.MySqliteOpenHelper;

/**
 * Created by dell on 2016-09-01.
 */
public class ContactActivity extends AppCompatActivity {
    private EditText edtname,edtphone;
    private MySqliteOpenHelper dbhelper;
    private boolean isNew=false;
    private int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        dbhelper=new MySqliteOpenHelper(this,"contact.db",null,1);
        edtname= (EditText) findViewById(R.id.edt_name);
        edtphone= (EditText) findViewById(R.id.edt_phone);
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNew) {
                    save();
                } else {
                    update();
                }
            }
        });
        Intent intent = getIntent();
        View view = findViewById(R.id.btn_delete);

        isNew = intent.getBooleanExtra("is_new", false);

        if (!isNew) {
            String name = intent.getStringExtra("name");
            String phone = intent.getStringExtra("phone");
            id = intent.getIntExtra("id", 0);
            edtname.setText(name);
            edtphone.setText(phone);
            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    delete();
                }
            });
        } else {
            view.setVisibility(View.GONE);
        }

    }

    private void update() {

        String name = edtname.getText().toString();
        String phone = edtphone.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.name_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.phone_not_null, Toast.LENGTH_SHORT).show();
            return;
        }

        String sql = "update contact set name = '" + name +"', phone = '" + phone + "' where id = " + id;
        Log.d("sql", sql);

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL(sql);
        db.close();

        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        setResult(RESULT_OK, intent);


    }

    private void delete() {
        String sql = "delete from contact where id = " + id;
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL(sql);
        db.close();

        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("is_delete", true);
        setResult(RESULT_OK, intent);

    }

    private void save() {
        String name = edtname.getText().toString();
        String phone = edtphone.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.name_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.phone_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        String sql = "insert into contact(name, phone) values('" + name + "', '" + phone + "')";
        db.execSQL(sql);
        db.close();
        Intent intent = new Intent();
        intent.putExtra("is_refresh", true);
        setResult(RESULT_OK, intent);
    }
}
