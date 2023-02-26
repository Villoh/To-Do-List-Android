package com.mikel.todolist.Tasks;

import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.mikel.todolist.Modelo.Tarea;
import com.mikel.todolist.R;
import com.mikel.todolist.Utils.DatabaseHandler;

/**
 * Tarea Asincrona que se encarga de carga los elementos en el item del recyclerView
 */
public class OnBindTask extends AsyncTask<Void, Void, Void> {

    private final Tarea tarea;
    private final MaterialCheckBox mCheckBoxTarea;
    private final TextView textViewFechaFinRecycler;
    private final DatabaseHandler db;
    private final View itemView;

    public OnBindTask(Tarea tarea, MaterialCheckBox mCheckBoxTarea, TextView textViewFechaFinRecycler, View itemView, DatabaseHandler db) {
        this.tarea = tarea;
        this.mCheckBoxTarea = mCheckBoxTarea;
        this.textViewFechaFinRecycler = textViewFechaFinRecycler;
        this.itemView = itemView;
        this.db = db;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mCheckBoxTarea.setText(tarea.getDescTarea());
        textViewFechaFinRecycler.setText(tarea.getFechaFin());
        mCheckBoxTarea.setChecked(toBoolean(tarea.getEstado()));
        if (mCheckBoxTarea.isChecked()) {
            mCheckBoxTarea.setPaintFlags(mCheckBoxTarea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        mCheckBoxTarea.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckBoxTarea.setPaintFlags(mCheckBoxTarea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                db.actualizarEstado(tarea.getId(), 1);
            } else {
                mCheckBoxTarea.setPaintFlags(mCheckBoxTarea.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                db.actualizarEstado(tarea.getId(), 0);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.deslizar_desde_derecha);
        itemView.startAnimation(animation);
        return null;
    }

    /**
     * Convierte un entero a boolean
     * @param n el número a convertir
     * @return boolean
     */
    private boolean toBoolean(int n) {
        return n != 0;
    }
}
