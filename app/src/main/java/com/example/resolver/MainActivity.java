package com.example.resolver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnQuery, btnDelete, btnInsert, btnUpdate, btnSecondPart, btnUseFirstURI, btnUseSecondURI;
    private TextView mtv;

    private static final String TABLE_FIRST_NAME = "first";
    private static final String TABLE_SECOND_NAME = "second";
    public static final String AUTHORITY = "com.example.provider";
    public static final Uri CONTENT_URI_FIRST = Uri.parse("content://" + AUTHORITY + File.separator + TABLE_FIRST_NAME);
    public static final Uri CONTENT_URI_SECOND = Uri.parse("content://" + AUTHORITY + File.separator + TABLE_SECOND_NAME);
    public Uri mCurrentURI = getUri(TABLE_FIRST_NAME);

    private DataBaseObserver mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDelete = (Button) findViewById(R.id.delete);
        btnInsert = (Button) findViewById(R.id.insert);
        btnQuery = (Button) findViewById(R.id.query);
        btnUpdate = (Button) findViewById(R.id.update);
        btnSecondPart = (Button) findViewById(R.id.secondPart);
        btnUseFirstURI = (Button) findViewById(R.id.first_uri);
        btnUseSecondURI = (Button) findViewById(R.id.second_uri);
        mtv = (TextView) findViewById(R.id.tv);
        mtv.setText("当前URI:" + mCurrentURI.toString());
        btnDelete.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnSecondPart.setOnClickListener(this);
        btnUseFirstURI.setOnClickListener(this);
        btnUseSecondURI.setOnClickListener(this);

        mObserver = new DataBaseObserver(new Handler());
        getContentResolver().registerContentObserver(getUri(null), true, mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
    }

    public class DataBaseObserver extends ContentObserver {

        public DataBaseObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.e("MainActivity", "selfChange = " + selfChange);
        }
    }

    private Uri getUri(String path) {
        return new Uri.Builder().authority(AUTHORITY)
                .path(path)
                .scheme("content")
                .build();
    }

    private void secondPart() {
        Intent intent = new Intent();
        intent.setAction("com.example.second");
        intent.setData(mCurrentURI);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void insert() {
        ContentValues values = new ContentValues();
        values.put("name", "hello");
        values.put("detail", "my name is demo");
        Uri uri = this.getContentResolver().insert(mCurrentURI, values);
        Log.e("MainActivity", "insert : " + uri.toString());
    }

    private void query() {
        Cursor cursor = this.getContentResolver().query(mCurrentURI, null, null, null, null);
        Log.e("MainActivity", "query : count = " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String table = cursor.getString(cursor.getColumnIndex("table_name"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String detail = cursor.getString(cursor.getColumnIndex("detail"));
            Log.e("MainActivity", "query : table_name = " + table + ", name = " + name + ", detail = " + detail);
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void update() {
        ContentValues values = new ContentValues();
        values.put("detail", "my name is update");
        int count = this.getContentResolver().update(mCurrentURI, values, "_id = 1", null);
        Log.e("MainActivity", "update : count = " + count);
        query();
    }

    private void delete() {
        int count = this.getContentResolver().delete(mCurrentURI, "_id = 1", null);
        Log.e("MainActivity", "delete : count = " + count);
        query();
    }

    private void switchURI(Uri uri) {
        mCurrentURI = uri;
        mtv.setText("switch URI = " + mCurrentURI.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.first_uri: {
                switchURI(getUri(TABLE_FIRST_NAME));
            }
            break;
            case R.id.second_uri: {
                switchURI(getUri(TABLE_SECOND_NAME));
            }
            break;
            case R.id.delete: {
                try {
                    delete();
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.insert: {
                try {
                    insert();
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.query: {
                try {
                    query();
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.update: {
                try {
                    update();
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.secondPart: {
                try {
                    secondPart();
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }
}