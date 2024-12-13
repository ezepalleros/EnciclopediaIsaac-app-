package com.example.act4ap_ezequiel_palleros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemActivity extends AppCompatActivity {

    private GridView passiveGridView, activeGridView;
    private TextView passiveTitle, activeTitle, emptyMessage;
    private Button addItemButton;
    private ArrayList<HashMap<String, String>> passiveItems, activeItems;
    private DatabaseHelper dbHelper;
    private String idDlc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        passiveTitle = findViewById(R.id.passiveTitle);
        activeTitle = findViewById(R.id.activeTitle);
        passiveGridView = findViewById(R.id.passiveGridView);
        activeGridView = findViewById(R.id.activeGridView);
        emptyMessage = findViewById(R.id.emptyMessage);
        addItemButton = findViewById(R.id.addItemButton);

        dbHelper = new DatabaseHelper(this);
        idDlc = getIntent().getStringExtra("idDlc");

        loadItems(); // Cargar los ítems (pasivos y activos) del DLC

        // Configurar el listener para el botón de agregar ítem
        addItemButton.setOnClickListener(v -> openAddItemFragment());
        addItemButton.setVisibility(View.VISIBLE);
    }

    // Metodo para cargar los ítems pasivos y activos desde la base de datos
    private void loadItems() {
        passiveItems = dbHelper.getPassiveItemsByDLC(idDlc);
        activeItems = dbHelper.getActiveItemsByDLC(idDlc);

        // Si hay ítems pasivos, mostrar el título y el GridView correspondiente
        if (!passiveItems.isEmpty()) {
            passiveTitle.setVisibility(View.VISIBLE);
            passiveGridView.setVisibility(View.VISIBLE);
            passiveGridView.setAdapter(new ItemAdapter(this, passiveItems));
        }

        // Si hay ítems activos, mostrar el título y el GridView correspondiente
        if (!activeItems.isEmpty()) {
            activeTitle.setVisibility(View.VISIBLE);
            activeGridView.setVisibility(View.VISIBLE);
            activeGridView.setAdapter(new ItemAdapter(this, activeItems));
        }

        // Si no hay ni ítems pasivos ni activos, mostrar el mensaje de vacío
        if (passiveItems.isEmpty() && activeItems.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

    // Metodo para abrir AddItemFragment
    private void openAddItemFragment() {
        Fragment fragment = new AddItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString("idDlc", idDlc); // Pasar el ID del DLC al fragmento
        fragment.setArguments(bundle); // Establecer los argumentos del fragmento

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE); // Hacer visible el contenedor de fragmentos
    }

    // Metodo para botón de retroceder
    public void onBackButtonClick(View view) {
        // Navegar de vuelta a la MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}