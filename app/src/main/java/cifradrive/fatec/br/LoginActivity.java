package cifradrive.fatec.br;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {
    // Constants
    private final String TAG = "LOGIN";
    private static final int SUB_ACTIVITY_CREATE_USER = 20;

    // UI Elements
    private EditText email;
    private EditText senha;

    private ProgressBar loadingBar;
    private Button entrar;
    private Button cadastro;

    // Utils
    private String url;
    private Routes routes = new Routes();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        preferences = getApplicationContext().getSharedPreferences( getString(R.string.preferences_file), MODE_PRIVATE);

        url = routes.getStringUrl(Routes.LOGIN);
        Log.v("URL", url);

        email = findViewById(R.id.et_emailLogin);
        senha = findViewById(R.id.et_senhaLogin);
        CheckBox mostrarSenha = findViewById(R.id.cb_mostrarSenhaLogin);

        entrar      = findViewById(R.id.btn_login);
        cadastro    = findViewById(R.id.btn_novoUsuario);
        loadingBar  = findViewById(R.id.pb_loadingLogin);

        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
                startActivityForResult(intent, SUB_ACTIVITY_CREATE_USER);
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendEmail = email.getText().toString();
                String sendSenha = senha.getText().toString();

                if( !sendEmail.isEmpty() && !sendSenha.isEmpty() ) {
                    login(sendEmail, sendSenha, null);
                } else {
                    emptyFields();
                }
            }
        });

        mostrarSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    senha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void login(final String email, final String senha, final String hash){
        WebUtils webUtils = WebUtils.getInstance( getApplicationContext() );

        WebRequest webRequest = new WebRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, response);
                        try {
                            JSONObject result = new JSONObject(response);
                            result = result.getJSONObject("data");
                            if( result.getBoolean("ok") ){
                                success( result.getString("hash") );
                            } else {
                                error();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage());
                    }
                }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    if( hash != null){
                        params.put("hash", hash);
                    } else {
                        params.put("email", email );
                        params.put("senha", senha );
                    }

                    Log.v(TAG+"_PARAMS", params.toString());

                    return params;
                }
        };

        webRequest.setTag(TAG);
        webUtils.addToRequestQueue(webRequest);
    }

    private void emptyFields(){
        Toast.makeText(this, R.string.requiredMessage, Toast.LENGTH_SHORT).show();
    }

    private void success(String hash){
        Intent data = new Intent();
        data.putExtra("logado", true);

        setResult(Activity.RESULT_OK, data);

        SharedPreferences.Editor pref = preferences.edit();
        pref.putString( getString(R.string.hashKey), hash);
        pref.apply();

        Toast.makeText(getBaseContext(), "Logado com sucesso!", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void error() {
        loadingBar.setVisibility(View.INVISIBLE);
        entrar.setVisibility(View.VISIBLE);
        cadastro.setVisibility(View.VISIBLE);

        if( preferences.contains(getString(R.string.hashKey)) ){
            SharedPreferences.Editor prefEdit = preferences.edit();
            prefEdit.remove( getString(R.string.hashKey) );
            prefEdit.apply();
        }

        Toast.makeText( getBaseContext(), R.string.errorLoginMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SUB_ACTIVITY_CREATE_USER) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String email = data.getStringExtra("email");
                String senha = data.getStringExtra("senha");

                login(email, senha, null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED); finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String hash = preferences.getString( getString(R.string.hashKey), "" );
        assert hash != null;
        if ( !hash.isEmpty() ){
              login(null, null, hash);
        }
    }
}