package com.mikel.todolist;

import android.app.Activity;
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
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.mikel.todolist.Modelo.Tarea;
import com.mikel.todolist.Utils.DatabaseHandler;

import java.util.Objects;

public class AnadirNuevaTarea extends BottomSheetDialogFragment {

    public static final String TAG = "AnadirNuevaTarea";
    private EditText editTextTarea;
    private Button buttonGuardarTarea;

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

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("desc_tarea");
            editTextTarea.setText(task);
            assert task != null;
            if(task.length()>0){
                buttonGuardarTarea.setEnabled(false);
            }
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

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

        final boolean finalIsUpdate = isUpdate;
        buttonGuardarTarea.setOnClickListener(v -> {
            String texto = editTextTarea.getText().toString();
            if(finalIsUpdate){
                db.actualizarDescTarea(bundle.getInt("id"), texto);
            }
            else {
                Tarea tarea = new Tarea();
                tarea.setDescTarea(texto);
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
