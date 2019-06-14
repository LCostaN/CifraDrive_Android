package cifradrive.fatec.br;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cifradrive.fatec.br.models.BaseActivity;
import cifradrive.fatec.br.models.WebRequest;
import cifradrive.fatec.br.models.Routes;
import cifradrive.fatec.br.utils.NetworkUtils;
import cifradrive.fatec.br.utils.WebUtils;

public class PerfilActivity extends BaseActivity {
    private final int PICK_FOTO_PERFIL = 11;
    private final String TAG = "PERFIL";

    private ImageView ivFoto;
    private TextView tvEmail;
    private EditText etNome;
    private EditText etDataNascimento;
    private Spinner spGenero;

    private AppCompatButton btnSalvar;
    private ProgressBar loadingBar;

    private Routes routes = new Routes();
    private String url;

    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        url = routes.getStringUrl( Routes.PERFIL );

        ivFoto           = findViewById(R.id.iv_fotoPerfil);
        tvEmail          = findViewById(R.id.tv_email);
        etNome           = findViewById(R.id.et_nome);
        spGenero         = findViewById(R.id.sp_sexo);
        etDataNascimento = findViewById(R.id.et_dataNasc);

        loadingBar =  findViewById(R.id.pb_loadingPerfil);
        btnSalvar = findViewById(R.id.btn_salvarPerfil);
        AppCompatButton btnAlterarFoto = findViewById(R.id.btn_selecionarFotoUsuario);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sexos, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenero.setAdapter(adapter);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        etDataNascimento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(PerfilActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                salvarDados();
            }
        });

        btnAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filePick = new Intent(Intent.ACTION_GET_CONTENT);
                filePick.addCategory(Intent.CATEGORY_OPENABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    filePick.setType("*/*");
                    String[] mimetypes = {"image/png", "image/jpeg"};
                    filePick.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                } else {
                    filePick.setType("image/*");
                }
                startActivityForResult(filePick, PICK_FOTO_PERFIL);
            }
        });
        getDados();
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        etDataNascimento.setText( sdf.format( myCalendar.getTime() ) );
    }

    private void getDados(){
        if( NetworkUtils.isConnected(getBaseContext()) ) {
            WebUtils webUtils = WebUtils.getInstance(getApplicationContext());

            WebRequest webRequest = new WebRequest
                    (Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, response);
                            try {
                                JSONObject result = new JSONObject(response);
                                boolean success = result.getBoolean("logged");
                                if (success) {
                                    JSONObject usuario = result.getJSONObject("data");
                                    showDados(usuario);
                                    updatePerfilLocal(usuario);
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
            showPerfilFromLocal();
        }
    }

    private void updatePerfilLocal(JSONObject usuario){
        try {
            int id = usuario.getInt("id");

            String email    = usuario.getString("email");
            String nome     = usuario.getString("nome");
            String dataNasc = usuario.getString("dataNasc");
            String sexo     = usuario.getString("sexo");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("id", id);
            editor.putString("email", email);
            editor.putString("nome", nome);
            editor.putString("dataNasc", dataNasc);
            editor.putString("sexo", sexo);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG+"_LOCAL", e.getMessage() );
            Toast.makeText(this, "Sem dados de Perfil", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showPerfilFromLocal(){
        String email    = preferences.getString("email", "");
        String nome     = preferences.getString("nome", "");
        String dataNasc = preferences.getString("dataNasc", "");
        String sexoPref = preferences.getString("sexo", null);
        int sexo = 0;
        if( sexoPref != null ){
            sexo = sexoPref.equalsIgnoreCase("m") ? 1 : 2;
        }

        tvEmail.setText(email);
        etNome.setText(nome);
        spGenero.setSelection(sexo);

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault() );
            Date date = format.parse(dataNasc);
            myCalendar.setTime( date );

            updateLabel();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("DATE_FORMAT_PERFIL", e.getMessage() );
        }
    }

    private void showDados(JSONObject usuario){
        try{
            String email    = usuario.getString("email");
            String nome     = usuario.getString("nome");
            String dataNasc = usuario.getString("dataNasc");
            String sexoUsuario = usuario.getString("sexo");
            int sexo = 0;
            if( sexoUsuario != null ){
                sexo = sexoUsuario.equalsIgnoreCase("m") ? 1 : 2;
            }

            tvEmail.setText(email);
            etNome.setText(nome);
            spGenero.setSelection(sexo);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault() );
            Date date = format.parse(dataNasc);
            myCalendar.setTime( date );

            updateLabel();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void salvarDados() {
        if( NetworkUtils.isConnected(getBaseContext())){
            WebUtils webUtils = WebUtils.getInstance(getApplicationContext());

            WebRequest webRequest = new WebRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v(TAG, response);
                            boolean result = false;
                            try {
                                JSONObject json = new JSONObject(response);
                                boolean success = json.getBoolean("logged");
                                if(success){
                                    result = json.getBoolean("data");
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
                    String sexo         = spGenero.getSelectedItem().toString().substring(0,1);
                    String dataNasc     = etDataNascimento.getText().toString();

                    if ( !nome.isEmpty() ) {
                        params.put("nome", nome);

                        if ( !sexo.isEmpty() ) {
                            params.put("sexo", sexo.toLowerCase() );
                        }

                        if ( !dataNasc.isEmpty() ) {
                            params.put("dataNasc", dataNasc);
                        }
                    }
                    Log.v(TAG+"_PARAMS", params.toString() );
                    return params;
                }
            };

            webRequest.setTag(TAG);
            webUtils.addToRequestQueue(webRequest);
        } else {
            Toast.makeText(this, R.string.notConnectedMessage, Toast.LENGTH_SHORT).show();
            loadingBar.setVisibility(View.INVISIBLE);
            btnSalvar.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(){
        loadingBar.setVisibility(View.VISIBLE);
        btnSalvar.setVisibility(View.INVISIBLE);
    }

    private void finishLoading(boolean result) {
        if( result ){
            success();
        } else {
            error();
        }
    }

    private void success(){
        String nome         = etNome.getText().toString();
        String sexo         = spGenero.getSelectedItem().toString().substring(0,1);
        String dataNasc     = etDataNascimento.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nome", nome);
        editor.putString("dataNasc", dataNasc);
        editor.putString("sexo", sexo);
        editor.apply();

        Toast.makeText(this, R.string.successMessage, Toast.LENGTH_LONG).show();
        loadingBar.setVisibility(View.INVISIBLE);
        btnSalvar.setVisibility(View.VISIBLE);
    }

    private void error() {
        Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_LONG).show();
        loadingBar.setVisibility(View.INVISIBLE);
        btnSalvar.setVisibility(View.VISIBLE);
    }

    private void setFoto(Uri uri){
        ivFoto.setImageURI(uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_FOTO_PERFIL){
            if(resultCode == RESULT_OK){
                assert data != null;
                Uri fotoContent = data.getData();
                Log.d("PERFIL_FOTO_PICK", String.valueOf(fotoContent) );

                setFoto(fotoContent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
