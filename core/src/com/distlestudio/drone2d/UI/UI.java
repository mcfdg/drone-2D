package com.distlestudio.drone2d.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class UI {

    boolean isVisible;
    float progress;
    float progressSpeed = 2;

    public void update() {
        if (!isVisible) return;
        progress = Math.min(progress + Gdx.graphics.getDeltaTime() * progressSpeed, 1);
        update_visible();
    }

    abstract void update_visible();

    public void render(SpriteBatch batch, OrthographicCamera cam) {
        if (!isVisible) return;
        render_visible(batch, cam);
    }

    abstract void render_visible(SpriteBatch batch, OrthographicCamera cam);

    public void touchUp() {

    }

    public void show() {
        isVisible = true;
        progress = 0;
    }

    public void hide() {
        isVisible = false;
    }
}
