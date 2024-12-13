package com.example.act4ap_ezequiel_palleros;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> itemList;

    public ItemAdapter(Context context, ArrayList<HashMap<String, String>> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Metodo para crear la vista de cada ítem.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("ItemAdapter", "getView llamado para posición: " + position);

        // Infla una nueva vista si no ha sido reciclada
        if (convertView == null) {
            Log.d("ItemAdapter", "Inflando una nueva vista para la posición: " + position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_layout, parent, false);
        }

        // Obtiene referencias a los elementos de la vista
        TextView itemName = convertView.findViewById(R.id.itemName);
        TextView itemDescription = convertView.findViewById(R.id.itemDescription);
        ImageView itemImage = convertView.findViewById(R.id.itemImage);

        HashMap<String, String> item = itemList.get(position);
        Log.d("ItemAdapter", "Datos del ítem en posición " + position + ": " + item);

        // Establece los valores de nombre y descripción
        itemName.setText(item.get("name"));
        itemDescription.setText(item.get("description"));

        // Configura la imagen
        String imgPath = item.get("img");
        if (imgPath != null) {
            File imgFile = new File(context.getFilesDir(), imgPath);
            if (imgFile.exists()) {
                Log.d("ItemAdapter", "Cargando imagen desde archivo: " + imgFile.getAbsolutePath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                itemImage.setImageBitmap(myBitmap);
            } else {
                int imageResource = context.getResources().getIdentifier(imgPath, "drawable", context.getPackageName());
                if (imageResource != 0) {
                    Log.d("ItemAdapter", "Cargando imagen desde recursos drawable: " + imgPath);
                    itemImage.setImageResource(imageResource);
                } else {
                    Log.w("ItemAdapter", "Imagen no encontrada, usando imagen predeterminada");
                    itemImage.setImageResource(R.drawable.default_image);
                }
            }
        } else {
            Log.w("ItemAdapter", "Ruta de imagen nula, usando imagen predeterminada");
            itemImage.setImageResource(R.drawable.default_image);
        }

        // Obtiene y verifica calidad y cargas
        String qualityStr = item.get("calidad");
        String chargesStr = item.get("carga");

        // Configura el comportamiento al hacer clic en la imagen
        itemImage.setOnClickListener(v -> {
            Log.d("ItemAdapter", "Se hizo clic en la imagen del ítem en posición: " + position);
            try {
                Fragment fragment = new ViewItemFragment();
                Bundle bundle = new Bundle();
                bundle.putString("itemName", item.get("name"));
                bundle.putString("itemDescription", item.get("description"));
                bundle.putString("itemImage", imgPath);

                // Validación de calidad
                if (qualityStr != null && !qualityStr.isEmpty()) {
                    try {
                        int quality = Integer.parseInt(qualityStr);
                        bundle.putInt("itemQuality", quality);
                        Log.d("ItemAdapter", "Calidad del ítem: " + quality);
                    } catch (NumberFormatException e) {
                        bundle.putInt("itemQuality", 0);
                        Log.e("ItemAdapter", "Error al parsear calidad: " + qualityStr, e);
                    }
                } else {
                    bundle.putInt("itemQuality", 0);
                    Log.w("ItemAdapter", "Calidad del ítem no especificada, asignando valor predeterminado (0)");
                }

                // Validación de cargas
                if (chargesStr != null && !chargesStr.isEmpty()) {
                    try {
                        int charges = Integer.parseInt(chargesStr);
                        bundle.putInt("itemCharges", charges);
                        Log.d("ItemAdapter", "Cargas del ítem: " + charges);
                    } catch (NumberFormatException e) {
                        bundle.putInt("itemCharges", 0);
                        Log.e("ItemAdapter", "Error al parsear cargas: " + chargesStr, e);
                    }
                } else {
                    bundle.putInt("itemCharges", 0);
                    Log.w("ItemAdapter", "Cargas del ítem no especificadas, asignando valor predeterminado (0)");
                }

                fragment.setArguments(bundle);

                // Realiza la transacción
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();  // Usa commitAllowingStateLoss() si hay un posible conflicto con el estado.
                Log.d("ItemAdapter", "Transacción del fragmento completada con éxito");
            } catch (Exception e) {
                Log.e("ItemAdapter", "Error inesperado: " + e.getMessage(), e);
                Toast.makeText(context, "Ocurrió un error inesperado.", Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }
}
