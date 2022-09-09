package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.distlestudio.drone2d.Battery;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Goal;
import com.distlestudio.drone2d.Res;

public class PlatformDrone extends Drone implements Goal {

    Vector2 pos_stationary;
    Vector2 pos_platform = new Vector2();
    Vector2 pos_fill = new Vector2();

    Sprite sprite_fill;

    boolean isReached;

    final float CORRECTIVE_FORCE = 20;
    final float MIN_CORRECTIVE_FORCE = 100;
    static final float BATTERY_CAPACITY = 60;
    static final float LINEAR_DAMPING = 1;

    int fillWidth;

    public PlatformDrone(float x, float y, World world) {
        super(x, y, world, new Battery(BATTERY_CAPACITY, 10), Res.definition_platformDrone);
        pos_stationary = new Vector2(x, y);
        body.setFixedRotation(true);
        body.setLinearDamping(LINEAR_DAMPING);
        battery.setPitch(.7f);

        initSprites(new Vector2(49 / 2f, 3), Res.tex_platformDrone, Res.tex_platformDronePropeller);
        sprite_fill = new Sprite(Res.tex_platformDrone_fill);

        pos_platform.set(pos.x - Res.tex_platformDrone.getRegionWidth() / 2f, pos.y - Res.tex_platformDrone.getRegionHeight() / 2f);
        pos_fill.set(pos_platform.x + 19, pos_platform.y + 1);
        sprite_fill.setPosition(pos_fill.x, pos_fill.y);

        fillWidth = sprite_fill.getRegionWidth();
    }

    public void update() {
        super.update();
        pos.set(0, 0);
        pos.mulAdd(body.getPosition(), Drone2D.PPM);
        pos_platform.set(pos.x - Res.tex_platformDrone.getRegionWidth() / 2f, pos.y - Res.tex_platformDrone.getRegionHeight() / 2f);
        pos_fill.set(pos_platform.x + 19, pos_platform.y + 1);
        sprite_fill.setPosition(pos_fill.x, pos_fill.y);
        sprite_fill.setRotation((float) Math.toDegrees(body.getAngle()));
        sprite_fill.setRegionWidth((int) (fillWidth * battery.getFraction()));
        sprite_fill.setSize(sprite_fill.getRegionWidth(), sprite_fill.getRegionHeight());

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

    public void render(SpriteBatch batch) {
        super.render(batch);
        sprite_fill.draw(batch);
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
        return pos_stationary;
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
