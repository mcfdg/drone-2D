package com.distlestudio.drone2d.scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public abstract class Scene {

    Vector2 pos_cam = new Vector2();
    ShaderProgram shader;

    public abstract void update();

    public abstract void render(SpriteBatch batch, OrthographicCamera cam);

    public void renderShapes(ShapeRenderer sr, OrthographicCamera cam) {

    }

    public void renderDebug(SpriteBatch batch, OrthographicCamera cam) {

    }

    public void renderOverlay(SpriteBatch batch, OrthographicCamera cam, Texture texture_buffer){

    }

    public void show() {

    }

    public void hide() {

    }

    public void touchDown(int button) {

    }

    public void touchUp() {

    }

    public void onKeyDown(int key) {

    }

    public void dispose() {

    }

    public void setBufferShader(SpriteBatch batch){

    }
}
