package com.example.act4ap_ezequiel_palleros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DLCAdapter adapter;
    private DatabaseHelper dbHelper;
    private FragmentContainerView fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas

        fragmentContainer = findViewById(R.id.fragment_container);

        dbHelper = new DatabaseHelper(this);

        // Obtener los datos de los DLCs
        ArrayList<HashMap<String, String>> dlcList = dbHelper.getAllDLCs();

        // Configurar el adaptador con los datos recuperados
        adapter = new DLCAdapter(this, dlcList);
        recyclerView.setAdapter(adapter);

        // Botón para añadir DLC
        Button addDLCButton = findViewById(R.id.addDLCButton);
        addDLCButton.setOnClickListener(v -> {
            fragmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddDLCFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    public void onBackButtonClick(View view) {
        // Volver a la actividad anterior (LoginActivity)
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public void updateDLCList() {
        ArrayList<HashMap<String, String>> dlcList = dbHelper.getAllDLCs();
        adapter = new DLCAdapter(this, dlcList);
        recyclerView.setAdapter(adapter);
    }
}
