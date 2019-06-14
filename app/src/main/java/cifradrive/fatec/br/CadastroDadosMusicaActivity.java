package cifradrive.fatec.br;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CadastroDadosMusicaActivity extends BaseActivity {
    private final int ARQUIVO_MUSICA_REQUEST = 9;
    private final String TAG = "NOVA_CIFRA_";

    private AutoCompleteTextView etMusica;
    private EditText etVersao;
    private EditText etTags;
    private Spinner spTom;

    private Routes routes = new Routes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_dados_musica);

        etMusica = findViewById(R.id.actv_nomeNovaMusica);
        etVersao = findViewById(R.id.et_versaoNovaMusica);
        etTags   = findViewById(R.id.et_tagsNovaMusica);
        spTom    = findViewById(R.id.sp_tomOriginal);

        getNomesMusicas();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.tons, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTom.setAdapter(spinnerAdapter);

        AppCompatButton btnAdicionarArquivo = findViewById(R.id.btn_arquivoNovaMusica);

        btnAdicionarArquivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( checkRequiredFields() ){
                    Intent arquivoMusica = new Intent(CadastroDadosMusicaActivity.this, CadastroArquivoMusicaActivity.class);
                    arquivoMusica.putExtra("nome", etMusica.getText().toString() );
                    arquivoMusica.putExtra("versao", etVersao.getText().toString() );
                    arquivoMusica.putExtra("tom", spTom.getSelectedItem().toString() );
                    arquivoMusica.putExtra("tags", etTags.getText().toString() );
                    startActivityForResult(arquivoMusica, ARQUIVO_MUSICA_REQUEST);
                } else {
                    showMissingArgumentsMessage();
                }
            }
        });
    }

    private void getNomesMusicas(){
        if( NetworkUtils.isConnected( getBaseContext() ) ){
            WebUtils webUtils = WebUtils.getInstance( getApplicationContext() );
            String urlNomesMusicas = routes.getStringUrl(Routes.GET_MUSICAS_NOMES);
            Log.v("URL_AUTOCOMPLETE", urlNomesMusicas);

            WebRequest webRequest = new WebRequest
                    (Request.Method.GET, urlNomesMusicas, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG+"NOMES", response);
                            try {
                                String[] musicas = new JSONObject(response).getString("data").split(",");

                                ArrayAdapter<String> adapter =
                                        new ArrayAdapter<>( getBaseContext(), android.R.layout.simple_list_item_1, musicas);
                                etMusica.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG + "_ERROR", error.getMessage());
                        }
                    });
            webRequest.setTag(TAG);

            webUtils.addToRequestQueue(webRequest);
        }
    }

    private boolean checkRequiredFields(){
        boolean nome         = etMusica.getText().toString().isEmpty();
        boolean versao       = etVersao.getText().toString().isEmpty();
        boolean tomOriginal  = spTom.getSelectedItem().toString().isEmpty();

        return !nome && !versao && !tomOriginal;
    }

    private void showMissingArgumentsMessage(){
        Toast.makeText(this, R.string.requiredMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == ARQUIVO_MUSICA_REQUEST){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
    }
}
