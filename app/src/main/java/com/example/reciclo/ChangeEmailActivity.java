package com.example.reciclo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText editTextNewEmail;
    private Button buttonSaveEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        editTextNewEmail = findViewById(R.id.editTextNewEmail);
        buttonSaveEmail = findViewById(R.id.buttonSaveEmail);

        buttonSaveEmail.setOnClickListener(v -> {
            String newEmail = editTextNewEmail.getText().toString().trim();
            if (!newEmail.isEmpty()) {
                // Lógica para cambiar el correo en la base de datos o autenticación
                Toast.makeText(this, "Correo electrónico cambiado", Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad y regresa a SettingsFragment
            } else {
                Toast.makeText(this, "Por favor ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}