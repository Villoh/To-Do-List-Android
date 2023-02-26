package com.mikel.todolist.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikel.todolist.R;

import java.util.ArrayList;
import java.util.List;

public class ToastHandler {
    private static final List<Toast> mensajes = new ArrayList<>();

    public static void toastMsg(Context context, LayoutInflater inflater, ViewGroup viewGroupToast, String msg){
        Toast mensajeToast = new Toast(context);
        View toastRoot = inflater.inflate(R.layout.toast, viewGroupToast);

        TextView text = toastRoot.findViewById(R.id.text);
        text.setText(msg);

        mensajeToast.setView(toastRoot);
        mensajeToast.setDuration(android.widget.Toast.LENGTH_SHORT);
        mensajeToast.show();
        mensajes.add(mensajeToast);
    }

    public static void removeToastMsg(){
        for(Toast mensaje : mensajes){
            mensaje.cancel();
        }
    }
}
