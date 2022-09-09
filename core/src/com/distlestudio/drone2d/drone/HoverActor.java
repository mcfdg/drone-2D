package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.physics.box2d.Body;
import com.distlestudio.drone2d.utils.ActionListener;
import com.distlestudio.drone2d.utils.Counter;

public class HoverActor extends Actor{

    public HoverActor(Drone drone, float[] args){
        super(drone, args);
    }

    @Override
    public void update(Body body) {
        thrust[0] = 0;
        thrust[1] = 0;

        if(drone.pos.y < args[1]){
            thrust[0] = 1;
            thrust[1] = 1;
        }
    }

    @Override
    public boolean isActingOnPropeller(int propeller) {
        return false;
    }
}
