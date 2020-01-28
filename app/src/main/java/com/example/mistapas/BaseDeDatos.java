package com.example.mistapas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class BaseDeDatos extends SQLiteOpenHelper {

    //BDEjemplo, con una sóla tabla llamada Data

    //Sentencia SQL para crear la tabla de de almacenamiento
    String sqlCreate = "CREATE TABLE Login (id INTEGER PRIMARY KEY AUTOINCREMENT,usuario TEXT,token TEXT, id_disp TEXT)";


    public BaseDeDatos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Login");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
    }

}
