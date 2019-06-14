package cifradrive.fatec.br.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cifradrive.fatec.br.R;
import cifradrive.fatec.br.models.DatabaseModel;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

public class LocalDataUtils {
    public static final int GRUPO  = 0;
    public static final int PERFIL = 1;
    public static final int MUSICA = 2;
    public static final int VERSOES = 3;

    private ArrayList<String> fileDirs  = new ArrayList<>();

    private Context context;
    private SharedPreferences preferences;

    public LocalDataUtils(Context context){
        this.context = context.getApplicationContext();
        preferences = context.getSharedPreferences( context.getString( R.string.preferences_file ), Context.MODE_PRIVATE );

        fileDirs.add("/grupo");
        fileDirs.add("/usuario");
        fileDirs.add("/cifra");
    }

    /* TODO: IMPLEMENTAR METODO saveNewData. Salva dados encontrados do acesso ao servidor e retorna booleano de sucesso.  */

    public String saveFile(byte[] data){

        // TODO: IMPLEMENTAR INCLUSÃO DE ARQUIVO NO SISTEMA.
        // TODO: RETORNAR URI.toString()
        return "";
    }

    private long saveToDB(ContentValues values, String tableName){
        SQLiteDatabase db = new DBUTils( context.getApplicationContext() ).getWritableDatabase();
        return db.insertWithOnConflict(tableName, null, values, CONFLICT_REPLACE);
    }

    public JSONArray getLista(int tipo){
        SQLiteDatabase db = new DBUTils( context.getApplicationContext() ).getReadableDatabase();
        JSONArray lista = new JSONArray();
        Cursor cursor = null;
        String[] projection = null;

        switch(tipo){
            case GRUPO:
                projection = new String[] {
                        DatabaseModel.Grupos._ID,
                        DatabaseModel.Grupos.COLUMN_NAME,
                        DatabaseModel.Grupos.COLUMN_TAGS
                };

                cursor = db.query(
                        DatabaseModel.Grupos.TABLE_NAME,     // The table to query
                        projection,                          // The columns to return
                        null,                       // The columns for the WHERE clause
                        null,                    // The values for the WHERE clause
                        null,                        // GroupBy
                        null,                         // Having
                        null                         // OrderBy
                );

                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++){
                    if( cursor.isAfterLast() ){
                        break;
                    }
                    JSONObject json = new JSONObject();

                    int id = cursor.getInt( cursor.getColumnIndexOrThrow( DatabaseModel.Grupos._ID ) );
                    String nome = cursor.getString( cursor.getColumnIndexOrThrow(DatabaseModel.Grupos.COLUMN_NAME) );
                    String tags = cursor.getString( cursor.getColumnIndexOrThrow(DatabaseModel.Grupos.COLUMN_TAGS) );

                    try {
                        json.put("id", id);
                        json.put("nome", nome);
                        json.put("tags", tags);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON_PUT_LISTA_GR", e.getMessage() );
                    }

                    lista.put(json);
                    cursor.moveToNext();
                }
                break;
            case MUSICA:
                projection = new String[] {
                        DatabaseModel.Grupos._ID,
                        DatabaseModel.Grupos.COLUMN_NAME,
                        DatabaseModel.Grupos.COLUMN_TAGS
                };

                cursor = db.query(
                        DatabaseModel.Grupos.TABLE_NAME,     // The table to query
                        projection,                          // The columns to return
                        null,                       // The columns for the WHERE clause
                        null,                    // The values for the WHERE clause
                        null,                        // GroupBy
                        null,                         // Having
                        null                         // OrderBy
                );

                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++){
                    if( cursor.isAfterLast() ){
                        break;
                    }
                    JSONObject json = new JSONObject();

                    int id = cursor.getInt( cursor.getColumnIndexOrThrow( DatabaseModel.Grupos._ID ) );
                    String nome = cursor.getString( cursor.getColumnIndexOrThrow(DatabaseModel.Grupos.COLUMN_NAME) );
                    String tags = cursor.getString( cursor.getColumnIndexOrThrow(DatabaseModel.Grupos.COLUMN_TAGS) );

                    try {
                        json.put("id", id);
                        json.put("nome", nome);
                        json.put("tags", tags);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON_PUT_LISTA_GR", e.getMessage() );
                    }

                    lista.put(json);
                    cursor.moveToNext();
                }
                break;
            case VERSOES:
                projection = new String[] {
                        DatabaseModel.Grupos._ID,
                        DatabaseModel.Grupos.COLUMN_NAME,
                        DatabaseModel.Grupos.COLUMN_TAGS
                };

                cursor = db.query(
                        DatabaseModel.Grupos.TABLE_NAME,     // The table to query
                        projection,                          // The columns to return
                        null,                       // The columns for the WHERE clause
                        null,                    // The values for the WHERE clause
                        null,                        // GroupBy
                        null,                         // Having
                        null                         // OrderBy
                );

                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++){
                    if( cursor.isAfterLast() ){
                        break;
                    }
                    JSONObject json = new JSONObject();

                    int id = cursor.getInt( cursor.getColumnIndexOrThrow( DatabaseModel.Grupos._ID ) );
                    String nome = cursor.getString( cursor.getColumnIndexOrThrow(DatabaseModel.Grupos.COLUMN_NAME) );
                    String tags = cursor.getString( cursor.getColumnIndexOrThrow(DatabaseModel.Grupos.COLUMN_TAGS) );

                    try {
                        json.put("id", id);
                        json.put("nome", nome);
                        json.put("tags", tags);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON_PUT_LISTA_GR", e.getMessage() );
                    }

                    lista.put(json);
                    cursor.moveToNext();
                }
                break;
            default:
                Toast.makeText(context, "Lista não existe.", Toast.LENGTH_SHORT).show();
        }
        if( cursor != null && !cursor.isClosed() ){
            cursor.close();
        }

        return lista;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private File getDirOf(int tipo){
        if(tipo == VERSOES){
            tipo = MUSICA;
        }
        File rootDir;
        File chosenDir;

        if( preferences.getBoolean("armazenamento_externo", false) && isExternalStorageWritable() ){
//            Armazenamento Externo
            if(tipo != MUSICA){
                rootDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            } else {
                rootDir = context.getExternalFilesDir(null);
            }
        } else {
//            Armazenamento Interno
            rootDir = context.getFilesDir();
        }
        chosenDir = new File(rootDir, fileDirs.get(tipo) );

        if ( !chosenDir.exists() ) {
            boolean mkdir = chosenDir.mkdir();
            Log.i("MKDIR_INFO", "Directory not created. Creating now.");
            if( !mkdir ){
                Log.e("MKDIR_ERROR", "Directory was not created successfully.");
                return null;
            }
        }

        return chosenDir;
    }
}