package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.scenes.Game;

public class PlayerActor extends Actor {
    boolean touchingLeft, touchingRight;

    static final float TOUCH_CENTER_LEFT_BOUNDARY = .5f;
    static final float TOUCH_CENTER_RIGHT_BOUNDARY = .5f;

    public PlayerActor(Drone drone){
        super(drone);
    }

    @Override
    public void update(Body body) {
        touchingLeft = false;
        touchingRight = false;
        thrust[0] = 0;
        thrust[1] = 0;

        // Can't move after game over
        if(Drone2D.game.phase == Game.Phase.GAME_OVER)
            return;

        for (int i = 0; i < 2; i++) {
            if (Gdx.input.isTouched(i)) {
                if (Gdx.input.getX(i) <= Gdx.graphics.getWidth() * TOUCH_CENTER_LEFT_BOUNDARY)
                    touchingRight = true;
                else if (Gdx.input.getX(i) >= Gdx.graphics.getWidth() * TOUCH_CENTER_RIGHT_BOUNDARY)
                    touchingLeft = true;
                else {
                    touchingLeft = true;
                    touchingRight = true;
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            touchingLeft = true;
            touchingRight = true;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                touchingRight = true;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                touchingLeft = true;
        }

        if (touchingLeft && touchingRight) {
            thrust[0] = 1;
            thrust[1] = 1;
        } else if (touchingLeft) {
            thrust[0] = Drone.ASSISTING_PROPELLER_POWER;
            thrust[1] = 1;
        } else if (touchingRight) {
            thrust[0] = 1;
            thrust[1] = Drone.ASSISTING_PROPELLER_POWER;
        }

        float stabilizingPower = Drone.C_STABILIZING_POWER * (float) Math.pow(Math.abs(body.getAngle()), 2);
        if (body.getAngle() > 0) thrust[0] += stabilizingPower;
        if (body.getAngle() < 0) thrust[1] += stabilizingPower;
    }

    @Override
    public boolean isActingOnPropeller(int propeller) {
        if (propeller == 0)
            return touchingLeft;
        return touchingRight;
    }
}
