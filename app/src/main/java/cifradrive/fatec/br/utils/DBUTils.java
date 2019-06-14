package cifradrive.fatec.br.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cifradrive.fatec.br.models.DatabaseModel;

public class DBUTils extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cifra_drive";

    public DBUTils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DatabaseModel.Grupos.CREATE_TABLE);
        sqLiteDatabase.execSQL(DatabaseModel.Musicas.CREATE_TABLE);
        sqLiteDatabase.execSQL(DatabaseModel.Versoes.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseModel.Grupos.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseModel.Musicas.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseModel.Versoes.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
