package cifradrive.fatec.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import cifradrive.fatec.br.models.WebRequest;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.WebUtils;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private final String TAG = "NOVO_USUARIO";

    private EditText etNome;
    private EditText etEmail;
    private EditText etSenha;
    private EditText etConfirm;

    private ProgressBar loadingBar;
    private AppCompatButton btnCadastrar;

    private Routes routes = new Routes();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_usuario);

        url = routes.getStringUrl( Routes.CADASTRAR_USUARIO );

        etNome      = findViewById(R.id.et_nomeNovoUsuario);
        etEmail     = findViewById(R.id.et_emailNovoUsuario);
        etSenha     = findViewById(R.id.et_senhaNovoUsuario);
        etConfirm   = findViewById(R.id.et_confirmarSenhaNovoUsuario);

        CheckBox cbMostrarSenha = findViewById(R.id.cb_mostrarSenhaNovoUsuario);

        loadingBar   = findViewById(R.id.pb_loadingNovoUsuario);
        btnCadastrar = findViewById(R.id.btn_cadastrarUsuario);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email    = etEmail.getText().toString();
                String nome     = etNome.getText().toString();
                String senha    = etSenha.getText().toString();
                String confirm  = etConfirm.getText().toString();

                if( !nome.isEmpty() && !email.isEmpty() && !senha.isEmpty() && !confirm.isEmpty() ){
                    showLoading();
                    makeRequest(email, nome, senha, confirm);
                } else {
                    emptyFields();
                }
            }
        });

        cbMostrarSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    etSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void makeRequest(final String email, final String nome, final String senha, final String confirm) {
        WebUtils webUtils = WebUtils.getInstance(getApplicationContext());

        WebRequest webRequest = new WebRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, response);
                        int result = 0;
                        try {
                            result = new JSONObject(response).getInt("data");
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

                    params.put("nome", nome);
                    params.put("email", email);
                    params.put("senha", senha);
                    params.put("confirm", confirm);

                    Log.v(TAG+"_PARAMS", params.toString() );

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

    private void emptyFields(){
        Toast.makeText(this, R.string.requiredMessage, Toast.LENGTH_LONG).show();
    }

    private void finishLoading(int result) {
        switch ( result ){
            case 1:
                success();
                break;
            case 0:
                confirmError();
                break;
            case -1:
                duplicateError();
                break;
            default:
                error();
        }
    }

    private void success(){
        Toast.makeText(this, R.string.successMessage, Toast.LENGTH_LONG).show();
        String email    = etEmail.getText().toString();
        String senha    = etSenha.getText().toString();

        Intent result = new Intent();
        result.putExtra("email", email);
        result.putExtra("senha", senha);

        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void error() {
        loadingBar.setVisibility(View.INVISIBLE);
        btnCadastrar.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_LONG).show();
    }

    private void confirmError() {
        loadingBar.setVisibility(View.INVISIBLE);
        btnCadastrar.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.errorConfirmMessage, Toast.LENGTH_LONG).show();
    }

    private void duplicateError() {
        loadingBar.setVisibility(View.INVISIBLE);
        btnCadastrar.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.errorDuplicateMessage, Toast.LENGTH_LONG).show();
    }
}