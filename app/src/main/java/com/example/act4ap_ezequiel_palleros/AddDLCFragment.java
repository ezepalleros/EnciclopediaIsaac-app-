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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddDLCFragment extends Fragment {

    private static final int PICK_IMAGE = 100;
    private EditText dlcNameEditText;
    private EditText dlcYearEditText;
    private ImageView dlcImageView;
    private Bitmap selectedImageBitmap;
    private DatabaseHelper dbHelper;
    private MainActivity mainActivity;

    // Metodo que se ejecuta al crear la vista.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_dlc, container, false);

        dlcNameEditText = view.findViewById(R.id.dlcNameEditText);
        dlcYearEditText = view.findViewById(R.id.dlcYearEditText);
        dlcImageView = view.findViewById(R.id.dlcImageView);

        dbHelper = new DatabaseHelper(getContext());
        mainActivity = (MainActivity) getActivity();

        // Botón para seleccionar una imagen
        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker(); // Abre álbum de imágenes
            }
        });

        // Botón para agregar el nuevo DLC
        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDLC(); // Agregar el DLC a la BDD
            }
        });

        return view;
    }

    // Metodo para abrir el álbum de imágenes (gracias Twitter)
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Metodo que maneja el resultado de la selección de imagen (gracias Github)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
                dlcImageView.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo para agregar el nuevo DLC
    private void addNewDLC() {
        String dlcName = dlcNameEditText.getText().toString();
        String dlcYear = dlcYearEditText.getText().toString();

        if (dlcName.isEmpty() || dlcYear.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si no se ha seleccionado una imagen, se usa default_image
        String imgDlc = (selectedImageBitmap == null) ? "default_image" : saveImage(selectedImageBitmap);

        dbHelper.addDLC(dlcName, dlcYear, imgDlc);

        mainActivity.updateDLCList();

        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    // Metodo para guardar la imagen seleccionada en el almacenamiento interno
    private String saveImage(Bitmap bitmap) {
        FileOutputStream fos = null;
        String fileName = "dlc_" + System.currentTimeMillis() + ".png";
        try {
            File directory = new File(getContext().getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs(); // Si el directorio no existe, lo crea
            }

            // Crea el archivo de imagen
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
