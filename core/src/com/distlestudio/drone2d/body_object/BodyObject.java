package com.distlestudio.drone2d.body_object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.utils.Funcs;

public class BodyObject {
    public Body body;
    Sprite sprite;
    Vector2 pos;

    public BodyObject() {

    }

    public void update() {
        pos = body.getPosition().scl(Drone2D.PPM);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        sprite.setPosition(pos.x - sprite.getWidth() / 2, pos.y - sprite.getHeight() / 2);
    }

    public void setVelocity(float vx, float vy) {
        body.setLinearVelocity(vx, vy);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void dispose(World world){
        Funcs.destroyBody(world, body);
    }
}
