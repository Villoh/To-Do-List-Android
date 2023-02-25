package com.mikel.todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mikel.todolist.Modelo.Tarea;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "ToDoList";
    private static final String TAREA_TABLE = "tarea";
    private static final String ID = "id";
    private static final String DESC_TAREA = "desc_tarea";
    private static final String FECHA_FIN = "fecha_fin";
    private static final String ESTADO = "estado";
    private static final String CREATE_TAREA_TABLE = "CREATE TABLE " + TAREA_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DESC_TAREA + " TEXT, "
            + FECHA_FIN + "TEXT, "
            + ESTADO + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TAREA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TAREA_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertarTarea(Tarea tarea){
        ContentValues cv = new ContentValues();
        cv.put(DESC_TAREA, tarea.getDescTarea());
        cv.put(FECHA_FIN, tarea.getFechaFin());
        cv.put(ESTADO, 0);
        db.insert(TAREA_TABLE, null, cv);
    }

    public List<Tarea> obtenerTareas(){
        List<Tarea> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TAREA_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        Tarea tarea = new Tarea();
                        tarea.setId(cur.getInt(cur.getColumnIndexOrThrow(ID)));
                        tarea.setDescTarea(cur.getString(cur.getColumnIndexOrThrow(DESC_TAREA)));
                        tarea.setEstado(cur.getInt(cur.getColumnIndexOrThrow(ESTADO)));
                        taskList.add(tarea);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void actualizarEstado(int id, int estado){
        ContentValues cv = new ContentValues();
        cv.put(ESTADO, estado);
        db.update(TAREA_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void actualizarDescTarea(int id, String tarea) {
        ContentValues cv = new ContentValues();
        cv.put(DESC_TAREA, tarea);
        db.update(TAREA_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void actualizarFechaFin(int id, String fechaFin){
        ContentValues cv = new ContentValues();
        cv.put(FECHA_FIN, fechaFin);
        db.update(TAREA_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void borrarTarea(int id){
        db.delete(TAREA_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}