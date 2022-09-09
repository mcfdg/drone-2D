package com.distlestudio.drone2d.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.scenes.Scene;

public class SceneManager {
    Scene currentScene;
    FrameBuffer buffer;

    static final boolean DO_DEBUG_RENDER = false;

    public SceneManager(){
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Drone2D.WIDTH, Drone2D.HEIGHT, false);
        Gdx.input.setInputProcessor(createInputProcessor());
    }

    public void setScene(Scene scene) {
        if (currentScene != null)
            currentScene.hide();
        scene.show();
        currentScene = scene;
    }

    public void updateScene() {
        currentScene.update();
    }

    public void renderScene(SpriteBatch batch, OrthographicCamera cam, ShapeRenderer sr) {
        buffer.begin();
        currentScene.render(batch, cam);
        buffer.end();

        Texture tex_buffer = buffer.getColorBufferTexture();
        tex_buffer.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        cam.position.set(Drone2D.WIDTH / 2f, Drone2D.HEIGHT / 2f, 0);
        cam.viewportWidth = Drone2D.WIDTH;
        cam.viewportHeight = Drone2D.HEIGHT;
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        currentScene.setBufferShader(batch);
        batch.draw(tex_buffer, 0, Drone2D.HEIGHT, Drone2D.WIDTH, -Drone2D.HEIGHT);
        currentScene.renderOverlay(batch, cam, tex_buffer);
        batch.setShader(null);
        batch.end();

        if(DO_DEBUG_RENDER) {
            currentScene.renderDebug(batch, cam);
            currentScene.renderShapes(sr, cam);
        }
    }

    public InputProcessor createInputProcessor() {
        return new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                currentScene.onKeyDown(keycode);
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                currentScene.touchDown(button);
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                currentScene.touchUp();
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        };
    }
}
