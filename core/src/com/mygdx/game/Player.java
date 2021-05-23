package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Player {
    Animacion animacion = new Animacion(16,
            new Texture("express_0.png"),
            new Texture("express_1.png"),
            new Texture("express_0.png")
    );

    float x, y, w, h, v;
    List<Habilidad> habilidad = new ArrayList<>();
    int vidas = 3;
    int puntos = 0;
    boolean muerto = false;
    Temporizador temporizadorFireRate = new Temporizador(20);
    Temporizador temporizadorRespawn = new Temporizador(120, false);

    Player() {
        x = 100;
        y = 100;
        w = 40 * 3;
        h = 19 * 4;
        v = 5;
    }

    void update() {
        for (Habilidad habilidad : habilidad) habilidad.update();

        if (Gdx.input.isKeyPressed(Input.Keys.D)) x += v;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= v;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) y += v;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= v;

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && temporizadorFireRate.suena() && !muerto) {
            habilidad.add(new Habilidad(x + w / 2, y + h));
        }

        if (x < 0) x = 0;

        if (temporizadorRespawn.suena()) {
            muerto = false;
        }
    }

    void render(SpriteBatch batch) {
        batch.draw(animacion.obtenerFrame(), x, y, w, h);
        for (Habilidad habilidad : habilidad) habilidad.render(batch);
    }

    public void morir() {
        vidas--;
        muerto = true;
        temporizadorRespawn.activar();
    }
}