package cifradrive.fatec.br;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.option_container, new OptionsFragment() )
                .commit();
    }
}
