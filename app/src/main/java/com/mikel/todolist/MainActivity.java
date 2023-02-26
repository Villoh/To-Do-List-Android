package com.mikel.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikel.todolist.Adaptador.AdaptadorTareas;
import com.mikel.todolist.Modelo.Tarea;
import com.mikel.todolist.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private RecyclerView recyclerViewTareas;
    private FloatingActionButton fabAnadir;
    private AdaptadorTareas adaptadorTareas;
    
    private DatabaseHandler db;


    private List<Tarea> listaTareas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        recyclerViewTareas = findViewById(R.id.recyclerViewTareas);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
        adaptadorTareas = new AdaptadorTareas(db,MainActivity.this);
        recyclerViewTareas.setAdapter(adaptadorTareas);

//        ItemTouchHelper itemTouchHelper = new
//                ItemTouchHelper(new RecyclerItemTouchHelper(adaptadorTareas));
//        itemTouchHelper.attachToRecyclerView(recyclerViewTareas);

        fabAnadir = findViewById(R.id.fabAnadir);

        listaTareas = db.obtenerTareas();
        Collections.reverse(listaTareas);

        adaptadorTareas.cargaTareas(listaTareas);

        fabAnadir.setOnClickListener(v -> AnadirNuevaTarea.newInstance().show(getSupportFragmentManager(), AnadirNuevaTarea.TAG));
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        listaTareas = db.obtenerTareas();
        Collections.reverse(listaTareas);
        adaptadorTareas.cargaTareas(listaTareas);
        adaptadorTareas.notifyDataSetChanged();
    }
}