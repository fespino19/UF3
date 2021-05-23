package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Jdark extends ApplicationAdapter {
    SpriteBatch batch;
    BitmapFont font;
    Fondo fondo;
    Player player;
    List<Enemigo> enemigos;
    List<Habilidad> disparosAEliminar;
    List<Enemigo> enemigosAEliminar;
    Temporizador temporizadorNuevoEnemigo;
    ScoreBoard scoreboard;
    boolean gameover;


    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(2f);

        inicializarJuego();
    }

    void inicializarJuego(){
        fondo = new Fondo();
        player = new Player();
        enemigos = new ArrayList<>();
        temporizadorNuevoEnemigo = new Temporizador(120);
        disparosAEliminar = new ArrayList<>();
        enemigosAEliminar = new ArrayList<>();
        scoreboard = new ScoreBoard();

        gameover = false;
    }

    void update() {
        Temporizador.framesJuego += 1;

        if (temporizadorNuevoEnemigo.suena()) enemigos.add(new Enemigo());

        if(!gameover) player.update();

        for (Enemigo enemigo : enemigos) enemigo.update();              // enemigos.forEach(Enemigo::update);

        for (Enemigo enemigo : enemigos) {
            for (Habilidad disparo : player.habilidad) {
                if (Utils.solapan(disparo.x, disparo.y, disparo.w, disparo.h, enemigo.x, enemigo.y, enemigo.w, enemigo.h)) {
                    disparosAEliminar.add(disparo);
                    enemigosAEliminar.add(enemigo);
                    player.puntos++;
                    break;
                }
            }

            if (!gameover && !player.muerto && Utils.solapan(enemigo.x, enemigo.y, enemigo.w, enemigo.h, player.x, player.y, player.w, player.h)) {
                player.morir();
                if (player.vidas == 0){
                    gameover = true;
                }
            }

            if (enemigo.x < -enemigo.w) enemigosAEliminar.add(enemigo);
        }

        for (Habilidad habilidad : player.habilidad)
            if (habilidad.x > 640)
                disparosAEliminar.add(habilidad);

        for (Habilidad habilidad : disparosAEliminar) player.habilidad.remove(habilidad);       // disparosAEliminar.forEach(disparo -> jugador.disparos.remove(disparo));
        for (Enemigo enemigo : enemigosAEliminar) enemigos.remove(enemigo);               // enemigosAEliminar.forEach(enemigo -> enemigos.remove(enemigo));
        disparosAEliminar.clear();
        enemigosAEliminar.clear();

        if(gameover) {
            int result = scoreboard.update(player.puntos);
            if(result == 1) {
                inicializarJuego();
            } else if (result == 2) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();

        batch.begin();
        fondo.render(batch);
        player.render(batch);
        for (Enemigo enemigo : enemigos) enemigo.render(batch);  // enemigos.forEach(e -> e.render(batch));
        font.draw(batch, "" + player.vidas, 590, 440);
        font.draw(batch, "" + player.puntos, 30, 440);

        if (gameover){
            scoreboard.render(batch, font);
        }
        batch.end();
    }
}