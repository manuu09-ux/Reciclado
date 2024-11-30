package com.example.reciclo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.widget.ImageView;
import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LogrosFragment extends Fragment {

    private RecyclerView recyclerView;
    private LogrosAdapter adapter;
    private List<Logro> logros;
    private int contadorEscaneos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logros, container, false);

        recyclerView = view.findViewById(R.id.recyclerLogros);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        logros = new ArrayList<>();
        inicializarLogros();
        cargarProgresoEscaneos(); // Cargar el progreso de escaneos
        cargarEstadoLogros();     // Cargar el estado de los logros

        adapter = new LogrosAdapter(logros);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void inicializarLogros() {
        // Agregar logros relacionados a escaneos
        logros.add(new Logro("Escanea un producto por primera vez", "Escanea cualquier producto por primera vez para obtener tu primer logro.", 0));
        logros.add(new Logro("Escanea 10 productos", "Escanea un total de 10 productos para desbloquear este logro.", 0));
        logros.add(new Logro("Escanea 20 productos", "Escanea un total de 20 productos para ganar este logro.", 0));
        logros.add(new Logro("Escanea 50 productos", "¡Un maestro del escaneo! Completa 50 escaneos para ganar este logro.", 0));

        // Otros logros (mantén tu lógica aquí)
        // ...
    }

    public void registrarEscaneo(boolean reciclable) {
        contadorEscaneos++;
        guardarProgresoEscaneos();

        // Actualizar logros basados en la cantidad de escaneos
        if (contadorEscaneos == 1) {
            actualizarLogroPorNombre("Escanea un producto por primera vez");
        } else if (contadorEscaneos == 10) {
            actualizarLogroPorNombre("Escanea 10 productos");
        } else if (contadorEscaneos == 20) {
            actualizarLogroPorNombre("Escanea 20 productos");
        } else if (contadorEscaneos == 50) {
            actualizarLogroPorNombre("Escanea 50 productos");
        }

        // Si el producto es reciclable, actualiza logros relacionados con reciclaje
        if (reciclable) {
            // Lógica adicional para logros de reciclaje (si aplica)
        }
    }

    private void guardarProgresoEscaneos() {
        SharedPreferences prefs = getActivity().getSharedPreferences("ProgresoPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("contadorEscaneos", contadorEscaneos);
        editor.apply();
    }

    private void cargarProgresoEscaneos() {
        SharedPreferences prefs = getActivity().getSharedPreferences("ProgresoPrefs", getActivity().MODE_PRIVATE);
        contadorEscaneos = prefs.getInt("contadorEscaneos", 0);
    }

    public void actualizarLogroPorNombre(String nombreLogro) {
        for (int i = 0; i < logros.size(); i++) {
            Logro logro = logros.get(i);
            if (logro.getNombre().equals(nombreLogro) && !logro.isAlcanzado()) {
                logro.marcarComoAlcanzado();
                adapter.notifyItemChanged(i);  // Actualiza solo el item que ha cambiado
                guardarEstadoLogros();        // Guarda el estado actualizado
                mostrarNotificacionDeLogro(logro.getNombre(), logro); // Muestra una notificación
                break;
            }
        }
    }

    private void mostrarNotificacionDeLogro(String nombreLogro, Logro logro) {
        Toast.makeText(getContext(), "¡Felicidades! Has desbloqueado el logro: " + nombreLogro, Toast.LENGTH_LONG).show();

        // Animación para destacar el logro desbloqueado
        View itemView = recyclerView.findViewHolderForAdapterPosition(logros.indexOf(logro)).itemView;
        if (itemView != null) {
            ImageView iconoLogro = itemView.findViewById(R.id.icono_logro);
            ObjectAnimator animation = ObjectAnimator.ofFloat(iconoLogro, "rotation", 0f, 360f);
            animation.setDuration(1000);
            animation.start();
        }
    }

    private void guardarEstadoLogros() {
        SharedPreferences prefs = getActivity().getSharedPreferences("LogrosPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < logros.size(); i++) {
            editor.putBoolean("logro_" + i, logros.get(i).isAlcanzado());
        }
        editor.apply();
    }

    private void cargarEstadoLogros() {
        SharedPreferences prefs = getActivity().getSharedPreferences("LogrosPrefs", getActivity().MODE_PRIVATE);
        for (int i = 0; i < logros.size(); i++) {
            boolean alcanzado = prefs.getBoolean("logro_" + i, false);
            logros.get(i).setAlcanzado(alcanzado);
        }
    }
}