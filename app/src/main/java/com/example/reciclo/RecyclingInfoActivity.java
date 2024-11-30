package com.example.reciclo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecyclingInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_info);

        // Aquí puedes obtener el nombre del producto desde el Intent si lo necesitas
        TextView productName = findViewById(R.id.product_name);
        TextView recyclingInfo = findViewById(R.id.recycling_info);

        // Llenar la información con un ejemplo (puedes hacerlo dinámico según lo que el usuario seleccione)
        productName.setText("Botella de Plástico");
        recyclingInfo.setText("Las botellas de plástico pueden reciclarse en centros de reciclaje especializados.");
    }
}