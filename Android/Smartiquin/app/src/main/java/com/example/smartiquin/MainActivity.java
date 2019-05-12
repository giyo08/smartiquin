package com.example.smartiquin;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnDesbloquear = findViewById(R.id.buttonAbrir);
    }

    public void abrirVentanaAbrir(View view){
        Intent intent = new Intent(view.getContext(),AbrirActivity.class);
        startActivity(intent);
    }
}
