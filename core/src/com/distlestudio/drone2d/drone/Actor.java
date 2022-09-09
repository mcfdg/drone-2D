package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class Actor {

    public static final int NONE = 0;
    public static final int PLAYER = 1;
    public static final int RANDOM_AI = 2;
    public static final int HOVER = 3;

    public float[] thrust = new float[2];

    float[] args;

    Drone drone;

    public Actor(Drone drone){
        this.drone = drone;
    }

    public Actor(Drone drone, float[] args){
        this.drone = drone;
        this.args = args;
    }

    public abstract void update(Body body);

    public abstract boolean isActingOnPropeller(int propeller);

    public float getAction(int propeller) {
        return thrust[propeller];
    }
}
