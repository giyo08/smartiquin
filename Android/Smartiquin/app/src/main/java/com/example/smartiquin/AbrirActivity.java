package com.example.smartiquin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AbrirActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abrir);

        Button btnValidar = findViewById(R.id.buttonValidar);
        final Button btnAbrir = findViewById(R.id.buttonAbrir);
        TextView tvNHW = findViewById(R.id.textViewNoHW);

        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(getApplicationContext());

        ///Valido si tiene sensor de huella el celular
        if(!fingerprintManagerCompat.isHardwareDetected()){
            btnValidar.setEnabled(false);
            tvNHW.setVisibility(View.VISIBLE);
        }else
            btnAbrir.setClickable(false);

        Executor e = Executors.newSingleThreadExecutor(); //Lo creo para el biometricPrompt, no lo inicio;

        FragmentActivity activity = this;

        final BiometricPrompt bp = new BiometricPrompt(activity, e, new BiometricPrompt.AuthenticationCallback() {

            ///Error inesperado al validar huella
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            ///reconoce la huella
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                btnAbrir.setClickable(true);
            }

            ///No reconoce la huella
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Desbloqueo de botiquin")
                .setSubtitle("Verifique su identidad")
                .setDescription("Use su huella digital para verificar su identidad y poder desbloquear el botiquín.")
                .setNegativeButtonText("Cancelar")
                .build();

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.authenticate(promptInfo);
            }
        });
    }

    public void abrirBotiquin(View view){
        Toast t = Toast.makeText(getApplicationContext(),"Abriendo botiquín",Toast.LENGTH_SHORT);
        t.setGravity(Gravity.BOTTOM,0,50);
        t.show();
    }
}
