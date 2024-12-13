package com.example.act4ap_ezequiel_palleros;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddItemFragment extends Fragment {

    private static final int PICK_IMAGE = 100;
    private EditText itemNameEditText;
    private EditText itemDescriptionEditText;
    private EditText itemQualityEditText;
    private Switch itemTypeSwitch;
    private EditText itemChargesEditText;
    private ImageView itemImageView;
    private Bitmap selectedImageBitmap;
    private DatabaseHelper dbHelper;
    private String idDlc;

    // Metodo que se ejecuta al crear la vista del fragmento.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        itemNameEditText = view.findViewById(R.id.itemNameEditText);
        itemDescriptionEditText = view.findViewById(R.id.itemDescriptionEditText);
        itemQualityEditText = view.findViewById(R.id.itemQualityEditText);
        itemTypeSwitch = view.findViewById(R.id.itemTypeSwitch);
        itemChargesEditText = view.findViewById(R.id.itemChargesEditText);
        itemImageView = view.findViewById(R.id.itemImageView);
        dbHelper = new DatabaseHelper(getContext());

        // Recibir idDlc desde los argumentos del fragmento
        if (getArguments() != null) {
            idDlc = getArguments().getString("idDlc");
        }

        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(v -> openImagePicker());

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> addNewItem());

        // Esconder "cargas" dependiendo del tipo de ítem (activo o pasivo)
        itemTypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                itemChargesEditText.setVisibility(View.VISIBLE);
            } else {
                itemChargesEditText.setVisibility(View.GONE);
            }
        });

        return view;
    }

    // Metodo para abrir el álbum de imágenes
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Metodo que muestra la imagen
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
                itemImageView.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo para agregar un nuevo ítem a la base de datos.
    private void addNewItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemDescription = itemDescriptionEditText.getText().toString().trim();
        String itemQualityStr = itemQualityEditText.getText().toString().trim();
        String itemChargesStr = itemChargesEditText.getText().toString().trim();

        if (itemName.isEmpty() || itemDescription.isEmpty() || itemQualityStr.isEmpty() ||
                (itemTypeSwitch.isChecked() && itemChargesStr.isEmpty()) || idDlc == null || idDlc.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos requeridos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int itemQuality = Integer.parseInt(itemQualityStr);

            if (itemQuality < 0 || itemQuality > 4) {
                Toast.makeText(getContext(), "La calidad debe estar entre 0 y 4.", Toast.LENGTH_SHORT).show();
                return;
            }

            String itemImage = (selectedImageBitmap == null) ? "default_image" : saveImage(selectedImageBitmap);

            // Agregar ítem activo o pasivo dependiendo del tipo seleccionado
            if (itemTypeSwitch.isChecked()) {
                if (itemChargesStr.isEmpty()) {
                    Toast.makeText(getContext(), "Por favor, ingrese el número de cargas.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int itemCharges = Integer.parseInt(itemChargesStr);
                dbHelper.addActiveItem(itemName, itemDescription, itemQuality, itemCharges, itemImage, idDlc);
            } else {
                dbHelper.addPassiveItem(itemName, itemDescription, itemQuality, itemImage, idDlc);
            }

            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

            getActivity().recreate();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Error en los valores numéricos ingresados.", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para guardar la imagen en el dispositivo.
    private String saveImage(Bitmap bitmap) {
        FileOutputStream fos = null;
        String fileName = "item_" + System.currentTimeMillis() + ".png";
        try {
            File directory = new File(getContext().getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File imageFile = new File(directory, fileName);
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "default_image";
        }
        return "images/" + fileName;
    }
}
