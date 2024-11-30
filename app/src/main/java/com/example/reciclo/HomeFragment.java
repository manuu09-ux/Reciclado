package com.example.reciclo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RecommendationAdapter adapter;
    private List<Recommendation> recommendationList;
    private ProgressBar progressBar;  // Barra de progreso para indicar carga

    // Lista de actividades recientes
    private List<String> actividadesRecientes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar referencia de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("recommendations");

        // Configuración del RecyclerView para mostrar las recomendaciones
        recyclerView = view.findViewById(R.id.recyclerViewRecommendations);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Barra de progreso
        progressBar = view.findViewById(R.id.progressBar); // Asegúrate de que existe en tu layout XML

        // Inicializar la lista de recomendaciones y el adaptador
        recommendationList = new ArrayList<>();
        adapter = new RecommendationAdapter(recommendationList);
        recyclerView.setAdapter(adapter);

        // Botón para permitir al usuario contribuir con una nueva recomendación
        Button buttonContribute = view.findViewById(R.id.button_contribute);
        buttonContribute.setOnClickListener(v -> showContributeDialog(requireContext()));

        // Botón para compartir las recomendaciones seleccionadas
        Button buttonShare = view.findViewById(R.id.button_share);
        buttonShare.setOnClickListener(v -> shareSelectedRecommendations());

        // Inicializar actividades recientes y cargarlas desde SharedPreferences
        actividadesRecientes = new ArrayList<>();
        cargarActividadesRecientes();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Llamar a la función para cargar las recomendaciones después de que la vista esté completamente inflada
        loadRecommendationsFromFirebase();
    }

    // Método para cargar las recomendaciones desde Firebase en tiempo real
    private void loadRecommendationsFromFirebase() {
        // Mostrar el ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Usamos un ValueEventListener para escuchar los cambios en las recomendaciones
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Limpiamos la lista antes de agregar los nuevos datos
                recommendationList.clear();
                // Iteramos sobre los datos obtenidos de Firebase
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Convertimos cada snapshot en un objeto Recommendation
                    Recommendation recommendation = dataSnapshot.getValue(Recommendation.class);
                    if (recommendation != null) {
                        recommendationList.add(recommendation);
                    }
                }
                // Notificamos al adaptador para que actualice la UI
                adapter.notifyDataSetChanged();

                // Ocultar el ProgressBar una vez que los datos están cargados
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // En caso de error, mostramos un mensaje
                Toast.makeText(requireContext(), "Error al cargar las recomendaciones.", Toast.LENGTH_SHORT).show();
                // Ocultar el ProgressBar si ocurre un error
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Método para mostrar el diálogo donde el usuario puede agregar una nueva recomendación
    private void showContributeDialog(Context context) {
        // Crear una vista personalizada para el diálogo
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_contribute, null);

        // Inicializar los componentes del diálogo
        final EditText editTextRecommendation = dialogView.findViewById(R.id.edittext_recommendation);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonAdd = dialogView.findViewById(R.id.button_add);

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Agregar Recomendación")
                .setView(dialogView);

        final AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonAdd.setOnClickListener(v -> {
            String recommendationText = editTextRecommendation.getText().toString().trim();

            // Validación de entrada
            if (recommendationText.isEmpty()) {
                Toast.makeText(context, "Por favor, ingresa una recomendación válida.", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);  // Mostrar barra de progreso mientras se agrega

                // Agregar la recomendación a Firebase
                Recommendation newRecommendation = new Recommendation();
                newRecommendation.setText(recommendationText);
                String key = databaseReference.push().getKey();  // Obtener una nueva clave única

                if (key != null) {
                    databaseReference.child(key).setValue(newRecommendation)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);  // Ocultar barra de progreso
                                Toast.makeText(context, "Recomendación agregada con éxito", Toast.LENGTH_SHORT).show();
                                loadRecommendationsFromFirebase();  // Actualizar las recomendaciones
                                dialog.dismiss();  // Cerrar el diálogo después de agregar

                                // Registrar la actividad reciente
                                registrarActividad("Contribución a las recomendaciones de reciclaje");
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);  // Ocultar barra de progreso
                                Toast.makeText(context, "Error al agregar recomendación. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        dialog.show();
    }

    // Método para compartir las recomendaciones seleccionadas
    private void shareSelectedRecommendations() {
        List<Recommendation> selectedRecommendations = adapter.getSelectedRecommendations();

        if (!selectedRecommendations.isEmpty()) {
            StringBuilder recommendationsText = new StringBuilder();
            for (Recommendation recommendation : selectedRecommendations) {
                recommendationsText.append(recommendation.getText()).append("\n\n");
            }

            // Crear el Intent para compartir
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Recomendaciones de Reciclaje");
            shareIntent.putExtra(Intent.EXTRA_TEXT, recommendationsText.toString());

            // Abrir selector de aplicaciones para compartir
            startActivity(Intent.createChooser(shareIntent, "Compartir Recomendaciones"));
        } else {
            Toast.makeText(requireContext(), "Selecciona al menos una recomendación para compartir.", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cargar las actividades recientes desde SharedPreferences
    private void cargarActividadesRecientes() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("ActividadesPrefs", Context.MODE_PRIVATE);
        // Recuperar las actividades recientes (últimas 5)
        for (int i = 0; i < 5; i++) {
            String actividad = prefs.getString("actividad_" + i, null);
            if (actividad != null) {
                actividadesRecientes.add(actividad);
            }
        }
    }

    // Método para registrar una nueva actividad y guardarla en SharedPreferences
    private void registrarActividad(String actividad) {
        // Añadir la nueva actividad al principio de la lista
        actividadesRecientes.add(0, actividad);
        // Limitar a las últimas 5 actividades
        if (actividadesRecientes.size() > 5) {
            actividadesRecientes.remove(actividadesRecientes.size() - 1);
        }

        // Actualizar las actividades en SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("ActividadesPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < actividadesRecientes.size(); i++) {
            editor.putString("actividad_" + i, actividadesRecientes.get(i));
        }
        editor.apply();
    }
}