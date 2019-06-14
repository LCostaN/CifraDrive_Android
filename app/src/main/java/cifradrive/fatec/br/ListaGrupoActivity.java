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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import cifradrive.fatec.br.adapters.GroupListAdapter;
import cifradrive.fatec.br.models.BaseActivity;
import cifradrive.fatec.br.models.WebRequest;
import cifradrive.fatec.br.utils.NetworkUtils;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.WebUtils;

public class ListaGrupoActivity extends BaseActivity implements GroupListAdapter.GroupListHandler {

    private final String TAG = "LISTA_GRUPOS";

    private EditText procurarGrupo;
    private RecyclerView listaGrupos;
    private TextView respostaVazia;

    private Routes routes = new Routes();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_grupo);

        ImageButton adicionarGrupo = findViewById(R.id.ib_addGrupo);
        RadioGroup rg_grupos       = findViewById(R.id.rg_grupos);
        final RadioButton rb_buscarGrupos = findViewById(R.id.rb_buscarGrupos);

        procurarGrupo = findViewById(R.id.et_procurarGrupo);
        respostaVazia = findViewById(R.id.tv_nenhumGrupo);

        url = routes.getStringUrl( Routes.MEUS_GRUPOS );

        listaGrupos = findViewById(R.id.rv_groupList);
        listaGrupos.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listaGrupos.setLayoutManager(layoutManager);

        adicionarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent novoGrupo = new Intent(ListaGrupoActivity.this, CadastroGrupoActivity.class);
                startActivity(novoGrupo);
            }
        });

        rg_grupos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if( rb_buscarGrupos.isChecked() ){
                    url = routes.getStringUrl( Routes.BUSCAR_GRUPOS );
                    procurarGrupo.setEnabled(true);
                } else {
                    url = routes.getStringUrl( Routes.MEUS_GRUPOS );
                    procurarGrupo.setEnabled(false);
                }
                getDados();
            }
        });

        procurarGrupo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getDados();

                    v.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        rg_grupos.check(R.id.rb_meusGrupos);
        getDados();
    }

    @Override
    public void onClick(int id) {
        if( NetworkUtils.isConnected(getBaseContext()) ){
            Intent details = new Intent(ListaGrupoActivity.this, VerGrupoActivity.class);
            details.putExtra("idGrupo", id);
            startActivity(details);
        } else {
            Toast.makeText(getBaseContext(), R.string.notConnectedMessage, Toast.LENGTH_SHORT).show();
        }
    }


    public void getDados() {
        if( NetworkUtils.isConnected(getBaseContext()) ){
            WebUtils webUtils = WebUtils.getInstance( getBaseContext() );

            WebRequest webRequest = new WebRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, response);
                            try {
                                JSONObject result = new JSONObject(response);
                                boolean logado = result.getBoolean("logged");
                                if(logado){
                                    JSONArray lista = result.getJSONArray("data");
                                    showList(lista);
                                } else {
                                    removeLoginHash();
                                    requestLogin();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                noResults();
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

                    String search = procurarGrupo.getText().toString();

                    if ( !search.isEmpty() ) {
                        params.put("buscarNome", search);
                    }

                    return params;
                }
            };

            webRequest.setTag(TAG);

            webUtils.addToRequestQueue(webRequest);
        } else {
            // TODO: Search local data
        }
    }

    public void showList(JSONArray dados) {
        listaGrupos.setVisibility(View.VISIBLE);
        respostaVazia.setVisibility(View.INVISIBLE);
        if (dados != null) {
            try {
                if (dados.length() == 0) {
                    noResults();
                } else {
                    String[] nomes = new String[dados.length()];
                    String[] subText = new String[dados.length()];
                    int[] ids = new int[dados.length()];

                    for (int i = 0; i < dados.length(); i++) {
                        JSONObject obj = dados.getJSONObject(i);

                        nomes[i] = obj.getString("nome");
                        subText[i] = obj.getString("tags");
                        ids[i] = obj.getInt("id");
                    }

                    RecyclerView.Adapter listaAdapter = new GroupListAdapter(nomes, subText, ids, this);
                    listaGrupos.setAdapter(listaAdapter);
                    showResults();
                }
            } catch (JSONException e) {
                Log.e("JSON_DATA", e.toString());
            }
        } else {
            Log.e("NULL_LIST", "LISTA SEM ELEMENTOS OU VALOR NULO");
        }
    }

    public void noResults() {
        listaGrupos.setVisibility(View.INVISIBLE);
        respostaVazia.setVisibility(View.VISIBLE);
    }

    public void showResults(){
        listaGrupos.setVisibility(View.VISIBLE);
        respostaVazia.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == LOGIN_REQUEST){
            if( resultCode == RESULT_OK){
                getDados();
            }
            if( resultCode == RESULT_CANCELED ){
                Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}