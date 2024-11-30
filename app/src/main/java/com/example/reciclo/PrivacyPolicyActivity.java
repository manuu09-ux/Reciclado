package com.example.reciclo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Configurar la Toolbar como barra de acción
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Política de Privacidad"); // Título de la barra
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Habilitar botón de regreso
        }

        // Configurar el texto de la política de privacidad
        TextView textViewPrivacyPolicy = findViewById(R.id.textViewPrivacyPolicy);
        textViewPrivacyPolicy.setText(getPrivacyPolicyText()); // Establecer el texto
    }

    /**
     * Método que retorna el texto de la política de privacidad.
     * Puedes personalizarlo según tu necesidad.
     */
    private String getPrivacyPolicyText() {
        return "Política de Privacidad\n\n" +
                "1. Introducción\n" +
                "Bienvenido a Reciclo. Respetamos su privacidad y nos comprometemos a protegerla.\n\n" +
                "2. Información recopilada\n" +
                "- Datos que usted proporciona.\n" +
                "- Información de uso de la aplicación.\n\n" +
                "3. Uso de la información\n" +
                "Usamos sus datos para mejorar nuestros servicios y su experiencia.\n\n" +
                "4. Seguridad\n" +
                "Tomamos medidas razonables para proteger su información.\n\n" +
                "5. Contáctenos\n" +
                "Si tiene preguntas, escríbanos a: soporte@reciclo.com.\n\n" +
                "Gracias por confiar en nosotros.";
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Hacer que el botón de regreso cierre esta actividad
        finish();
        return true;
    }
}