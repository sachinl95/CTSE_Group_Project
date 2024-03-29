package com.sliit.learnmedicine.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sliit.learnmedicine.DTO.Medicine;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the Database related methods to ease reading and writing to the Sql Lite database
 */
public class MedicineDatabaseHelper extends SQLiteOpenHelper {

    private final static String TAG = "DB Helper";

    private final static String DB_NAME = "medicine.db";
    private final static String TABLE_NAME = "medicines";

    /**
     * Create the medicines table if it does not exist
     *
     * @param context
     */
    public MedicineDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(" +
                "id text PRIMARY KEY," +
                "name text," +
                "description text," +
                "favourite boolean" +
                ")");
    }

    /**
     * Saves one medicine record (Not Currently Used: Was implemented expecting it to be needed but
     * was actually not)
     *
     * @param medicine
     */
    public void saveOne(Medicine medicine) {
        ContentValues cv = new ContentValues();
        cv.put("id", medicine.getId());
        cv.put("name", medicine.getName());
        cv.put("description", medicine.getDescription());
        cv.put("favourite", medicine.isFavourite());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    /**
     * Saves a list of medicines in the database (Clears the table first to prevent conflicts)
     *
     * @param medicineList List of medicine objects
     */
    public void saveAll(List<Medicine> medicineList) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        ContentValues cv = new ContentValues();
        for (Medicine medicine : medicineList) {
            cv.clear();
            cv.put("id", medicine.getId());
            cv.put("name", medicine.getName());
            cv.put("description", medicine.getDescription());
            cv.put("favourite", medicine.isFavourite());
            db.insert(TABLE_NAME, null, cv);
        }
        db.close();
    }

    /**
     * Reads and returns all medicine records currently stored in the database
     *
     * @return List of Medicine Objects
     */
    public List<Medicine> readAll() {
        List<Medicine> medicineList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToNext();
        int id = cursor.getColumnIndex("id");
        int name = cursor.getColumnIndex("name");
        int description = cursor.getColumnIndex("description");
        int favorite = cursor.getColumnIndex("favourite");
        while (!cursor.isAfterLast()) {
            Medicine medicine = new Medicine();
            medicine.setId(cursor.getString(id));
            medicine.setName(cursor.getString(name));
            medicine.setDescription(cursor.getString(description));
            medicine.setFavourite(cursor.getInt(favorite) > 0);
            medicineList.add(medicine);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return medicineList;
    }

    /**
     * Reads all medicines marked as favorite and returns them
     *
     * @return List of Medicines marked as favorite
     */
    public List<Medicine> readFavourites() {
        List<Medicine> medicineList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE favourite > 0",
                null);
        cursor.moveToNext();
        int id = cursor.getColumnIndex("id");
        int name = cursor.getColumnIndex("name");
        int description = cursor.getColumnIndex("description");
        int favorite = cursor.getColumnIndex("favourite");
        while (!cursor.isAfterLast()) {
            Medicine medicine = new Medicine();
            medicine.setId(cursor.getString(id));
            medicine.setName(cursor.getString(name));
            medicine.setDescription(cursor.getString(description));
            medicine.setFavourite(cursor.getInt(favorite) > 0);
            medicineList.add(medicine);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return medicineList;
    }

    /**
     * Reads one medicine record
     *
     * @param id the unique ID of the medicine
     * @return Medicine Object containing all its information
     * @throws NullPointerException Is thrown if the medicine does not exist
     */
    public Medicine readOne(String id) throws NullPointerException {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = '" + id + "'",
                null);
        cursor.moveToNext();
        if (cursor.isAfterLast()) {
            throw new NullPointerException();
        } else {
            int name = cursor.getColumnIndex("name");
            int description = cursor.getColumnIndex("description");
            int favorite = cursor.getColumnIndex("favourite");
            Medicine medicine = new Medicine();
            medicine.setId(id);
            medicine.setName(cursor.getString(name));
            medicine.setDescription(cursor.getString(description));
            medicine.setFavourite(cursor.getInt(favorite) > 0);
            return medicine;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade()");
    }
}
