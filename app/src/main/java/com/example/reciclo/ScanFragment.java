package com.example.reciclo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanFragment extends Fragment {

    private static final int REQUEST_CODE_SCAN = 101;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private boolean productsLoaded = false;
    private SharedPreferences preferences;

    private int scanCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        preferences = getActivity().getSharedPreferences("RecicloPrefs", getActivity().MODE_PRIVATE);
        productsLoaded = preferences.getBoolean("productsLoaded", false);

        progressBar = view.findViewById(R.id.progress_bar);
        Button scanButton = view.findViewById(R.id.button_scan);
        Button galleryButton = view.findViewById(R.id.button_gallery);
        Button loadProductsButton = view.findViewById(R.id.button_load_products);

        // Cargar el contador de escaneos desde las preferencias
        scanCount = preferences.getInt("scanCount", 0);

        // Establecer listeners para botones
        scanButton.setOnClickListener(v -> startBarcodeScanner());
        galleryButton.setOnClickListener(v -> openGallery());
        loadProductsButton.setOnClickListener(v -> cargarProductosFirebase());

        // Cargar productos automáticamente si no se han cargado previamente
        if (!productsLoaded) {
            cargarProductosFirebase();
        }

        return view;
    }

    private void startBarcodeScanner() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1002);
    }

    private void cargarProductosFirebase() {
        progressBar.setVisibility(View.VISIBLE); // Mostrar el indicador de carga
        List<Map<String, Object>> productos = getProductos();

        // Usar un ciclo para agregar los productos a Firebase solo si no existen
        for (Map<String, Object> producto : productos) {
            String codigoBarra = (String) producto.get("codigo_barra");
            db.collection("productos").document(codigoBarra)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().exists()) {
                            db.collection("productos").document(codigoBarra)
                                    .set(producto)
                                    .addOnSuccessListener(aVoid -> Log.d("ScanFragment", "Producto agregado: " + codigoBarra))
                                    .addOnFailureListener(e -> showError("Error al agregar producto: " + codigoBarra));
                        }
                    });
        }

        // Guardar preferencia para evitar recargar productos
        preferences.edit().putBoolean("productsLoaded", true).apply();
        progressBar.setVisibility(View.GONE); // Ocultar el indicador de carga
        showSuccess("Productos cargados correctamente.");
    }

    private List<Map<String, Object>> getProductos() {
        // Crear productos con información de reciclaje
        List<Map<String, Object>> productos = new ArrayList<>();
        productos.add(createProducto("1234567890123", "Botella de plástico", "1. Lava la botella\n2. Sepárala por colores\n3. Llévala a un centro de reciclaje"));
        productos.add(createProducto("2345678901234", "Lata de aluminio", "1. Aplana la lata\n2. Limpia los residuos\n3. Llévala a un centro de reciclaje"));
        return productos;
    }

    private Map<String, Object> createProducto(String codigoBarra, String nombre, String pasosReciclaje) {
        Map<String, Object> producto = new HashMap<>();
        producto.put("codigo_barra", codigoBarra);
        producto.put("nombre", nombre);
        producto.put("pasos_reciclaje", pasosReciclaje);
        return producto;
    }

    private void showRecyclingSteps(String nombre, String pasosReciclaje) {
        TextView recyclingInfoTextView = getView().findViewById(R.id.recycling_steps_textview);
        if (recyclingInfoTextView != null) {
            // Formatear el texto para mostrar el nombre y los pasos con etiquetas HTML correctamente interpretadas
            String formattedPasos = "<b>Producto:</b> " + nombre + "<br><b></b><br>" + pasosReciclaje;

            // Usar Html.fromHtml para interpretar las etiquetas HTML
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                recyclingInfoTextView.setText(Html.fromHtml(formattedPasos, Html.FROM_HTML_MODE_LEGACY));
            } else {
                recyclingInfoTextView.setText(Html.fromHtml(formattedPasos));
            }
        }
    }

    private void showError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void checkAchievements() {
        if (scanCount == 1) {
            showSuccess("¡Logro desbloqueado: Primer escaneo!");
        } else if (scanCount == 10) {
            showSuccess("¡Logro desbloqueado: 10 escaneos!");
        } else if (scanCount == 50) {
            showSuccess("¡Logro desbloqueado: 50 escaneos!");
        }
    }

    private void registrarActividad(String codigoBarra) {
        Map<String, Object> actividad = new HashMap<>();
        actividad.put("codigo_barra", codigoBarra);
        actividad.put("fecha_hora", FieldValue.serverTimestamp());
        actividad.put("scan_count", scanCount);

        db.collection("actividades")
                .add(actividad)
                .addOnSuccessListener(documentReference -> showSuccess("Actividad registrada con éxito."))
                .addOnFailureListener(e -> showError("Error al registrar la actividad."));
    }

    private void manejarResultadoEscaneo(String codigoBarras, boolean esReciclable) {
        // Mostrar información al usuario
        String mensaje = esReciclable
                ? "¡El producto es reciclable! Gracias por contribuir al medio ambiente."
                : "El producto no es reciclable, pero puedes revisarlo más tarde.";
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();

        // Incrementar el contador de escaneos
        scanCount++;
        preferences.edit().putInt("scanCount", scanCount).apply();

        // Registrar la actividad del escaneo
        registrarActividad(codigoBarras);

        // Verificar logros
        checkAchievements();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN && resultCode == getActivity().RESULT_OK && data != null) {
            String scannedCode = data.getStringExtra("SCAN_RESULT");
            showSuccess("Código escaneado: " + scannedCode);
            getRecyclingInfo(scannedCode);
        }
    }

    private void getRecyclingInfo(String scannedCode) {
        db.collection("productos")
                .whereEqualTo("codigo_barra", scannedCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nombre = document.getString("nombre");
                            String pasosReciclaje = document.getString("pasos_reciclaje");
                            boolean esReciclable = true; // Suponiendo que todos los productos registrados son reciclables
                            showRecyclingSteps(nombre, pasosReciclaje);
                            manejarResultadoEscaneo(scannedCode, esReciclable);
                        }
                    } else {
                        agregarNuevoProducto(scannedCode);
                    }
                });
    }

    private void agregarNuevoProducto(String scannedCode) {
        // Inflar el diseño del diálogo desde el archivo XML
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        // Obtener las referencias a los elementos del layout
        EditText productNameEditText = dialogView.findViewById(R.id.edittext_product_name);

        // Crear el AlertDialog y configurarlo
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Nuevo Producto")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String productName = productNameEditText.getText().toString().trim();
                    if (!productName.isEmpty()) {
                        // Formato mejorado de los pasos de reciclaje con listas y descripciones
                        String pasosReciclajePredeterminados =
                                "<b>Pasos para Reciclar:</b><br>" +
                                        "<ol>" +
                                        "<li><b>Lava el producto:</b> Asegúrate de que no haya residuos de comida, líquidos o materiales sucios. " +
                                        "Esto ayuda a evitar la contaminación y hace que el reciclaje sea más eficiente.</li>" +
                                        "<li><b>Sepáralo por materiales:</b> Divide los materiales reciclables como plásticos, vidrios, metales y cartón. " +
                                        "Cada tipo de material debe ir a su respectivo contenedor de reciclaje.</li>" +
                                        "<li><b>Llévalo a un centro de reciclaje:</b> Busca el centro de reciclaje más cercano o un punto verde donde puedas dejar tus materiales reciclables.</li>" +
                                        "</ol>" +
                                        "<b>Recuerda:</b> ¡Cada pequeño esfuerzo cuenta para salvar el planeta!";

                        // Guardamos el nuevo producto en la base de datos con pasos de reciclaje predeterminados
                        Map<String, Object> newProduct = new HashMap<>();
                        newProduct.put("codigo_barra", scannedCode);
                        newProduct.put("nombre", productName);
                        newProduct.put("pasos_reciclaje", pasosReciclajePredeterminados);

                        db.collection("productos")
                                .document(scannedCode)
                                .set(newProduct)
                                .addOnSuccessListener(aVoid -> {
                                    showSuccess("Producto agregado exitosamente.");
                                    getRecyclingInfo(scannedCode); // Volver a buscar para mostrar los pasos de reciclaje
                                })
                                .addOnFailureListener(e -> showError("Error al agregar producto."));
                    } else {
                        showError("Por favor, ingresa un nombre válido.");
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

}