package com.distlestudio.drone2d.utils;


import com.badlogic.gdx.Gdx;

public class EventManager {
    int event;
    float count;
    float countMax;
    float progress;
    EventListener eventListener;
    boolean doCount;

    public EventManager(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public EventManager(EventListener eventListener, boolean start) {
        this.eventListener = eventListener;
        if (start)
            start();
    }

    public void update(float dt) {
        if (doCount) {
            count -= dt;
            if (count <= 0) {
                float resp = eventListener.onEvent(event);
                if (resp == -1) {
                    doCount = false;
                    progress = 1;
                    return;
                } else if (resp == -2){
                    doCount = true;
                    count = 0;
                    event = 0;
                    update();
                    return;
                }
                countMax = resp;
                count = countMax;
                event++;
            }
            progress = (countMax - count) / countMax;
        }
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public EventManager start() {
        doCount = true;
        count = 0;
        event = 0;
        return this;
    }

    public float getCountMax() {
        return countMax;
    }

    public float getCount() {
        return count;
    }

    public float getProgress() {
        return progress;
    }

    public int getEvent() {
        return Math.max(0, event - 1);
    }
}
