package com.example.mistapas.ui.login;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class BdController extends SQLiteOpenHelper {

    //BDEjemplo, con una sóla tabla llamada Data

    //Sentencia SQL para crear la tabla de de almacenamiento
    String sqlCreate = "CREATE TABLE Login (id_disp TEXT PRIMARY KEY ,configuracion TEXT , id_user INTEGER)";


    public BdController(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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

    public  static void insertarData(Context c,String id_disp, String token , int id_user) {
        //Abrimos la base de datos  en modo escritura
        BdController Login = new BdController(c, "Login", null, 1);
        SQLiteDatabase bd = Login.getWritableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            try {
                bd.execSQL("DELETE FROM Login");

                //insertamos el juego recibido con su id
                bd.execSQL("INSERT INTO Login (id_disp  , configuracion , id_user   ) " +
                        "VALUES ('" + id_disp + "','" +token+ "', " + id_user +  ")");

                //Cerramos la base de datos
                bd.close();
            } catch (Exception e) {

            }
        }
    }


    public static  int selectIdUser(Context c) {
        int id = 0;
        BdController Login = new BdController(c, "Login", null, 1);
        SQLiteDatabase bd = Login.getWritableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos
            Cursor cur = bd.rawQuery(" SELECT id_user  FROM Login", null);
            //Nos aseguramos de que existe al menos un registro
            if (cur.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros y se llena la lista de juegos
                id = cur.getInt(0);

            }
            //Cerramos la base de datos
            bd.close();
        }

        return id;
    }

}
