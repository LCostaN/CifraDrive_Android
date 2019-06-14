package cifradrive.fatec.br;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class OptionsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected static final int PERMISSION_EXTERNAL_FILES = 1;

    private SharedPreferences preferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.options, rootKey);
        preferences = requireActivity().getSharedPreferences( getString(R.string.preferences_file), Context.MODE_PRIVATE );

        findPreference("logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showConfirmDialog();
                return true;
            }
        });
    }

    public void logOut(){
        preferences.edit()
                .remove( getString(R.string.hashKey) )
                .remove( "id" )
                .remove( "nome" )
                .remove( "email" )
                .remove( "dataNasc" )
                .remove( "sexo" )
                .remove( "PHPSESSID")
        .apply();
        requireActivity().finish();
    }

    public void showConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
        builder.setTitle(R.string.confirmMessage)
                .setPositiveButton(R.string.sair, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logOut();
                        dialog.dismiss();
                        requireActivity().finish();
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Registers a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregisters the listener set in onStart().
        // It's best practice to unregister listeners when your app isn't using them to cut down on
        // unnecessary system overhead. You do this in onStop().
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("SHARED_PREF_CHANGED", sharedPreferences.toString() );
        Log.d("PREF_KEY_CHANGED", key );
        if ("armazenamento_externo".equals(key)) {
            if (sharedPreferences.getBoolean(key, false)) {
                if (ContextCompat.checkSelfPermission( requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions( requireActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_EXTERNAL_FILES);
                }
            }
        } else if ( "mobile_download".equals(key) ){
            if ( sharedPreferences.getBoolean(key, false) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                builder.setMessage(R.string.pref_mobile_download_warn).setTitle(R.string.confirmMessage)
                        .setPositiveButton(R.string.goToLogin, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                preferences.edit().putBoolean("mobile_download", false).apply();
                                SwitchPreference mobile = (SwitchPreference) findPreference("mobile_download");
                                mobile.setChecked(false);
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSION_EXTERNAL_FILES) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                preferences.edit().putBoolean("armazenamento_externo", true).apply();
            } else {
                preferences.edit().putBoolean("armazenamento_externo", false).apply();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}

