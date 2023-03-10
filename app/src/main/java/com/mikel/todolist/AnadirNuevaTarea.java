package com.mikel.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.mikel.todolist.Modelo.Tarea;
import com.mikel.todolist.Utils.DatabaseHandler;
import com.mikel.todolist.Utils.ToastHandler;

import java.util.Calendar;

public class AnadirNuevaTarea extends BottomSheetDialogFragment {

    public static final String TAG = "AnadirNuevaTarea";
    private EditText editTextTarea;
    private Button buttonGuardarTarea;
    private TextView textViewFechaFin;

    private String fechaFin = "";
    private boolean fechaModificada = false;
    private boolean isUpdate = false;

    private Context context;
    private DatabaseHandler db;
    private ViewGroup viewGroupToast;
    private MediaPlayer sonido;

    public static AnadirNuevaTarea newInstance(){
        return new AnadirNuevaTarea();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        isUpdate = false;

        //Comprueba que est?? actualizando la tarea o creando una nueva
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String desc_tarea = bundle.getString("desc_tarea");
            fechaFin = bundle.getString("fecha_fin");
            editTextTarea.setText(desc_tarea);
            textViewFechaFin.setText(fechaFin);
            assert desc_tarea != null;
            if(desc_tarea.length()>0){
                buttonGuardarTarea.setEnabled(true);
                fechaModificada = true;
            }
        }

        //Abre la base de datos
        db = new DatabaseHandler(getActivity());
        db.abreDB();

        //Listener para detectar cambios en el editText, de esta forma si esta vac??o el bot??n estar?? desactivado
        editTextTarea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonGuardarTarea.setEnabled(!s.toString().equals("") && fechaModificada);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Listener para el textView fecha fin, cuando lo clickas aparece un dialogo para seleccionar la fecha final de la tarea, cuando seleccionas la fecha el texto del textView se sobreeescribe.
        textViewFechaFin.setOnClickListener(v -> {
            //Obtienes la fecha actual para pasarsela al dialogo, as?? se abrir?? en la fecha actual.
            Calendar calendar = Calendar.getInstance();

            int MONTH = calendar.get(Calendar.MONTH);
            int YEAR = calendar.get(Calendar.YEAR);
            int DAY = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                /**
                 * Cuando se selecciona una fecha, se cambia el texto del textView y se a??ade la fecha a una variable.
                 * @param view vista mostrada.
                 * @param year  a??o seleccionado
                 * @param month mes seleccionado
                 * @param dayOfMonth d??a del mes seleccionado
                 */
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    textViewFechaFin.setText(dayOfMonth + "/" + month + "/" + year);
                    fechaFin = dayOfMonth + "/" + month +"/"+year;
                    fechaModificada = true;
                    buttonGuardarTarea.setEnabled(!editTextTarea.getText().toString().equals("") && fechaModificada);
                }
            } , YEAR , MONTH , DAY);

            datePickerDialog.show();
        });

        final boolean finalIsUpdate = isUpdate;
        //Listener para el bot??n de guardar, se encarga de comprobar si es una actualizaci??n y inserta una nueva tarea en caso de que no lo sea o actualiza la existente en caso de que si sea una actualizaci??n.
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
                ToastHandler.removeToastMsg();
                ToastHandler.toastMsg(context, LayoutInflater.from(context), null,"Tarea creada");
            }
            dismiss();
            sonido = MediaPlayer.create(getContext(), R.raw.nueva_tarea);
            sonido.start();
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
