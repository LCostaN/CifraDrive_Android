package cifradrive.fatec.br.models;

import java.util.ArrayList;

public class Routes {
    public static final Integer LOGIN                    = 0;
    public static final Integer CADASTRAR_USUARIO        = 1;
    public static final Integer PERFIL                   = 2;
    public static final Integer DOWNLOAD_FOTO_PERFIL     = 3;
    public static final Integer GET_MUSICAS_NOMES        = 4;
    public static final Integer CADASTRAR_MUSICA         = 5;
    public static final Integer CADASTRAR_ARQUIVO_MUSICA = 6;
    public static final Integer LISTA_MUSICAS            = 7;
    public static final Integer LISTA_VERSOES            = 8;
    public static final Integer VER_MUSICA               = 9;
    public static final Integer DOWNLOAD_MUSICA          = 10;
    public static final Integer LIKE_MUSICA              = 11;
    public static final Integer HATE_MUSICA              = 12;
    public static final Integer CADASTRAR_GRUPO          = 13;
    public static final Integer ATUALIZAR_GRUPO          = 14;
    public static final Integer MEUS_GRUPOS              = 15;
    public static final Integer BUSCAR_GRUPOS            = 16;
    public static final Integer VER_GRUPO                = 17;
    public static final Integer DOWNLOAD_FOTO_GRUPO      = 18;
    private final String BASE_URL = "http://192.168.17.111/CifraDrive/api/";
    private ArrayList<String> routes = new ArrayList<>();

    public Routes() {
        routes.add("login/entrar");
        routes.add("usuario/novo");
        routes.add("usuario/perfil");
        routes.add("usuario/download");
        routes.add("musicas/getInfo");
        routes.add("musicas/novaMusica");
        routes.add("musicas/arquivoCifra");
        routes.add("musicas/listarMusicas");
        routes.add("musicas/versoes");
        routes.add("musicas/verMusica");
        routes.add("musicas/download");
        routes.add("musicas/like");
        routes.add("musicas/hate");
        routes.add("grupos/novoGrupo");
        routes.add("grupos/atualizar");
        routes.add("grupos/meusGrupos");
        routes.add("grupos/buscar");
        routes.add("grupos/verGrupo");
        routes.add("grupos/download");
    }

    public String getStringUrl(int function, int id) {
        String urlString = BASE_URL + routes.get(function) + "/" + id;
        return urlString;
    }

    public String getStringUrl(int function){
        String urlString = BASE_URL + routes.get(function);
        return urlString;
    }
}
