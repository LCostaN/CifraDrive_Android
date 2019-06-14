package cifradrive.fatec.br;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cifradrive.fatec.br.models.BaseActivity;
import cifradrive.fatec.br.models.WebRequest;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.WebUtils;

public class CadastroGrupoActivity extends BaseActivity {
    
    private final String TAG = "NOVO_GRUPO";

    private EditText etNome;
    private EditText etDescricao;
    private EditText etWebsite;
    private EditText etInteresses;

    private ProgressBar loadingBar;
    private AppCompatButton btnCadastrar;

    private Routes routes = new Routes();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_grupo);

        url = routes.getStringUrl( Routes.CADASTRAR_GRUPO );

        etNome          = findViewById(R.id.et_nomeNovoGrupo);
        etDescricao     = findViewById(R.id.et_descricaoNovoGrupo);
        etWebsite       = findViewById(R.id.et_websiteNovoGrupo);
        etInteresses    = findViewById(R.id.et_tagsNovoGrupo);

        loadingBar =  findViewById(R.id.pb_loadingNovoGrupo);
        btnCadastrar = findViewById(R.id.btn_cadastrarNovoGrupo);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                makeRequest();
            }
        });
    }

    private void makeRequest() {
        WebUtils webUtils = WebUtils.getInstance(getApplicationContext());

        WebRequest webRequest = new WebRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, response);
                        boolean result = false;
                        try {
                             boolean logged = new JSONObject(response).getBoolean("logged");
                             if( logged ) {
                                 result = new JSONObject(response).getBoolean("data");
                             } else {
                                 removeLoginHash();
                                 requestLogin();
                             }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finishLoading(result);
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

                String nome         = etNome.getText().toString();
                String descricao    = etDescricao.getText().toString();
                String website      = etWebsite.getText().toString();
                String tags         = etInteresses.getText().toString();


                if ( !nome.isEmpty() ) {
                    params.put("nome", nome);
                }

                if ( !descricao.isEmpty() ) {
                    params.put("descricao", descricao);
                }

                if ( !website.isEmpty() ) {
                    params.put("website", website);
                }

                if ( !tags.isEmpty() ) {
                    params.put("tags", tags);
                }

                return params;
            }
        };

        webRequest.setTag(TAG);

        webUtils.addToRequestQueue(webRequest);
    }

    private void showLoading(){
        loadingBar.setVisibility(View.VISIBLE);
        btnCadastrar.setVisibility(View.INVISIBLE);
    }

    private void finishLoading(boolean result) {
        if( result ){
            success();
        } else {
            error();
        }
    }

    private void success(){
        Toast.makeText(this, R.string.successMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    private void error() {
        loadingBar.setVisibility(View.INVISIBLE);
        btnCadastrar.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == LOGIN_REQUEST ){
            if( resultCode == RESULT_CANCELED ){
                finish();
            }
        }
    }

}
