package com.example.reciclo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogrosAdapter extends RecyclerView.Adapter<LogrosAdapter.LogroViewHolder> {

    private List<Logro> logros;

    public LogrosAdapter(List<Logro> logros) {
        this.logros = logros;
    }

    @NonNull
    @Override
    public LogroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logro, parent, false);
        return new LogroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogroViewHolder holder, int position) {
        Logro logro = logros.get(position);

        // Configura el texto del logro
        holder.textoLogro.setText(logro.getNombre());

        // Cambia el ícono dependiendo si el logro está alcanzado
        if (logro.isAlcanzado()) {
            holder.iconoLogro.setImageResource(R.drawable.ic_logro_completado);  // Ícono de logro completado
            holder.textoLogro.setText(logro.getNombre() + " - ¡Completado!");
        } else {
            holder.iconoLogro.setImageResource(R.drawable.ic_logro_incompleto);  // Ícono de logro incompleto
        }
    }

    @Override
    public int getItemCount() {
        return logros.size();
    }

    public static class LogroViewHolder extends RecyclerView.ViewHolder {
        ImageView iconoLogro;
        TextView textoLogro;

        public LogroViewHolder(View itemView) {
            super(itemView);
            iconoLogro = itemView.findViewById(R.id.icono_logro);
            textoLogro = itemView.findViewById(R.id.texto_logro);
        }
    }
}