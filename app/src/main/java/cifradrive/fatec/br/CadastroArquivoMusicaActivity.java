package cifradrive.fatec.br;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
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
import cifradrive.fatec.br.utils.LocalDataUtils;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.WebUtils;

public class CadastroArquivoMusicaActivity extends BaseActivity {
    private final int FILE_PICK_REQUEST_CODE = 11;
    private final String TAG = "NOVA_CIFRA_";

    private LocalDataUtils localSource;
    private WebView wvArquivoCifra;

    private Routes routes = new Routes();
    private String url;

    private String musica;
    private String versaoMusica;
    private String tom;
    private String tagsVersao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_dados_musica);

        url = routes.getStringUrl( Routes.CADASTRAR_ARQUIVO_MUSICA );
        localSource = new LocalDataUtils( getBaseContext() );

        Intent sent = getIntent();

        musica       = sent.getStringExtra("nome");
        versaoMusica = sent.getStringExtra("versao");
        tagsVersao   = sent.getStringExtra("tags");
        tom          = sent.getStringExtra("tom");

//        btnCadastrar = findViewById(R.id.btn_novaMusica);

//        AppCompatButton btnArquivoMusica = findViewById(R.id.btn_selecionarArquivo);
//        btnArquivoMusica.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent filePick = new Intent(Intent.ACTION_GET_CONTENT);
//                filePick.addCategory(Intent.CATEGORY_OPENABLE);
//                filePick.setType("application/pdf");
//
//                startActivityForResult(filePick, FILE_PICK_REQUEST_CODE);
//            }
//        });

//        btnCadastrar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if( checkRequiredFields() ){
//                    showLoading();
//                    dadosRequest();
//                } else {
//                    showMissingArgumentsMessage();
//                }
//            }
//        });
    }

    private void dadosRequest() {
        WebUtils webUtils = WebUtils.getInstance( getApplicationContext() );

        WebRequest webRequest = new WebRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, response);
                        boolean result = false;
                        try {
                            result = new JSONObject(response).getBoolean("data");
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
                
                String nome         = musica;
                String versao       = versaoMusica;
                String tomOriginal  = tom;
                String tags         = tagsVersao;


                params.put("nome", nome);
                params.put("versao", versao);
                params.put("tomOriginal", tomOriginal);
                params.put("tags", tags);

                return params;
            }
        };

        webRequest.setTag(TAG);

        webUtils.addToRequestQueue(webRequest);
    }

//    private void uploadRequest(){
//
//    }

    private void finishLoading(boolean result) {
        if( result ){
            success();
        } else {
            error();
        }
    }

    private void success(){
        Toast.makeText(this, R.string.successMessage, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    private void error() {
        Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
