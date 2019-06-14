package cifradrive.fatec.br;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cifradrive.fatec.br.adapters.MusicListAdapter;
import cifradrive.fatec.br.models.BaseActivity;
import cifradrive.fatec.br.models.WebRequest;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.LocalDataUtils;
import cifradrive.fatec.br.utils.NetworkUtils;
import cifradrive.fatec.br.utils.WebUtils;

public class ListaMusicaActivity extends BaseActivity implements MusicListAdapter.MusicListHandler {

    private final String TAG = "LISTA_MUSICAS";

    private RecyclerView listaMusicas;

    private LocalDataUtils localHelper;
    private Routes routes = new Routes();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_musica);

        EditText procurarMusica = findViewById(R.id.et_procurarMusica);
        ImageButton adicionarMusica = findViewById(R.id.ib_addMusic);

        localHelper = new LocalDataUtils( getBaseContext() );
        url = routes.getStringUrl( Routes.LISTA_MUSICAS );
        Log.v("URL", url);

        listaMusicas = findViewById(R.id.rv_musicList);
        listaMusicas.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listaMusicas.setLayoutManager(layoutManager);

        getDados(null);

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

        procurarMusica.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getDados(v.getText().toString());

                    v.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(int id, String nome) {
        Intent details = new Intent(ListaMusicaActivity.this, ListaVersaoActivity.class);
        details.putExtra("idMusica", id);
        details.putExtra("nomeMusica", nome);
        startActivity(details);
    }


    public void getDados(final String search) {
        if( NetworkUtils.isConnected( getBaseContext() ) ){
            WebUtils webUtils = WebUtils.getInstance(getApplicationContext());

            WebRequest webRequest = new WebRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, response);
                            try {
                                JSONObject result = new JSONObject(response);
                                boolean logado = result.getBoolean("logged");
                                if( logado ){
                                    JSONArray lista = result.getJSONArray("data");
                                    showList(lista);
                                } else {
                                    requestLogin();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG + "_ERROR", error.getMessage());
                        }
                    }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    if (search != null) {
                        params.put("procurar", search);
                    }

                    return params;
                }
            };

            webRequest.setTag(TAG);
            webUtils.addToRequestQueue(webRequest);
        } else {
            // TODO: CHECK AND GET DATA FROM FILE SYSTEM
        }
    }

    public void showList(JSONArray dados) {
        if (dados != null && dados.length() > 0) {
            try {
                String[] nomes = new String[dados.length()];
                String[] subText = new String[dados.length()];
                int[] ids = new int[dados.length()];

                for (int i = 0; i < dados.length(); i++) {

                    JSONObject obj = dados.getJSONObject(i);

                    nomes[i] = obj.getString("nome");
                    subText[i] = obj.getString("totalVersoes") + "  versões";
                    ids[i] = obj.getInt("id");
                }

                RecyclerView.Adapter listaAdapter = new MusicListAdapter(nomes, subText, ids, this);
                listaMusicas.setAdapter(listaAdapter);
            } catch (JSONException e) {
                Log.e("JSON_DATA", e.toString());
            }
        } else {
            Log.e("NULL_LIST", "LISTA SEM ELEMENTOS OU VALOR NULO");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.errorLoginMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}