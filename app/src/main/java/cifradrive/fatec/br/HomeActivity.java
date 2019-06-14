package cifradrive.fatec.br;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import cifradrive.fatec.br.models.BaseActivity;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.home);
        super.onCreate(savedInstanceState);

        ImageButton btn_profile = findViewById(R.id.btn_profile);
        ImageButton btn_cifra = findViewById(R.id.btn_cifras);
        ImageButton btn_grupos = findViewById(R.id.btn_grupos);

        btn_cifra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ListaMusicaActivity.class);
                startActivity(intent);
            }
        });

        btn_grupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ListaGrupoActivity.class);
                startActivity(intent);
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == LOGIN_REQUEST){
            Log.d("HOME_VARIABLES", String.valueOf(resultCode) );
            if( resultCode != RESULT_OK){
                finish();
            }
        }
    }
}
