package cifradrive.fatec.br;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import cifradrive.fatec.br.utils.NetworkUtils;
import cifradrive.fatec.br.utils.WebUtils;

public class VerGrupoActivity extends BaseActivity {
//    @Constants
    final String TAG = "VER_GRUPO";

//    @Info Variables
    private int lider;

//    @Variables
    private int idGrupo;
    private String nome;
    private String descricao;
    private String website;
    private String tags;

//    @UI Elements
    private ImageView ivFoto;
    private EditText etNome;
    private EditText etDescricao;
    private EditText etWebsite;
    private EditText etTags;

//    @Buttons
    private AppCompatButton btnAlterarFoto;
    private AppCompatButton btnSalvar;
    private AppCompatButton btnCancelar;

//    Helpers
    private Routes routes = new Routes();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_grupo);

        ivFoto      = findViewById(R.id.iv_fotoGrupo);
        etNome      = findViewById(R.id.et_nomeGrupo);
        etDescricao = findViewById(R.id.et_descricaoGrupo);
        etWebsite   = findViewById(R.id.et_websiteGrupo);
        etTags      = findViewById(R.id.et_tagsGrupo);

        btnAlterarFoto  = findViewById(R.id.btn_alterarFotoGrupo);
        btnSalvar       = findViewById(R.id.btn_confirmarEditarGrupo);
        btnCancelar     = findViewById(R.id.btn_cancelarEditarGrupo);

        Intent intent = getIntent();
        idGrupo = intent.getIntExtra("idGrupo", 0);

        url = routes.getStringUrl(Routes.VER_GRUPO, idGrupo);
        Log.d("URL", url);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarGrupo();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });

        etWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !etWebsite.isEnabled() ){
                    Intent web = new Intent(Intent.ACTION_VIEW);
                    web.setData(Uri.parse( website ));
                    startActivity(web);
                }
            }
        });

        viewMode();
        getDados();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ver_grupo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.menu_editar_grupo:
                editMode();
                return true;
            case R.id.menu_entrar:
                entrarNoGrupo();
                return true;
            case R.id.menu_sair:
                sairDoGrupo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getDados(){
        if(NetworkUtils.isConnected(getBaseContext()) ){
            WebUtils webUtils = WebUtils.getInstance(getApplicationContext());

            WebRequest webRequest = new WebRequest
                    (Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, response);
                            try {
                                JSONObject result = new JSONObject( response ).getJSONObject("data");
                                lider        = result.getInt("lider");
                                nome         = result.getString("nome");
                                descricao    = result.getString("descricao");
                                website      = result.getString("website");
                                tags         = result.getString("tags");

                                //TODO: add preferences about downloading images.
                                getImage();
                                preencherViews();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                error();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.getMessage() );
                            error();
                        }
                    });

            webRequest.setTag(TAG);

            webUtils.addToRequestQueue(webRequest);
        } else {
            //TODO: check and get data from file.

        }
    }

    private void getImage() {
        //TODO: Send image Request to get photo;
    }

    public void preencherViews(){
        setTitle( nome );

        // TODO:Set Image
        ivFoto.setImageResource(R.mipmap.group);
        etNome.setText(nome);
        etDescricao.setText(descricao);
        etWebsite.setText(website);
        etTags.setText(tags);
    }

    public void editarGrupo(){
        WebUtils webUtils = WebUtils.getInstance(getApplicationContext());
        String urlPost = routes.getStringUrl(Routes.ATUALIZAR_GRUPO, idGrupo);

        WebRequest webRequest = new WebRequest
                (Request.Method.POST, urlPost, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, response);
                        try {
                            boolean result = new JSONObject( response ).getBoolean("data");
                            if( result ){
                                viewMode();
                            } else {
                                cancelar();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cancelar();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage() );
                    }
                }) {
                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        // TODO: Image

                        String nome         = etNome.getText().toString();
                        String descricao    = etDescricao.getText().toString();
                        String website      = etWebsite.getText().toString();
                        String tags         = etTags.getText().toString();

                        if( nome.isEmpty() ){
                            params.put("nome", nome);
                        }
                        if( descricao.isEmpty() ){
                            params.put("descricao", descricao);
                        }
                        if( website.isEmpty() ){
                            params.put("website", website);
                        }
                        if( tags.isEmpty() ){
                            params.put("tags", tags);
                        }

                        return params;
                    }
                };

        webRequest.setTag(TAG);

        webUtils.addToRequestQueue(webRequest);
    }

    public void cancelar(){
        ivFoto.setImageResource(R.mipmap.group);
        etNome.setText(nome);
        etDescricao.setText(descricao);
        etWebsite.setText(website);
        etTags.setText(tags);

        viewMode();
    }

    public void viewMode(){
        etNome.setTag(etNome.getKeyListener());
        etNome.setKeyListener(null);

        etDescricao.setTag(etDescricao.getKeyListener());
        etDescricao.setKeyListener(null);

        etWebsite.setTag(etWebsite.getKeyListener());
        etWebsite.setKeyListener(null);

        etTags.setTag(etTags.getKeyListener());
        etTags.setKeyListener(null);

        btnAlterarFoto.setVisibility(View.GONE);
        btnSalvar.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.GONE);
    }

    public void editMode(){
        etNome.setKeyListener( (KeyListener)etNome.getTag());
        etDescricao.setKeyListener( (KeyListener)etDescricao.getTag());
        etWebsite.setKeyListener( (KeyListener)etWebsite.getTag());
        etTags.setKeyListener( (KeyListener)etTags.getTag());

        btnAlterarFoto.setVisibility(View.VISIBLE);
        btnSalvar.setVisibility(View.VISIBLE);
        btnCancelar.setVisibility(View.VISIBLE);
    }

    public void entrarNoGrupo(){

    }

    public void sairDoGrupo(){

    }

    public void error(){
        Toast.makeText(VerGrupoActivity.this, R.string.errorMessage, Toast.LENGTH_SHORT).show();
        finish();
    }
}