package com.example.reciclo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private TextView userNameTextView, userEmailTextView;
    private ImageView profileImageView;
    private Button editProfileButton, showAchievementsButton;

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1002;

    private String userName;
    private String userEmail;
    private String userPhotoUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inicializar vistas
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        profileImageView = view.findViewById(R.id.profileImageView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        showAchievementsButton = view.findViewById(R.id.showAchievementsButton);

        // Cargar datos de perfil
        loadProfileData();

        // Configurar acciones
        editProfileButton.setOnClickListener(v -> editProfile());
        showAchievementsButton.setOnClickListener(v -> showAchievements());
        profileImageView.setOnClickListener(v -> openImageSourceDialog());

        return view;
    }

    private void loadProfileData() {
        // Obtener el usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Obtener nombre, correo y foto del usuario
            userName = currentUser.getDisplayName();
            userEmail = currentUser.getEmail();
            userPhotoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "";

            // Mostrar los datos
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);

            // Cargar la foto de perfil (usando Picasso para cargarla desde la URL)
            if (!userPhotoUrl.isEmpty()) {
                Picasso.get().load(userPhotoUrl).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_user_avatar);  // Foto predeterminada si no hay foto
            }
        } else {
            // Si no hay usuario autenticado, mostrar datos predeterminados
            userName = "Juan Pérez";  // Datos de ejemplo
            userEmail = "juan@example.com";
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);
            profileImageView.setImageResource(R.drawable.ic_user_avatar);  // Foto predeterminada
        }
    }

    private void editProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Perfil");

        // Layout personalizado
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        final EditText inputName = view.findViewById(R.id.editUserName);
        final EditText inputEmail = view.findViewById(R.id.editTextNewEmail);  // Cambié el ID aquí
        inputName.setText(userName);
        inputEmail.setText(userEmail);

        builder.setView(view);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newUserName = inputName.getText().toString().trim();
            String newUserEmail = inputEmail.getText().toString().trim();

            if (!newUserName.isEmpty() && !newUserEmail.isEmpty()) {
                userName = newUserName;
                userEmail = newUserEmail;
                userNameTextView.setText(userName);
                userEmailTextView.setText(userEmail);
                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showAchievements() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logros");

        String[] achievements = {
                "Logro 1: Visitar 10 puntos de reciclaje",
                "Logro 2: Reciclar 5 tipos de materiales"
        };

        if (achievements.length > 0) {
            builder.setItems(achievements, null);
        } else {
            builder.setMessage("Aún no tienes logros.");
        }
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void openImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Seleccionar Imagen");
        builder.setItems(new CharSequence[] {"Tomar foto", "Seleccionar de la galería"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    openCamera();
                } else {
                    openGallery();
                }
            }
        });
        builder.show();
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_TAKE_PHOTO);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            try {
                Bitmap bitmap = null;
                if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                    Uri imageUri = data.getData();
                    Log.d("ProfileFragment", "Imagen seleccionada de la galería: " + imageUri);
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    Log.d("ProfileFragment", "Foto tomada con cámara");
                }

                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                    Log.d("ProfileFragment", "Imagen cargada correctamente");
                } else {
                    Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("ProfileFragment", "Resultado no OK o datos nulos");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    }
}