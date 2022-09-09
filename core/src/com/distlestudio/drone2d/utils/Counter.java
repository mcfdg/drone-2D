package com.distlestudio.drone2d.utils;

import com.badlogic.gdx.Gdx;

public class Counter {
    ActionListener listener;
    float time;
    float count;
    boolean doCount;

    public Counter(ActionListener listener, float time) {
        this.listener = listener;
        this.time = time;
    }

    public Counter(float time) {
        this.time = time;
    }

    public Counter start() {
        count = time;
        doCount = true;
        return this;
    }

    public void update() {
        if (doCount) {
            count -= Gdx.graphics.getDeltaTime();
            if (count < 0) {
                count = 0;
                doCount = false;
                if (listener != null)
                    listener.action(); // fix this before using listener
            }
        }
    }

    public void action(){
        listener.action();
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getCount() {
        return count;
    }

    public float getProgress() {
        return (time - count) / time;
    }

    public void setProgress(float progress) {
        count = (1 - progress) * time;
    }
}
