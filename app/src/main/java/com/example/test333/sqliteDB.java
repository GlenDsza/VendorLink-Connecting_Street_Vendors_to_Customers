package com.example.test333;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sqliteDB extends SQLiteOpenHelper {
    Context context;

    public sqliteDB(Context context) {
        super(context, "Salesdata.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table userinfo(document_name String,login_type Sting)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("Drop table if exists userinfo");
    }
    public boolean insertDoc(String doc_name,String log_type) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("document_name",doc_name);
        contentValues.put("login_type",log_type);
        long result = DB.insert("userinfo", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean deletedoc(String doc_name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from userinfo where document_name = ?", new String[] {String.valueOf(doc_name)});
        if (cursor.getCount()>0) {
            long result = DB.delete("userinfo ", "document_name=?", new String[]{String.valueOf(doc_name)});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
        else{
            return false;
        }
    }

    public String getDoc() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor c1 = DB.rawQuery("select * from userinfo",null);
        if(c1.getCount()>0)
        {
            c1.moveToFirst();
            String doc = c1.getString(0);
            return  doc;
        }
        else
        {
            return null;
        }
    }
    public String getType() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor c1 = DB.rawQuery("select * from userinfo",null);
        if(c1.getCount()>0)
        {
            c1.moveToFirst();
            String doc = c1.getString(1);
            return  doc;
        }
        else
        {
            return null;
        }
    }
}
