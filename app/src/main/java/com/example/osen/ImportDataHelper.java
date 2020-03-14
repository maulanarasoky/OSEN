package com.example.osen;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.osen.database.MyDatabaseOpenHelper;
import com.example.osen.model.Classroom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class ImportDataHelper {
    public Context context;
    private SQLiteDatabase database;
    private MyDatabaseOpenHelper myDatabaseOpenHelper;


    public ImportDataHelper(Context context) {
        this.context = context;

        myDatabaseOpenHelper = new MyDatabaseOpenHelper(context);
        database = myDatabaseOpenHelper.getWritableDatabase();
    }

    private String readJsonFile(Uri data) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = null;
        try {
            File file = new File(data.getPath());
            String[] split = file.getPath().split(":");//split the path.
            String filePath = split[1];

            String jsonDataString = null;
            inputStream = new FileInputStream(filePath);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferReader.readLine()) != null){
                builder.append(jsonDataString);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        Log.d("OUTPUT", builder.toString());
        return new String(builder);
    }

    public void readToDb(Uri data) throws IOException, JSONException {
        String ID = "ID_";
        String IMAGE = "IMAGE";
        String NAME = "NAME";
        String TYPE = "TYPE";
        String CATEGORY = "CATEGORY";
        String START_DATE = "START_DATE";
        String END_DATE = "END_DATE";
        String START_TIME = "START_TIME";
        String END_TIME = "END_TIME";
        String DAY = "DAY";
        String TEACHER_ID = "TEACHER_ID";
        try {
            String jsonDataString = readJsonFile(data);
            JSONArray menuItemJsonArray = new JSONArray(jsonDataString);
            for(int i = 0; i < menuItemJsonArray.length(); i++){

                JSONObject jsonObject = menuItemJsonArray.getJSONObject(i);

                ContentValues contentValue = new ContentValues();
                contentValue.put(ID, jsonObject.getString(ID));
                contentValue.put(IMAGE, jsonObject.getString(IMAGE));
                contentValue.put(NAME, jsonObject.getString(NAME));
                contentValue.put(TYPE, jsonObject.getString(TYPE));
                contentValue.put(CATEGORY, jsonObject.getString(CATEGORY));
                contentValue.put(START_DATE, jsonObject.getString(START_DATE));
                contentValue.put(END_DATE, jsonObject.getString(END_DATE));
                contentValue.put(START_TIME, jsonObject.getString(START_TIME));
                contentValue.put(END_TIME, jsonObject.getString(END_TIME));
                contentValue.put(DAY, jsonObject.getString(DAY));
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID));

                database.insert(Classroom.TABLE_CLASSROOM, null, contentValue);

                Log.d("INSERT SUCCESS", contentValue.toString());
            }
        }catch (JSONException e){
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }
}
