package com.mikel.todolist.Adaptador;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.mikel.todolist.AnadirNuevaTarea;
import com.mikel.todolist.MainActivity;
import com.mikel.todolist.Modelo.Tarea;
import com.mikel.todolist.R;
import com.mikel.todolist.Utils.DatabaseHandler;

import java.util.List;

public class AdaptadorTareas extends RecyclerView.Adapter<AdaptadorTareas.ViewHolder> {

    private List<Tarea> listaTareas;
    private final DatabaseHandler db;
    private final MainActivity actividadPrincipal;

    public AdaptadorTareas(DatabaseHandler db, MainActivity actividadPrincipal) {
        this.db = db;
        this.actividadPrincipal = actividadPrincipal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_tarea, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Tarea tarea = listaTareas.get(position);
        holder.mCheckBoxTarea.setText(tarea.getDescTarea());
        holder.textViewFechaFinRecycler.setText(tarea.getFechaFin());
        holder.mCheckBoxTarea.setChecked(toBoolean(tarea.getEstado()));
        if (holder.mCheckBoxTarea.isChecked()) {
            holder.mCheckBoxTarea.setPaintFlags(holder.mCheckBoxTarea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.mCheckBoxTarea.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.mCheckBoxTarea.setPaintFlags(holder.mCheckBoxTarea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                db.actualizarEstado(tarea.getId(), 1);
            } else {
                holder.mCheckBoxTarea.setPaintFlags(holder.mCheckBoxTarea.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                db.actualizarEstado(tarea.getId(), 0);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.deslizar_desde_derecha);
        holder.itemView.startAnimation(animation);
    }

    /**
     * Convierte un entero a boolean
     * @param n el número a convertir
     * @return boolean
     */
    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    /**
     * Devuelve el context de la actividad.
     * @return Context
     */
    public Context getContext() {
        return actividadPrincipal;
    }

    /**
     * Carga una nueva lista de tareas en el recyclerView
     * @param listaTareas lista a cargar
     */
    public void cargaTareas(List<Tarea> listaTareas) {
        this.listaTareas = listaTareas;
        notifyDataSetChanged();
    }

    /**
     * Borra una determinada tarea
     * @param position posicion de la tarea a borrar
     */
    public void borrarTarea(int position) {
        Tarea tarea = listaTareas.get(position);
        db.borrarTarea(tarea.getId());
        listaTareas.remove(position);
        notifyItemRemoved(position);
    }

    public void borrarTareas(){
        db.borrarTareas();
        listaTareas.clear();
        notifyDataSetChanged();
    }

    /**
     * Editar una determinada tarea
     * @param position posición de la tarea a editar
     */
    public void editarTarea(int position) {
        Tarea tarea = listaTareas.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", tarea.getId());
        bundle.putString("desc_tarea", tarea.getDescTarea());
        bundle.putString("fecha_fin", tarea.getFechaFin());
        AnadirNuevaTarea fragmento = new AnadirNuevaTarea();
        fragmento.setArguments(bundle);
        fragmento.show(actividadPrincipal.getSupportFragmentManager(), AnadirNuevaTarea.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox mCheckBoxTarea;
        TextView textViewFechaFinRecycler;
        ViewHolder(View view) {
            super(view);
            mCheckBoxTarea = view.findViewById(R.id.mCheckBoxTarea);
            textViewFechaFinRecycler = view.findViewById(R.id.textViewFechaFinRecycler);
        }
    }
}
