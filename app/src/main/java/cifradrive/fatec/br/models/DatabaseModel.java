package cifradrive.fatec.br.models;

import android.provider.BaseColumns;

public final class DatabaseModel {
    private DatabaseModel() {
    }

    public static class Grupos implements BaseColumns {
        public static final String TABLE_NAME = "grupos";
        public static final String COLUMN_NAME = "nome";
        public static final String COLUMN_DESCRIPTION = "descricao";
        public static final String COLUMN_WEBSITE = "website";
        public static final String COLUMN_TAGS = "tags";
        public static final String COLUMN_LIDER = "lider";
        public static final String COLUMN_URI_FOTO = "foto";
        public static final String COLUMN_ID_USUARIO = "idUsuario";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_DESCRIPTION + "TEXT, " +
                    COLUMN_WEBSITE + " TEXT, " +
                    COLUMN_TAGS + " TEXT, " +
                    COLUMN_LIDER + " TEXT, " +
                    COLUMN_URI_FOTO + " TEXT" +
                ")";
    }

    public static class Musicas implements BaseColumns {
        public static final String TABLE_NAME = "musicas";
        public static final String COLUMN_NAME = "nome";


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT" +
                ")";
    }

    public static class Versoes implements BaseColumns {
        public static final String TABLE_NAME = "usuario";
        public static final String COLUMN_IDMUSICA = "idMusica";
        public static final String COLUMN_IDUSUARIO = "idUsuario";
        public static final String COLUMN_NAME = "nome";
        public static final String COLUMN_TOM_ORIGINAL = "tomOriginal";
        public static final String COLUMN_URI_ARQUIVO = "arquivo";
        public static final String COLUMN_DATA = "dataAdicionado";
        public static final String COLUMN_AVALIACAO = "avaliacao";
        public static final String COLUMN_TAGS = "tags";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_IDMUSICA + "INTEGER, " +
                    COLUMN_IDUSUARIO + "INTEGER, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_TOM_ORIGINAL + " TEXT, " +
                    COLUMN_URI_ARQUIVO + " TEXT, " +
                    COLUMN_DATA + " INTEGER, " +
                    COLUMN_AVALIACAO + " TEXT, " +
                    COLUMN_TAGS + " TEXT" +
                ")";
    }
}