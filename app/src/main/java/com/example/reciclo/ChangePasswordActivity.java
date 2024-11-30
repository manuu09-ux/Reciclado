package com.example.reciclo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextNewPassword;
    private Button buttonSavePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonSavePassword = findViewById(R.id.buttonSavePassword);

        buttonSavePassword.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString().trim();
            if (!newPassword.isEmpty()) {
                // Lógica para cambiar la contraseña en la base de datos
                Toast.makeText(this, "Contraseña cambiada", Toast.LENGTH_SHORT).show();
                finish(); // Regresar a SettingsFragment
            } else {
                Toast.makeText(this, "Por favor ingrese una nueva contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }
}