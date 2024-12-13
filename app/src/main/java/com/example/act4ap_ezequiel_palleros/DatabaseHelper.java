package com.example.act4ap_ezequiel_palleros;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "binding_of_isaac.db";
    private static final int DATABASE_VERSION = 10;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Usuario (" +
                "idUsu INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nomUsu TEXT NOT NULL, " +
                "emailUsu TEXT UNIQUE NOT NULL, " +
                "contraUsu TEXT NOT NULL)");

        db.execSQL("CREATE TABLE DLC (" +
                "idDlc INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nomDlc TEXT NOT NULL, " +
                "lanzaDlc INTEGER NOT NULL, " +
                "imgDlc TEXT)");

        db.execSQL("CREATE TABLE item_pasivo (" +
                "idPas INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nomPas TEXT NOT NULL, " +
                "descPas TEXT NOT NULL, " +
                "calidadPas INTEGER NOT NULL, " +
                "imgPas TEXT, " +
                "idDlc INTEGER, " +
                "FOREIGN KEY (idDlc) REFERENCES DLC(idDlc))");

        db.execSQL("CREATE TABLE item_activo (" +
                "idAct INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nomAct TEXT NOT NULL, " +
                "descAct TEXT NOT NULL, " +
                "calidadAct INTEGER NOT NULL, " +
                "cargaAct INTEGER NOT NULL, " +
                "imgAct TEXT, " +
                "idDlc INTEGER, " +
                "FOREIGN KEY (idDlc) REFERENCES DLC(idDlc))");

        db.execSQL("INSERT INTO Usuario (idUsu, nomUsu, emailUsu, contraUsu) VALUES (100, 'persona', 'persona@gmail.com', '1234')");

        db.execSQL("INSERT INTO DLC (nomDlc, lanzaDlc, imgDlc) VALUES ('Flash', 2011, 'dlc1')");
        db.execSQL("INSERT INTO DLC (nomDlc, lanzaDlc, imgDlc) VALUES ('Rebirth', 2014, 'dlc2')");
        db.execSQL("INSERT INTO DLC (nomDlc, lanzaDlc, imgDlc) VALUES ('Afterbirth', 2015, 'dlc3')");
        db.execSQL("INSERT INTO DLC (nomDlc, lanzaDlc, imgDlc) VALUES ('Afterbirth+', 2017, 'dlc4')");
        db.execSQL("INSERT INTO DLC (nomDlc, lanzaDlc, imgDlc) VALUES ('Repentance', 2021, 'dlc5')");

        db.execSQL("INSERT INTO item_pasivo (idPas, nomPas, descPas, calidadPas, imgPas, idDlc) VALUES (1, 'Magic Mushroom', 'Mas Dano y vida.', 4, 'itempdlc1', 1)");
        db.execSQL("INSERT INTO item_pasivo (idPas, nomPas, descPas, calidadPas, imgPas, idDlc) VALUES (2, 'Godhead', 'Lagrimas con aura.', 4, 'itempdlc2', 2)");
        db.execSQL("INSERT INTO item_pasivo (idPas, nomPas, descPas, calidadPas, imgPas, idDlc) VALUES (3, 'Black Candle', 'No maldiciones', 3, 'itempdlc3', 3)");
        db.execSQL("INSERT INTO item_pasivo (idPas, nomPas, descPas, calidadPas, imgPas, idDlc) VALUES (4, 'Tech X', 'Rayo circular.', 4, 'itempdlc4', 4)");
        db.execSQL("INSERT INTO item_pasivo (idPas, nomPas, descPas, calidadPas, imgPas, idDlc) VALUES (5, 'Akeldama', 'Fila de lágrimas', 1, 'itempdlc5', 5)");

        db.execSQL("INSERT INTO item_activo (idAct, nomAct, descAct, calidadAct, cargaAct, imgAct, idDlc) VALUES (1, 'D6', 'Cambia item.', 4, 6, 'itemadlc1', 1)");
        db.execSQL("INSERT INTO item_activo (idAct, nomAct, descAct, calidadAct, cargaAct, imgAct, idDlc) VALUES (2, 'Red Candle', 'Tira fuego.', 1, 1, 'itemadlc2', 2)");
        db.execSQL("INSERT INTO item_activo (idAct, nomAct, descAct, calidadAct, cargaAct, imgAct, idDlc) VALUES (3, 'Mr Boom', 'Una bomba.', 1, 2, 'itemadlc3', 3)");
        db.execSQL("INSERT INTO item_activo (idAct, nomAct, descAct, calidadAct, cargaAct, imgAct, idDlc) VALUES (4, 'Moving Box', 'Mueves los items.', 2, 4, 'itemadlc4', 4)");
        db.execSQL("INSERT INTO item_activo (idAct, nomAct, descAct, calidadAct, cargaAct, imgAct, idDlc) VALUES (5, 'R Key', 'Reinicia la partida.', 4, 1, 'itemadlc5', 5)");
    }

    // Metodo para actualizar las tablas.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar las tablas si existen
        db.execSQL("DROP TABLE IF EXISTS Usuario");
        db.execSQL("DROP TABLE IF EXISTS DLC");
        db.execSQL("DROP TABLE IF EXISTS item_pasivo");
        db.execSQL("DROP TABLE IF EXISTS item_activo");

        onCreate(db);
    }

    // Metodo para agregar DLCs a la BDD
    public void addDLC(String nomDlc, String lanzaDlc, String imgDlc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomDlc", nomDlc);
        values.put("imgDlc", imgDlc);

        if (lanzaDlc != null && !lanzaDlc.isEmpty()) {
            try {
                int lanzamiento = Integer.parseInt(lanzaDlc);
                values.put("lanzaDlc", lanzamiento);
            } catch (NumberFormatException e) {
                values.put("lanzaDlc", 0);
            }
        } else {
            values.put("lanzaDlc", 0);
        }

        db.insert("DLC", null, values);
        db.close();
    }

    // Metodo para eliminar DLCs de la BDD
    public void deleteDLC(String dlcId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("item_pasivo", "idDlc = ?", new String[]{dlcId});
        db.delete("item_activo", "idDlc = ?", new String[]{dlcId});
        db.delete("DLC", "idDlc = ?", new String[]{dlcId});
        db.close();
    }

    // Metodo para agregar un ítem pasivo
    public void addPassiveItem(String name, String description, int quality, String image, String idDlc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomPas", name);
        values.put("descPas", description);
        values.put("calidadPas", quality);
        values.put("imgPas", image);
        values.put("idDlc", Integer.parseInt(idDlc));

        long result = db.insert("item_pasivo", null, values);

        db.close();
    }

    // Metodo para agregar un ítem activo
    public void addActiveItem(String name, String description, int quality, int charges, String image, String idDlc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomAct", name);
        values.put("descAct", description);
        values.put("calidadAct", quality);
        values.put("cargaAct", charges);
        values.put("imgAct", image);
        values.put("idDlc", Integer.parseInt(idDlc));

        long result = db.insert("item_activo", null, values);
        db.close();
    }

    // Metodo para obtener items Pasivos por DLC (muuuuy dificil, ni se que es HashMap)
    public ArrayList<HashMap<String, String>> getPassiveItemsByDLC(String idDlc) {
        ArrayList<HashMap<String, String>> itemList = new ArrayList<>();
        if (idDlc == null || idDlc.isEmpty()) {
            return itemList;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM item_pasivo WHERE idDlc = ?", new String[]{idDlc});

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("nomPas");
            int descIndex = cursor.getColumnIndex("descPas");
            int imgIndex = cursor.getColumnIndex("imgPas");
            int calidadIndex = cursor.getColumnIndex("calidadPas");

            if (nameIndex != -1 && descIndex != -1 && imgIndex != -1) {
                do {
                    HashMap<String, String> item = new HashMap<>();
                    item.put("name", cursor.getString(nameIndex));
                    item.put("description", cursor.getString(descIndex));
                    item.put("img", cursor.getString(imgIndex));
                    item.put("calidad", cursor.getString(calidadIndex));
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Metodo para obtener items Activos por DLC (igual de dificil)
    public ArrayList<HashMap<String, String>> getActiveItemsByDLC(String idDlc) {
        ArrayList<HashMap<String, String>> itemList = new ArrayList<>();
        if (idDlc == null || idDlc.isEmpty()) {
            return itemList;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM item_activo WHERE idDlc = ?", new String[]{idDlc});

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("nomAct");
            int descIndex = cursor.getColumnIndex("descAct");
            int imgIndex = cursor.getColumnIndex("imgAct");
            int cargaIndex = cursor.getColumnIndex("cargaAct");
            int calidadIndex = cursor.getColumnIndex("calidadAct");


            if (nameIndex != -1 && descIndex != -1 && imgIndex != -1) {
                do {
                    HashMap<String, String> item = new HashMap<>();
                    item.put("name", cursor.getString(nameIndex));
                    item.put("description", cursor.getString(descIndex));
                    item.put("img", cursor.getString(imgIndex));
                    item.put("carga", cursor.getString(cargaIndex));
                    item.put("calidad", cursor.getString(calidadIndex));
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Metodo para obtener las cargas de un item
    public int getItemCharges(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cargaAct FROM item_activo WHERE nomAct = ?";
        Cursor cursor = db.rawQuery(query, new String[]{itemName});

        int itemCharges = 0; // Valor predeterminado para ítems pasivos

        if (cursor != null && cursor.moveToFirst()) {
            itemCharges = cursor.getInt(cursor.getColumnIndex("cargaAct")); // No entiendo el error pero esto funciona igual.
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return itemCharges;
    }

    // Metodo para borrar el ítem
    public void deleteItem(String itemName, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();

        String tableName = isActive ? "item_activo" : "item_pasivo";
        String columnName = isActive ? "nomAct" : "nomPas";

        int rowsAffected = db.delete(tableName, columnName + " = ?", new String[]{itemName});

        db.close();
    }


    // Metodo para validar usuarios de la BDD (gracias Victor)
    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = false;

        try {
            cursor = db.rawQuery("SELECT * FROM Usuario WHERE emailUsu=? AND contraUsu=?", new String[]{email, password});
            result = cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return result;
    }

    // Metodo para insertar usuarios a la BDD
    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomUsu", name);
        values.put("emailUsu", email);
        values.put("contraUsu", password);

        long result = db.insert("Usuario", null, values);
        db.close();

        return result != -1;
    }

    // Metodo para verificar si el email ya fue utilizado
    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isTaken = false;

        try {
            cursor = db.rawQuery("SELECT * FROM Usuario WHERE emailUsu=?", new String[]{email});
            if (cursor.moveToFirst()) {
                isTaken = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return isTaken; // isTaken es yaTomado en espanol, pero aguante el ingles B)
    }

    // Metodo para obtener todos los DLCs (más díficil que con los items)
    public ArrayList<HashMap<String, String>> getAllDLCs() {
        ArrayList<HashMap<String, String>> dlcs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT idDlc, nomDlc, imgDlc FROM DLC", null);

        if (cursor != null && cursor.moveToFirst()) {
            int idDlcIndex = cursor.getColumnIndex("idDlc");
            int nomDlcIndex = cursor.getColumnIndex("nomDlc");
            int imgDlcIndex = cursor.getColumnIndex("imgDlc");

            if (idDlcIndex != -1 && nomDlcIndex != -1 && imgDlcIndex != -1) {
                do {
                    HashMap<String, String> dlc = new HashMap<>();
                    dlc.put("idDlc", cursor.getString(idDlcIndex));
                    dlc.put("nomDlc", cursor.getString(nomDlcIndex));
                    dlc.put("imgDlc", cursor.getString(imgDlcIndex));
                    dlcs.add(dlc);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return dlcs;
    }

}