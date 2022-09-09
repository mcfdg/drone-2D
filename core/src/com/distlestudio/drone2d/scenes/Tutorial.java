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
import com.distlestudio.drone2d.drone.Drone;
import com.distlestudio.drone2d.utils.ActionListener;
import com.distlestudio.drone2d.utils.Counter;

import java.util.ArrayList;

public class Tutorial extends Scene {

    World world;

    ArrayList<Drone> drones = new ArrayList<>();
    ArrayList<Drone> dronesToRemove = new ArrayList<>();

    static final int PLAYINGFIELD_WIDTH = (int) (Drone2D.WIDTH * 1.5f);
    static final int PLAYINGFIELD_HEIGHT = Drone2D.HEIGHT;

    public Tutorial() {
        world = new World(new Vector2(0, Drone2D.GRAVITY), true);
        world.setContactListener(new B2DContactListener());
    }

    @Override
    public void update() {
        world.step(Gdx.graphics.getDeltaTime(), 3, 8);

        for (Drone drone : drones) {
            drone.update();
            if (drone.leftPlayingField(pos_cam, PLAYINGFIELD_WIDTH, PLAYINGFIELD_HEIGHT))
                dronesToRemove.add(drone);
        }

        for (Drone drone : dronesToRemove)
            drone.dispose(world);

        drones.removeAll(dronesToRemove);
        dronesToRemove.clear();

        if (Gdx.input.justTouched())
            Drone2D.sceneManager.setScene(Drone2D.game);
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

        for (Drone drone : drones)
            drone.render(batch);

        batch.end();
    }

    @Override
    public void setBufferShader(SpriteBatch batch) {
        batch.setShader(Res.shader_palette);
        Res.shader_palette.setUniform3fv("colors", Res.paletteTutorialFlat, 0, 12);
    }
}
