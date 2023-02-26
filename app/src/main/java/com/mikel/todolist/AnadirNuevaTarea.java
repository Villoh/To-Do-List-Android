package com.mikel.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.mikel.todolist.Modelo.Tarea;
import com.mikel.todolist.Utils.DatabaseHandler;

import java.util.Calendar;
import java.util.Objects;

public class AnadirNuevaTarea extends BottomSheetDialogFragment {

    public static final String TAG = "AnadirNuevaTarea";
    private EditText editTextTarea;
    private Button buttonGuardarTarea;
    private TextView textViewFechaFin;

    private String fechaFin = "";

    private Context context;
    private DatabaseHandler db;

    public static AnadirNuevaTarea newInstance(){
        return new AnadirNuevaTarea();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.anadir_nueva_tarea, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextTarea = view.findViewById(R.id.editTextTarea);
        buttonGuardarTarea = view.findViewById(R.id.buttonGuardarTarea);
        textViewFechaFin = view.findViewById(R.id.textViewFechaFin);

        boolean isUpdate = false;

        //Comprueba que esté actualizando la tarea o creando una nueva
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String desc_tarea = bundle.getString("desc_tarea");
            fechaFin = bundle.getString("fecha_fin");
            editTextTarea.setText(desc_tarea);
            assert desc_tarea != null;
            if(desc_tarea.length()>0){
                buttonGuardarTarea.setEnabled(false);
            }
        }

        //Abre la base de datos
        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        //Listener para detectar cambios en el editText, de esta forma si esta vacío el botón estará desactivado
        editTextTarea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonGuardarTarea.setEnabled(!s.toString().equals(""));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Listener para el textView fecha fin, cuando lo clickas aparece un dialogo para seleccionar la fecha final de la tarea, cuando seleccionas la fecha el texto del textView se sobreeescribe.
        textViewFechaFin.setOnClickListener(v -> {
            //Obtienes la fecha actual para pasarsela al dialogo, así se abrirá en la fecha actual.
            Calendar calendar = Calendar.getInstance();

            int MONTH = calendar.get(Calendar.MONTH);
            int YEAR = calendar.get(Calendar.YEAR);
            int DAY = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                /**
                 * Cuando se selecciona una fecha, se cambia el texto del textView y se añade la fecha a una variable.
                 * @param view vista mostrada.
                 * @param year  año seleccionado
                 * @param month mes seleccionado
                 * @param dayOfMonth día del mes seleccionado
                 */
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    textViewFechaFin.setText(dayOfMonth + "/" + month + "/" + year);
                    fechaFin = dayOfMonth + "/" + month +"/"+year;
                }
            } , YEAR , MONTH , DAY);

            datePickerDialog.show();
        });

        final boolean finalIsUpdate = isUpdate;
        //Listener para el botón de guardar, se encarga de comprobar si es una actualización y inserta una nueva tarea en caso de que no lo sea o actualiza la existente en caso de que si sea una actualización.
        buttonGuardarTarea.setOnClickListener(v -> {
            String descTarea = editTextTarea.getText().toString();
            if(finalIsUpdate){
                db.actualizarDescTarea(bundle.getInt("id"), descTarea);
                db.actualizarFechaFin(bundle.getInt("id"), fechaFin);
            }
            else {
                Tarea tarea = new Tarea();
                tarea.setDescTarea(descTarea);
                tarea.setFechaFin(fechaFin);
                tarea.setEstado(0);
                db.insertarTarea(tarea);
            }
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }
}
