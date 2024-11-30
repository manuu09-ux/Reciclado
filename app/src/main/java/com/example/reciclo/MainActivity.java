package com.example.reciclo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración de DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Configuración de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Verificar autenticación del usuario
        authenticateUser();

        // Configuración de navegación
        setupNavigation();

        // Acciones del Navigation Drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Usar if-else en lugar de switch-case
            if (itemId == R.id.nav_profile) {
                navController.navigate(R.id.nav_profile);
            } else if (itemId == R.id.nav_logros) { // Manejo de la opción Logros
                navController.navigate(R.id.nav_logros);
            } else if (itemId == R.id.nav_map) { // Navegación al mapa
                navController.navigate(R.id.nav_map);  // Asegúrate de tener el fragmento de mapa en el nav_graph.xml
            } else if (itemId == R.id.nav_settings) {
                navController.navigate(R.id.nav_settings);
            } else if (itemId == R.id.nav_logout) {
                logoutUser();
            } else {
                Toast.makeText(this, "Opción no reconocida", Toast.LENGTH_SHORT).show();
                return false;
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void authenticateUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void setupNavigation() {
        // Inicializar el NavController y configurar la navegación
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_profile, R.id.nav_logros, R.id.nav_settings, R.id.nav_map, R.id.nav_scan)
                    .setOpenableLayout(drawerLayout)
                    .build();

            // Configurar ActionBar
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Configurar BottomNavigationView
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            // Configurar NavigationView (DrawerLayout)
            NavigationUI.setupWithNavController(navigationView, navController);
        } else {
            Log.e(TAG, "NavHostFragment no encontrado");
            // Mostrar un mensaje de error al usuario o realizar alguna acción alternativa
            Toast.makeText(this, "Error al inicializar la navegación", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
    }
}