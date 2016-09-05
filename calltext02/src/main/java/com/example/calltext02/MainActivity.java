package com.example.calltext02;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.calltext02.db.MySqliteOpenHelper;
import com.example.calltext02.db.pojo.Contact;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Contact> list;
    private MyAdapter myAdapter;
    private MySqliteOpenHelper dbhelper;
    private final int REQUEST_NEW=100;
    private final int REQUEST_CONTACTINFO=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=new ArrayList<>();
        dbhelper=new MySqliteOpenHelper(this,"contact.db",null,1);
        //新建联系人按钮
        findViewById(R.id.btn_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newContact();
            }
        });
        ListView listview= (ListView) findViewById(R.id.listview);
        list=new ArrayList<>();
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "select * from contact";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.setId(cursor.getInt(0));
            contact.setName(cursor.getString(1));
            contact.setPhone(cursor.getString(2));
            list.add(contact);
        }
        db.close();
        myAdapter = new MyAdapter();
        listview.setAdapter(myAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra("name", list.get(position).getName());
                intent.putExtra("phone", list.get(position).getPhone());
                intent.putExtra("id", list.get(position).getId());
                startActivityForResult(intent, REQUEST_CONTACTINFO);
            }
        });


    }
//新建联系人方法
    private void newContact() {
        Intent intent=new Intent(this,ContactActivity.class);
//        请求码用于标识请求来源
        intent.putExtra("is_new",true);
        startActivityForResult(intent,REQUEST_NEW);
    }
//    在Activity中得到新打开Activity关闭后返回的数据，
//    你需要使用系统提供的startActivityForResult
//            (Intent intent,int requestCode)方法打开新的Activity
//            ，新的Activity关闭后会向前面的Activity传回数据，为了得到传回的数据，
//    你必须在前面的Activity中重写onActivityResult(
//            int requestCode, int resultCode,Intent data)方法
    //之前的Activity通过intent传值
//第一个参数为请求码，即调用startActivityForResult()传递过去的值
//第二个参数为结果码，结果码用于标识返回数据来自哪个新Activity
    //每一次有返回值就会刷新一次列表，用这个可以做到优化
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) {
        return;
    }

    if (requestCode == REQUEST_NEW) {
        boolean isRefresh = data.getBooleanExtra("is_refresh", false);
        if (isRefresh) {
            refresh();
        }
    } else if (requestCode == REQUEST_CONTACTINFO) {
        int id = data.getIntExtra("id", 0);
        if (data.getBooleanExtra("is_delete", false) ) {
            for (Contact c: list) {
                if (c.getId() == id) {
                    list.remove(c);
                    break;
                }
            }
        } else {
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            for (Contact c:
                    list) {
                if (c.getId() == id) {
                    c.setName(name);
                    c.setPhone(phone);
                    break;
                }
            }
        }
        myAdapter.notifyDataSetChanged();
    }

}

    private void refresh() {

        list.clear();
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "select * from contact";

        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.setId(cursor.getInt(0));
            contact.setName(cursor.getString(1));
            contact.setPhone(cursor.getString(2));
            list.add(contact);
        }
        db.close();
        myAdapter.notifyDataSetChanged();
    }

    //适配器
    private class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            //判断list是否值，没有值返回0，有值 返回list的size
            return list==null ? 0:list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            //根据id找到对应的list
            return list.get(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder=null;
            if(view==null){
                view=getLayoutInflater().inflate(R.layout.list_item,null);
                viewHolder=new ViewHolder();
                viewHolder.textname= (TextView) view.findViewById(R.id.contact_name);
                viewHolder.textphone= (TextView) view.findViewById(R.id.contact_phone);
                view.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) view.getTag();
            }
            Contact contact= (Contact) getItem(i);
            viewHolder.textname.setText(contact.getName());
            viewHolder.textphone.setText(contact.getPhone());
            return view;
        }


        private class ViewHolder{
            TextView textname,textphone;
        }
    }
}

