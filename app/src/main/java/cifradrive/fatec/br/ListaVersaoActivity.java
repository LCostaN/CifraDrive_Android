package cifradrive.fatec.br;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cifradrive.fatec.br.adapters.MusicListAdapter;
import cifradrive.fatec.br.models.BaseActivity;
import cifradrive.fatec.br.models.WebRequest;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.NetworkUtils;
import cifradrive.fatec.br.utils.WebUtils;

public class ListaVersaoActivity extends BaseActivity implements MusicListAdapter.MusicListHandler {

    private final String TAG = "LISTA_VERSOES";

    private RecyclerView listaMusicas;
    private Routes routes = new Routes();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_versao);

        Intent intent = getIntent();
        String nomeMusica = intent.getStringExtra("nomeMusica");
        int idMusica = intent.getIntExtra("idMusica", 0);

        ImageButton adicionarMusica = findViewById(R.id.ib_addMusic);
        TextView tv_musica = findViewById(R.id.tv_musica);
        tv_musica.setText(nomeMusica);

        url = routes.getStringUrl(Routes.LISTA_VERSOES, idMusica);

        listaMusicas = findViewById(R.id.rv_versionList);
        listaMusicas.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listaMusicas.setLayoutManager(layoutManager);

        getDadosOnline();

        adicionarMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( NetworkUtils.isConnected(getBaseContext()) ){
                    Intent novaMusica = new Intent( getBaseContext(), CadastroDadosMusicaActivity.class);
                    startActivity(novaMusica);
                } else {
                    Toast.makeText( getBaseContext(), R.string.notConnectedMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(int id, String nome) {
        Intent details = new Intent(ListaVersaoActivity.this, VerMusicaActivity.class);
        details.putExtra("idVersao", id);
        startActivity(details);
    }


    public void getDadosOnline() {
        if( NetworkUtils.isConnected( getBaseContext() ) ){
            WebUtils webUtils = WebUtils.getInstance(getApplicationContext());

            WebRequest webRequest = new WebRequest
                    (Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, response);
                            try {
                                JSONObject result = new JSONObject(response);
                                boolean logado = result.getBoolean("logged");
                                if( logado ) {
                                    showList( result.getJSONObject("data").getJSONArray("versoes") );
                                } else {
                                    removeLoginHash();
                                    requestLogin();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.toString());
                        }
                    });

            webRequest.setTag(TAG);
            webUtils.addToRequestQueue(webRequest);
        } else {
            // TODO: CHECK AND GET DATA FROM FILE SYSTEM
        }
    }

    public void showList(JSONArray versoes) {
        if (versoes != null && versoes.length() > 0) {
            try {
                String[] nomes = new String[versoes.length()];
                String[] subText = new String[versoes.length()];
                int[] ids = new int[versoes.length()];

                for (int i = 0; i < versoes.length(); i++) {
                    JSONObject obj = versoes.getJSONObject(i);

                    nomes[i] = obj.getString("nome");
                    subText[i] = obj.getString("tags");
                    ids[i] = obj.getInt("id");
                }

                RecyclerView.Adapter listaAdapter = new MusicListAdapter(nomes, subText, ids, this);
                listaMusicas.setAdapter(listaAdapter);
            } catch (JSONException e) {
                Log.e("JSON_DATA", e.toString());
            }
        } else {
            Log.e("NULL_LIST", "LISTA SEM ELEMENTOS OU DE VALOR NULO");
        }
    }
}