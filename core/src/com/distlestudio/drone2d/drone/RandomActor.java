package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.physics.box2d.Body;
import com.distlestudio.drone2d.utils.ActionListener;
import com.distlestudio.drone2d.utils.Counter;

public class RandomActor extends Actor {

    Counter counter;
    int direction;

    public RandomActor(Drone drone) {
        super(drone);
        counter = new Counter(new ActionListener() {
            @Override
            public void action() {
                changeDirection();
                counter.setTime(.25f);
                counter.start();
            }
        }, .5f).start();
        direction = 2;
    }

    @Override
    public void update(Body body) {
        counter.update();
        switch (direction) {
            case 0:
                thrust[0] = Drone.ASSISTING_PROPELLER_POWER;
                thrust[1] = 1;
                break;
            case 1:
                thrust[0] = 1;
                thrust[1] = Drone.ASSISTING_PROPELLER_POWER;
                break;
            case 2:
                thrust[0] = 1;
                thrust[1] = 1;
                break;
        }
    }

    public void changeDirection() {
        float rnd = (float)Math.random();
        if(rnd < .5f)
            direction = 2;
        else if(rnd < .75)
            direction = 1;
        else
            direction = 0;
    }

    @Override
    public boolean isActingOnPropeller(int propeller) {
        return false;
    }
}
