package com.distlestudio.drone2d.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.distlestudio.drone2d.B2DContactListener;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.drone.Actor;
import com.distlestudio.drone2d.drone.Drone;
import com.distlestudio.drone2d.drone.PlayerDrone;
import com.distlestudio.drone2d.utils.ActionListener;
import com.distlestudio.drone2d.utils.Counter;

import java.util.ArrayList;

public class Cover extends Scene {
    World world;
    World world_background;

    ArrayList<Drone> drones = new ArrayList<>();
    ArrayList<Drone> dronesToRemove = new ArrayList<>();
    ArrayList<Drone> backgroundDrones = new ArrayList<>();
    ArrayList<Drone> backgroundDronesToRemove = new ArrayList<>();

    Counter counter_spawnDrones;
    Counter counter_blink;
    boolean showText;

    static final int PLAYINGFIELD_WIDTH = (int) (Drone2D.WIDTH * 1.5f);
    static final int PLAYINGFIELD_HEIGHT = Drone2D.HEIGHT;

    public Cover() {
        world = new World(new Vector2(0, Drone2D.GRAVITY), true);
        world.setContactListener(new B2DContactListener());

        world_background = new World(new Vector2(0, Drone2D.GRAVITY), true);
        world_background.setContactListener(new B2DContactListener());

        counter_spawnDrones = new Counter(new ActionListener() {
            @Override
            public void action() {
                spawnDrone();
                counter_spawnDrones.start();
            }
        }, .07f).start();

        counter_spawnDrones.action();

        counter_blink = new Counter(new ActionListener() {
            @Override
            public void action() {
                showText = !showText;
                counter_blink.start();
            }
        }, 2f).start();
    }

    @Override
    public void update() {
        world.step(Gdx.graphics.getDeltaTime(), 3, 8);
        world_background.step(Gdx.graphics.getDeltaTime(), 3, 8);

        for (Drone drone : drones) {
            drone.update();
            if (drone.leftPlayingField(pos_cam, PLAYINGFIELD_WIDTH, PLAYINGFIELD_HEIGHT))
                dronesToRemove.add(drone);
        }

        for (Drone drone : backgroundDrones) {
            drone.update();
            if (drone.leftPlayingField(pos_cam, PLAYINGFIELD_WIDTH, PLAYINGFIELD_HEIGHT))
                backgroundDronesToRemove.add(drone);
        }

        for (Drone drone : dronesToRemove)
            drone.dispose(world);

        drones.removeAll(dronesToRemove);
        dronesToRemove.clear();

        for (Drone drone : backgroundDronesToRemove)
            drone.dispose(world_background);

        backgroundDrones.removeAll(backgroundDronesToRemove);
        backgroundDronesToRemove.clear();

        if (Gdx.input.justTouched())
            Drone2D.sceneManager.setScene(Drone2D.game);

        counter_spawnDrones.update();
        counter_blink.update();
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera cam) {
        Gdx.gl.glClearColor(Res.Color_.BACKGROUND.r, Res.Color_.BACKGROUND.g, Res.Color_.BACKGROUND.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.position.set(pos_cam.x, pos_cam.y, 0);
        cam.viewportWidth = Drone2D.WIDTH;
        cam.viewportHeight = Drone2D.HEIGHT;
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        for (Drone drone : backgroundDrones)
            drone.render(batch);

        for (Drone drone : drones)
            drone.render(batch);

        if (showText)
            batch.draw(Res.tex_press_to_start, -Res.tex_press_to_start.getRegionWidth() / 2f, -Res.tex_press_to_start.getRegionHeight() / 2f);
        batch.end();
    }

    static final float DISTANCE_BETWEEN_SPAWNING = 25;

    public void spawnDrone() {
        boolean inBackground = false;
        if (Math.random() < .65)
            inBackground = true;

        Vector2 pos_newDrone = new Vector2(-PLAYINGFIELD_WIDTH / 2f + PLAYINGFIELD_WIDTH * (float) Math.random(), -PLAYINGFIELD_HEIGHT / 2f - 5);

        if (inBackground) {
            for (Drone drone : backgroundDrones)
                if (drone.pos.dst(pos_newDrone) < DISTANCE_BETWEEN_SPAWNING)
                    return;
        } else {
            for (Drone drone : drones)
                if (drone.pos.dst(pos_newDrone) < DISTANCE_BETWEEN_SPAWNING)
                    return;
        }

        PlayerDrone newDrone;

        if (inBackground) {
            newDrone = new PlayerDrone(pos_newDrone.x, pos_newDrone.y, world_background, true);
            backgroundDrones.add(newDrone);
        } else {
            newDrone = new PlayerDrone(pos_newDrone.x, pos_newDrone.y, world);
            drones.add(newDrone);
        }

        newDrone.setActor(Actor.RANDOM_AI);
    }
}
