package com.mikel.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class InicioActivity extends AppCompatActivity {

    private ImageView logoApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        //Se oculta la action bar superior
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
        //Animación de aparición para la imagen
        logoApp = findViewById(R.id.imageViewLogo);
        Animation animacionAparecer = AnimationUtils.loadAnimation(getBaseContext(), R.anim.aparecer);
        logoApp.setAnimation(animacionAparecer);
        //Se inicia la actividad inicio y pasados los 4 segundos se muestra la actividad principal.
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(InicioActivity.this, MainActivity.class));
            finish();
        }, 1000);
    }
}