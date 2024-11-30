package com.example.reciclo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    // Lista para almacenar los nuevos puntos de reciclaje
    private List<LatLng> newRecyclingPoints = new ArrayList<>();
    private List<String> newRecyclingNames = new ArrayList<>();
    private List<String> newRecyclingInfo = new ArrayList<>();

    // Coordenadas de algunos puntos de reciclaje en Teziutlán, Puebla
    private static final LatLng[] RECYCLING_POINTS = {
            new LatLng(19.869569436596176, -97.36439267116376),  // Recolecta-Tez
            new LatLng(19.700, -97.365),  // Ejemplo: Punto 1
            new LatLng(19.710, -97.370),  // Ejemplo: Punto 2
            new LatLng(19.720, -97.375)   // Ejemplo: Punto 3
    };

    private static final String[] RECYCLING_NAMES = {
            "Recolecta-Tez",
            "Punto de Reciclaje A",
            "Punto de Reciclaje B",
            "Punto de Reciclaje C"
    };

    private static final String[] RECYCLING_INFO = {
            "Recolecta-tez en Josefa Ramiro Gavilán, sección 2° de San Diego, 73965 Teziutlán, Pue., México. " +
                    "Abierto de lunes a sábado de 8:00 a 18:30 y los domingos de 8:00 a 13:00. Calificación: 5 estrellas en Google Maps.",
            "Reciclaje de electrónicos y baterías.",
            "Reciclaje de envases plásticos.",
            "Reciclaje de vidrio y latas."
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Inicializar MapView
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);  // Configura el mapa cuando esté listo
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Agregar puntos de reciclaje predeterminados al mapa
        for (int i = 0; i < RECYCLING_POINTS.length; i++) {
            googleMap.addMarker(new MarkerOptions()
                    .position(RECYCLING_POINTS[i])
                    .title(RECYCLING_NAMES[i])  // Título con el nombre del lugar
                    .snippet(RECYCLING_INFO[i]));  // Información adicional
        }

        // Agregar los puntos nuevos al mapa
        addNewRecyclingPointsToMap();

        // Centrar el mapa en el primer punto
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(RECYCLING_POINTS[0], 14));

        // Habilitar controles estándar de Google Maps (zoom, etc.)
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Habilitar la ubicación del usuario en el mapa
        enableUserLocation();

        // Configurar un listener para los clics en los marcadores
        googleMap.setOnMarkerClickListener(marker -> {
            // Mostrar una ventana emergente (AlertDialog) con la información del marcador
            showMarkerInfoDialog(marker.getTitle(), marker.getSnippet());

            // Registrar la actividad del usuario (marcador clicado)
            registrarActividad(marker.getTitle());
            return false;  // Retornar false para permitir la acción predeterminada (selección del marcador)
        });

        // Configurar listener para tocar el mapa y agregar un nuevo punto de reciclaje
        googleMap.setOnMapClickListener(latLng -> {
            showAddPointDialog(latLng);
        });
    }

    private void addNewRecyclingPointsToMap() {
        // Agregar los nuevos puntos de reciclaje al mapa
        for (int i = 0; i < newRecyclingPoints.size(); i++) {
            googleMap.addMarker(new MarkerOptions()
                    .position(newRecyclingPoints.get(i))
                    .title(newRecyclingNames.get(i))
                    .snippet(newRecyclingInfo.get(i)));
        }
    }

    private void enableUserLocation() {
        // Verificar si se tienen permisos para acceder a la ubicación
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están concedidos
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Activar el botón para mostrar la ubicación del usuario
        googleMap.setMyLocationEnabled(true);

        // Obtener la ubicación actual del usuario y mover la cámara al punto de ubicación
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Mostrar la ubicación del usuario con un marcador
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Tu Ubicación"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
                    }
                });
    }

    private void showMarkerInfoDialog(String title, String info) {
        // Crear un AlertDialog para mostrar la información del marcador
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(info)
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss()) // Botón para cerrar la ventana emergente
                .create()
                .show();
    }

    private void registrarActividad(String puntoReciclaje) {
        // Aquí puedes implementar el registro de la actividad
        // Esto puede incluir guardar la actividad en una base de datos, enviar la información a un servidor, etc.
        Toast.makeText(requireContext(), "Actividad registrada: " + puntoReciclaje, Toast.LENGTH_SHORT).show();
    }

    private void showAddPointDialog(LatLng latLng) {
        // Mostrar un diálogo para ingresar la información del nuevo punto
        View view = getLayoutInflater().inflate(R.layout.dialog_add_point, null);
        EditText nameEditText = view.findViewById(R.id.editTextName);
        EditText infoEditText = view.findViewById(R.id.editTextInfo);

        new AlertDialog.Builder(requireContext())
                .setTitle("Nuevo Punto de Reciclaje")
                .setView(view)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String name = nameEditText.getText().toString();
                    String info = infoEditText.getText().toString();

                    if (!name.isEmpty() && !info.isEmpty()) {
                        // Agregar marcador en el mapa
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(name)
                                .snippet(info));

                        // Crear un objeto RecyclingPoint
                        RecyclingPoint newPoint = new RecyclingPoint(name, info, latLng.latitude, latLng.longitude);

                        // Guardar el punto de reciclaje en Firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference pointsRef = database.getReference("recycling_points");

                        // Generar un ID único para el nuevo punto
                        String pointId = pointsRef.push().getKey();

                        if (pointId != null) {
                            pointsRef.child(pointId).setValue(newPoint)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Punto agregado y guardado en Firebase", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Error al guardar en Firebase", Toast.LENGTH_SHORT).show();
                                    });
                        }

                        Toast.makeText(requireContext(), "Punto agregado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Por favor, ingresa todos los detalles", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Clase para representar un punto de reciclaje
    public static class RecyclingPoint {
        public String name;
        public String info;
        public double latitude;
        public double longitude;

        public RecyclingPoint() {
            // Constructor vacío requerido para Firebase
        }

        public RecyclingPoint(String name, String info, double latitude, double longitude) {
            this.name = name;
            this.info = info;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}