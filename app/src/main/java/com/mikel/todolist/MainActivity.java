package com.mikel.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikel.todolist.Adaptador.AdaptadorTareas;
import com.mikel.todolist.Modelo.Tarea;
import com.mikel.todolist.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;

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

        //Creo una instancia de la clase que gestiona la DB y la abro
        db = new DatabaseHandler(this);
        db.abreDB();

        recyclerViewTareas = findViewById(R.id.recyclerViewTareas);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
        adaptadorTareas = new AdaptadorTareas(db,MainActivity.this);
        recyclerViewTareas.setAdapter(adaptadorTareas);

        //ItemTouchHelper para poder editar o borrar las tareas deslizando hacia un lado.
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(adaptadorTareas));
        itemTouchHelper.attachToRecyclerView(recyclerViewTareas);

        fabAnadir = findViewById(R.id.fabAnadir);

        listaTareas = db.obtenerTareas();
        Collections.reverse(listaTareas);

        adaptadorTareas.cargaTareas(listaTareas);

        fabAnadir.setOnClickListener(v -> AnadirNuevaTarea.newInstance().show(getSupportFragmentManager(), AnadirNuevaTarea.TAG));
    }

    /**
     * Listener que se activa cuando se cierra el dialogo/fragmento.
     * @param dialog dialogo
     */
    @Override
    public void handleDialogClose(DialogInterface dialog){
        listaTareas.clear();
        listaTareas = db.obtenerTareas();
        Collections.reverse(listaTareas);
        adaptadorTareas.cargaTareas(listaTareas);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.abreDB();
        adaptadorTareas.cargaTareas(listaTareas);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.cierraDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.cierraDB();
    }

    @Override //menu de la toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public void borrarTareas(MenuItem menuItem){
        adaptadorTareas.borrarTareas();
    }
}