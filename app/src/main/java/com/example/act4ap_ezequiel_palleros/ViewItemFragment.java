package com.example.act4ap_ezequiel_palleros;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ViewItemFragment extends Fragment {

    private ImageView itemImageView;
    private TextView itemNameTextView, itemDescriptionTextView, itemQualityTextView, itemChargesTextView;
    private String itemImage, itemName, itemDescription;
    private int itemQuality, itemCharges;
    private DatabaseHelper dbHelper;

    // Metodo cuando se crea la vista del fragmento
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.fragment_view_item, container, false);
    }

    // Metodo llamado después de la creación
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        dbHelper = new DatabaseHelper(getActivity());
        loadItemData(view);
        configureDeleteButton(view);
        configureBackButton(view);
    }

    // Metodo para inicializar las vistas (medio al dope)
    private void initializeViews(View view) {
        itemImageView = view.findViewById(R.id.itemImageView);
        itemNameTextView = view.findViewById(R.id.itemNameTextView);
        itemDescriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        itemQualityTextView = view.findViewById(R.id.itemQualityTextView);
        itemChargesTextView = view.findViewById(R.id.itemChargesTextView);
    }

    // Metodo para cargar los datos del ítem desde el Bundle
    private void loadItemData(View view) {
        if (getArguments() != null) {
            itemName = getArguments().getString("itemName");
            itemDescription = getArguments().getString("itemDescription");
            itemImage = getArguments().getString("itemImage");
            itemQuality = getArguments().getInt("itemQuality");
            itemCharges = getArguments().getInt("itemCharges");

            itemNameTextView.setText(itemName);
            itemDescriptionTextView.setText(itemDescription);
            itemQualityTextView.setText("Calidad: " + itemQuality);
            itemChargesTextView.setText("Cargas: " + itemCharges);

            loadItemImage(itemImage);
        }
    }

    // Metodo para cargar la imagen del ítem desde el almacenamiento o los recursos
    private void loadItemImage(String itemImage) {
        String imgPath = itemImage;
        if (imgPath != null) {
            File imgFile = new File(getActivity().getFilesDir(), imgPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                itemImageView.setImageBitmap(myBitmap);
            } else {
                int resID = getResources().getIdentifier(imgPath, "drawable", getActivity().getPackageName());
                if (resID != 0) {
                    itemImageView.setImageResource(resID);
                } else {
                    itemImageView.setImageResource(R.drawable.default_image);
                }
            }
        } else {
            itemImageView.setImageResource(R.drawable.default_image);
        }
    }

    // Metodo para configurar el botón de eliminar ítem
    private void configureDeleteButton(View view) {
        Button deleteButton = view.findViewById(R.id.deleteButton);

        int itemCharges = dbHelper.getItemCharges(itemName);

        boolean isActive = itemCharges > 0;

        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteItem(itemName, isActive);
            Toast.makeText(getActivity(), "Ítem eliminado", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        });
    }

    // Metodo para configurar el botón de volver atrás
    private void configureBackButton(View view) {
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
    }
}
