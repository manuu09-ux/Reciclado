package com.example.reciclo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private List<Recommendation> recommendationList;
    private List<Recommendation> selectedRecommendations = new ArrayList<>();
    private boolean isLongPressed = false;  // Variable para controlar si se mantiene el dedo pulsado

    public RecommendationAdapter(List<Recommendation> recommendationList) {
        this.recommendationList = recommendationList;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        Recommendation recommendation = recommendationList.get(position);
        holder.recommendationText.setText(recommendation.getText());

        // Si la opción está activada por un toque largo, mostramos el CheckBox
        holder.checkBox.setVisibility(isLongPressed ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(selectedRecommendations.contains(recommendation));

        // Activamos el CheckBox al mantener pulsado el item
        holder.itemView.setOnLongClickListener(v -> {
            isLongPressed = true;  // Se activa la opción de mostrar el CheckBox
            notifyDataSetChanged();  // Notificamos al adaptador para actualizar la UI
            return true;  // Indica que el evento se ha manejado
        });

        // Comportamiento del CheckBox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedRecommendations.add(recommendation);
            } else {
                selectedRecommendations.remove(recommendation);
            }
        });

        // Desactivar el CheckBox cuando el usuario suelte el dedo
        holder.itemView.setOnClickListener(v -> {
            if (isLongPressed) {
                // Alterna la selección del CheckBox solo si el toque largo está activado
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendationList.size();
    }

    public List<Recommendation> getSelectedRecommendations() {
        return selectedRecommendations;
    }

    static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        TextView recommendationText;
        CheckBox checkBox;

        public RecommendationViewHolder(View itemView) {
            super(itemView);
            recommendationText = itemView.findViewById(R.id.text_recommendation);
            checkBox = itemView.findViewById(R.id.checkbox_select);
        }
    }
}