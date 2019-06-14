package cifradrive.fatec.br.models;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import cifradrive.fatec.br.LoginActivity;
import cifradrive.fatec.br.OptionsActivity;
import cifradrive.fatec.br.R;
import cifradrive.fatec.br.utils.NetworkUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements LoginDialog.LoginDialogListener {
    protected static final int LOGIN_REQUEST = 10;
    protected SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        preferences = getApplicationContext().getSharedPreferences( getString(R.string.preferences_file), MODE_PRIVATE);

        if( !isLogged() ){
            requestLogin();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if( !isLogged() ){
            requestLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ON_DETROY", this.getClass().getSimpleName() + " has just been destroyed.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_options:
                showOptions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showOptions() {
        Intent options = new Intent(getBaseContext(), OptionsActivity.class);
        startActivity(options);
    }

    public boolean isLogged(){
        return preferences.contains( getString(R.string.hashKey) );
    }

    public void removeLoginHash(){
        SharedPreferences.Editor spEditor = preferences.edit();
        spEditor.remove( getString(R.string.hashKey) );
        spEditor.apply();
    }

    public void requestLogin(){
        DialogFragment dialog = new LoginDialog();
        dialog.show( getSupportFragmentManager(), "LoginDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if( NetworkUtils.isConnected( getBaseContext() ) ){
            Intent login = new Intent( this, LoginActivity.class );
            startActivityForResult(login, LOGIN_REQUEST);
        } else {
            Toast.makeText( getBaseContext(), R.string.notConnectedMessage, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        finish();
    }
}
