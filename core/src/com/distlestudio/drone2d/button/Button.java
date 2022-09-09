package com.distlestudio.drone2d.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.utils.Funcs;

public class Button {
    public Vector2 pos;
    boolean isDown;
    boolean isDownBefore;
    TextureRegion tex;
    TextureRegion tex_pressed;

    public Button(TextureRegion tex, TextureRegion tex_pressed, float x, float y) {
        this.tex = tex;
        this.tex_pressed = tex_pressed;
        pos = new Vector2(x, y);
    }

    public void action() {

    }

    public void update() {
        isDownBefore = isDown;
        isDown = false;

        if (Gdx.input.isTouched()) {
            if (touchOnButton()) {
                isDown = true;
            }
        }
    }

    public void touchUp() {
        if (isDownBefore && Drone2D.checkFreeTouchAction())
            action();
    }

    public boolean touchOnButton() {
        return Funcs.pointInRectangle(Drone2D.getTouchX(), Drone2D.getTouchY(), pos.x, pos.y,
                tex.getRegionWidth(), tex.getRegionHeight());
    }

    public void render(SpriteBatch batch, OrthographicCamera cam) {
        if (isDown)
            batch.draw(tex_pressed, cam.position.x + pos.x, cam.position.y + pos.y + 4);
        else
            batch.draw(tex, cam.position.x + pos.x, cam.position.y + pos.y);
    }

    public void press() {
        if (!isDown)
            isDown = true;
    }
}
