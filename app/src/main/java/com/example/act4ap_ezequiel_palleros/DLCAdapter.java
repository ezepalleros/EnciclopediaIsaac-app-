package com.example.act4ap_ezequiel_palleros;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DLCAdapter extends RecyclerView.Adapter<DLCAdapter.DLCViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> dlcList;
    private DatabaseHelper dbHelper;

    // Constructor de la clase
    public DLCAdapter(Context context, ArrayList<HashMap<String, String>> dlcList) {
        this.context = context;
        this.dlcList = dlcList;
        this.dbHelper = new DatabaseHelper(context);
    }

    // Metodo para crear una nueva vista para cada item del RecyclerView
    @Override
    public DLCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout de cada item
        View view = LayoutInflater.from(context).inflate(R.layout.item_dlc, parent, false);
        return new DLCViewHolder(view); // Crear un nuevo ViewHolder para ese item
    }

    // Metodo para asignar los datos a cada item del RecyclerView
    @Override
    public void onBindViewHolder(DLCViewHolder holder, int position) {
        HashMap<String, String> dlc = dlcList.get(position);
        holder.dlcName.setText(dlc.get("nomDlc"));

        // Cargar la imagen desde el almacenamiento interno o desde los recursos drawable
        String imgPath = dlc.get("imgDlc");
        if (imgPath != null) {
            File imgFile = new File(context.getFilesDir(), imgPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.dlcImage.setImageBitmap(myBitmap);
            } else {
                int imageResource = context.getResources().getIdentifier(imgPath, "drawable", context.getPackageName());
                if (imageResource != 0) {
                    holder.dlcImage.setImageResource(imageResource);
                } else {
                    holder.dlcImage.setImageResource(R.drawable.default_image);
                }
            }
        } else {
            holder.dlcImage.setImageResource(R.drawable.default_image);
        }

        // Configurar el click listener para cada item del RecyclerView
        holder.itemView.setOnClickListener(v -> {
            String idDlc = dlc.get("idDlc");
            if (idDlc != null && !idDlc.isEmpty()) {
                Intent intent = new Intent(context, ItemActivity.class);
                intent.putExtra("idDlc", idDlc);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "ID de DLC no válido", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el click listener para el botón de eliminar el DLC
        holder.deleteDLCButton.setOnClickListener(v -> {
            // Mostrar un cuadro de diálogo de confirmación para eliminar el DLC (Esto lo busqué en Google y Gemini me respondió con esto)
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Eliminar DLC")
                    .setMessage("¿Estás seguro de que quieres eliminar este DLC?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Si se confirma, eliminar el DLC de la base de datos
                        dbHelper.deleteDLC(dlc.get("idDlc"));
                        dlcList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, dlcList.size());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    // Metodo que devuelve la cantidad de elementos en la lista de DLCs
    @Override
    public int getItemCount() {
        return dlcList.size(); // Retornar el tamaño de la lista de DLCs
    }

    // ViewHolder que contiene las vistas de cada item del RecyclerView
    public static class DLCViewHolder extends RecyclerView.ViewHolder {
        TextView dlcName;
        ImageView dlcImage;
        Button deleteDLCButton;

        public DLCViewHolder(View itemView) {
            super(itemView);
            dlcName = itemView.findViewById(R.id.dlcName);
            dlcImage = itemView.findViewById(R.id.dlcImage);
            deleteDLCButton = itemView.findViewById(R.id.deleteDLCButton);
        }
    }
}
