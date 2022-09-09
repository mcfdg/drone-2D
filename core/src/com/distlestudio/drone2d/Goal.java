package com.distlestudio.drone2d;

import com.badlogic.gdx.math.Vector2;

public interface Goal {
    boolean isPoint();
    boolean isPlatform();
    boolean isReached();
    Vector2 getPosition();
    void setGoalPosition(Vector2 pos);
}
