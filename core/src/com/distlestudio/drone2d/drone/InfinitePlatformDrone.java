package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.distlestudio.drone2d.Battery;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Goal;
import com.distlestudio.drone2d.Res;

public class InfinitePlatformDrone extends Drone implements Goal {

    Vector2 pos_stationary;

    boolean isReached;

    final float CORRECTIVE_FORCE = 20;
    final float MIN_CORRECTIVE_FORCE = 100;
    static final float BATTERY_CAPACITY = 100_000;
    static final float LINEAR_DAMPING = 1;

    public InfinitePlatformDrone(float x, float y, World world) {
        super(x, y, world, new Battery(BATTERY_CAPACITY, 10), Res.definition_platformDrone);
        pos_stationary = new Vector2(x, y);
        body.setFixedRotation(true);
        body.setLinearDamping(LINEAR_DAMPING);
        battery.setPitch(.7f);

        initSprites(new Vector2(49 / 2f, 8), Res.tex_infinitePlatformDrone, Res.tex_infinitePlatformDronePropeller);
    }

    public void update() {
        super.update();
        pos.set(0, 0);
        pos.mulAdd(body.getPosition(), Drone2D.PPM);

        if (!battery.isEmpty()) {
            if (!propellerExploded[0] && !propellerExploded[1]) {
                body.applyForce(
                        CORRECTIVE_FORCE * (pos_stationary.x - pos.x),
                        Math.max(MIN_CORRECTIVE_FORCE, CORRECTIVE_FORCE * (pos_stationary.y - pos.y)),
                        body.getWorldCenter().x,
                        body.getWorldCenter().y,
                        true
                );
                animatePropeller(0, 1);
                animatePropeller(1, 1);
            } else {
                body.setFixedRotation(false);
                body.setLinearDamping(0);
                thrust(0, 1);
                thrust(1, 1);
            }
        }
    }

    public void onSuccessfulPlayerCollision() {
        isReached = true;
    }

    @Override
    public boolean isPoint() {
        return false;
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public Vector2 getPosition() {
        return pos;
    }

    @Override
    public void playHurtSound() {
        playHurtSound(.5f, .5f);
    }

    @Override
    public boolean isReached() {
        return isReached;
    }

    @Override
    public void setGoalPosition(Vector2 pos) {
        // Not needed
    }
}
