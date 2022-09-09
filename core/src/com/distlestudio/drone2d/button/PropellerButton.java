package com.distlestudio.drone2d.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.distlestudio.drone2d.Res;

public class PropellerButton extends Button {
    Sprite sprite_propeller;
    Sprite sprite_cross;
    float speed;
    float rotation;
    boolean propellerVisible;

    final static float SPEED_MAX = 20;

    public PropellerButton(float x, float y) {
        super(Res.tex_propellerButton, Res.tex_propellerButton_pressed, x, y);
        sprite_propeller = new Sprite(Res.tex_propellerButton_propeller);
        sprite_cross = new Sprite(Res.tex_propellerButton_cross);
    }

    public void update() {
        super.update();
        speed = Math.max(0, speed - SPEED_MAX * Gdx.graphics.getDeltaTime());
        rotation += speed;
        sprite_propeller.setRotation(rotation);
    }

    @Override
    public void press() {
        super.press();
        speed = SPEED_MAX;
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera cam) {
        super.render(batch, cam);

        if (isDown) {
            sprite_propeller.setPosition(cam.position.x + pos.x + 4, cam.position.y + pos.y + 7);
            sprite_cross.setPosition(cam.position.x + pos.x + 5, cam.position.y + pos.y + 8);
        } else {
            sprite_propeller.setPosition(cam.position.x + pos.x + 4, cam.position.y + pos.y + 4);
            sprite_cross.setPosition(cam.position.x + pos.x + 5, cam.position.y + pos.y + 5);
        }

        if (propellerVisible)
            sprite_propeller.draw(batch);
        else
            sprite_cross.draw(batch);
    }

    public void setPropellerVisible(boolean visible) {
        propellerVisible = visible;
    }
}
