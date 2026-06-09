package com.example.carbyer;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.carbyer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String message = getIntent().getStringExtra("message");

        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main
        );

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            int id = destination.getId();

            if (id == R.id.nav_cars) {

                binding.appBarMain.fab.show();

                binding.appBarMain.fab.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putString("mode", "create");

                    controller.navigate(R.id.nav_car_create_update, args);
                });
            }

            else if (id == R.id.nav_dealers) {

                binding.appBarMain.fab.show();

                binding.appBarMain.fab.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putString("mode", "create");

                    controller.navigate(R.id.nav_dealer_create_update, args);
                });
            }

            else {
                binding.appBarMain.fab.hide();
            }
        });


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_cars, R.id.nav_dealers
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main
        );
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}