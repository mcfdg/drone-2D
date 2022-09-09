package com.distlestudio.drone2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.distlestudio.drone2d.scenes.Game;
import com.distlestudio.drone2d.utils.Funcs;

public class Item implements Goal {
    World world;
    Body body;
    public Vector2 pos;
    public Vector2 pos_goal;
    boolean isDisposed;
    boolean isCollected;
    Sprite sprite;

    float progress_float;
    public int type;

    public Item(float x, float y, World world) {
        pos_goal = new Vector2(x, y);
        pos = new Vector2(x, y);
        this.world = world;
        createBody();
        type = (int) (Math.random() * Res.itemDefinitions.length);
        sprite = new Sprite(Res.itemDefinitions[type].getRegion());
        sprite.setPosition((int) (pos.x - sprite.getWidth() / 2), (int) (pos.y - sprite.getHeight() / 2));
        progress_float = (float)(Math.random() * Math.PI * 2);
    }

    void createBody() {
        body = world.createBody(Res.bodyDef_dot);
        body.createFixture(Res.fixtureDef_dot);
        body.setTransform(pos.x * Drone2D.MPP, pos.y * Drone2D.MPP, 0);
        body.setUserData(this);
    }

    public void render(SpriteBatch batch) {
        progress_float = (progress_float + Gdx.graphics.getDeltaTime() * 5) % ((float) Math.PI * 2);
        sprite.setPosition((int) (pos.x - sprite.getWidth() / 2) + .5f, (int) (pos.y - sprite.getHeight() / 2 + 2 * (float) Math.cos(progress_float)));
        sprite.draw(batch);
    }

    public void onCollect() {
        isCollected = true;
        if (!isDisposed) {
            isDisposed = true;
            Game.itemsToRemove.add(this);
        }
    }

    public void dispose() {
        body = Funcs.destroyBody(world, body);
    }

    @Override
    public boolean isPoint() {
        return true;
    }

    @Override
    public boolean isPlatform() {
        return false;
    }

    @Override
    public Vector2 getPosition() {
        return pos_goal;
    }

    @Override
    public boolean isReached() {
        return isCollected;
    }

    @Override
    public void setGoalPosition(Vector2 pos) {
        pos_goal.set(pos);
    }
}
