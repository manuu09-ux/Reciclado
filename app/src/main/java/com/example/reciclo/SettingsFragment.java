package com.example.reciclo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private TextView changeEmailText, changePasswordText, privacyPolicyText;
    private Switch switchNotifications;
    private Button logoutButton;

    public SettingsFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Obtener las referencias de los elementos
        changeEmailText = view.findViewById(R.id.changeEmailText);
        changePasswordText = view.findViewById(R.id.changePasswordText);
        privacyPolicyText = view.findViewById(R.id.privacyPolicyText);
        switchNotifications = view.findViewById(R.id.switchNotifications);

        // Establecer listeners o configuraciones

        // Cambiar correo electrónico
        changeEmailText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeEmailActivity.class);
            startActivity(intent);
        });

        // Cambiar contraseña
        changePasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Ver política de privacidad
        privacyPolicyText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        // Configurar notificaciones
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Aquí puedes agregar la lógica para habilitar o deshabilitar las notificaciones
            if (isChecked) {
                // Habilitar notificaciones
            } else {
                // Deshabilitar notificaciones
            }
        });


        return view;
    }
}