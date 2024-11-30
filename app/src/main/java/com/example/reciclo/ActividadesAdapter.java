package com.example.reciclo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ActividadViewHolder> {

    private List<Actividad> actividades;

    // Constructor que recibe la lista de actividades
    public ActividadesAdapter(List<Actividad> actividades) {
        this.actividades = actividades;
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño personalizado para cada ítem
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        // Obtener la actividad actual y asignar sus valores a las vistas
        Actividad actividad = actividades.get(position);
        holder.codigoBarraTextView.setText("Código: " + actividad.getCodigoBarra());
        holder.fechaHoraTextView.setText("Fecha: " + actividad.getFechaHora());
    }

    @Override
    public int getItemCount() {
        return actividades.size(); // Retorna el número de actividades en la lista
    }

    // Clase ViewHolder que maneja las vistas de cada ítem
    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView codigoBarraTextView;
        TextView fechaHoraTextView;

        public ActividadViewHolder(View itemView) {
            super(itemView);
            // Enlazar las vistas desde el diseño personalizado
            codigoBarraTextView = itemView.findViewById(R.id.textCodigoBarra);
            fechaHoraTextView = itemView.findViewById(R.id.textFechaHora);
        }
    }
}