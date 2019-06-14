package cifradrive.fatec.br;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import cifradrive.fatec.br.models.BaseActivity;
import cifradrive.fatec.br.models.WebRequest;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.NetworkUtils;
import cifradrive.fatec.br.utils.WebUtils;

public class VerMusicaActivity extends BaseActivity {
    final String TAG = "VER_MUSICA";

    protected int idVersao;
    protected int avaliacao;

    private Routes routes = new Routes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_musica);

        Intent intent = getIntent();
        idVersao = intent.getIntExtra("idVersao", 0);
        avaliacao = preferences.getInt("versao_"+idVersao, 0);

        getDados();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ver_musica, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_like:
                like(idVersao);
                return true;
            case R.id.menu_hate:
                hate(idVersao);
                return true;
            case R.id.menu_editar_cifra:
                Toast.makeText(this, "Editar Clicado", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getDados(){
        if( NetworkUtils.isConnected(getBaseContext()) ){
            WebUtils webUtils = WebUtils.getInstance( getApplicationContext() );
            String url = routes.getStringUrl(Routes.VER_MUSICA, idVersao);

            WebRequest webRequest = new WebRequest
                    (Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, response);
                            try {
                                JSONObject result = new JSONObject(response).getJSONObject("data");
                                String nomeMusica = result.getString("nomeMusica");
                                String nomeVersao = result.getString("nome");
                                String nomeCompleto = nomeMusica + " - " + nomeVersao;
                                setTitle( nomeCompleto );
                                // TODO: Acrescentar visualização de Música
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.getMessage() );
                        }
                    });

            webRequest.setTag(TAG);
            webUtils.addToRequestQueue(webRequest);
        } else {
            // TODO: Acrescentar visualização local
            finish();
        }
    }

    public void like(int id){
        String url = routes.getStringUrl(Routes.LIKE_MUSICA, id);
        if( NetworkUtils.isConnected(getBaseContext()) ){
            WebUtils webUtils = WebUtils.getInstance( getApplicationContext() );

            WebRequest webRequest = new WebRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.v(TAG+"_LIKE", response);
                    try {
                        JSONObject result = new JSONObject(response);
                        boolean logado = result.getBoolean("logged");
                        if(logado){
                            boolean success = result.getBoolean("data");
                            if(success){
                                avaliacao = 1;
                                saveAvalicao(avaliacao);
                            } else {
                                Toast.makeText(VerMusicaActivity.this, R.string.errorMessage, Toast.LENGTH_SHORT).show();
                            }
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
                    Log.e(TAG+"_LIKE", error.getMessage() );
                }
            });

            webRequest.setTag(TAG+"_LIKE");
            webUtils.addToRequestQueue(webRequest);
        } else {
            Toast.makeText(getBaseContext(), R.string.notConnectedMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void hate(int id){
        String url = routes.getStringUrl(Routes.HATE_MUSICA, id);
        if( NetworkUtils.isConnected(getBaseContext()) ){
            WebUtils webUtils = WebUtils.getInstance( getApplicationContext() );

            WebRequest webRequest = new WebRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.v(TAG+"_HATE", response);
                    try {
                        JSONObject result = new JSONObject(response);
                        boolean logado = result.getBoolean("logged");
                        if(logado){
                            boolean success = result.getBoolean("data");
                            if(success){
                                avaliacao = -1;
                                saveAvalicao(avaliacao);
                            } else {
                                Toast.makeText(VerMusicaActivity.this, R.string.errorMessage, Toast.LENGTH_SHORT).show();
                            }
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
                    Log.e(TAG+"_HATE", error.getMessage() );
                }
            });

            webRequest.setTag(TAG+"_HATE");
            webUtils.addToRequestQueue(webRequest);
        } else {
            Toast.makeText(getBaseContext(), R.string.notConnectedMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void editMode(){

    }

    public void saveAvalicao(int value){
        preferences.edit().putInt("versao_"+idVersao, value).apply();
    }
}