package com.distlestudio.drone2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.distlestudio.drone2d.drone.Drone;
import com.distlestudio.drone2d.scenes.Cover;
import com.distlestudio.drone2d.scenes.Game;
import com.distlestudio.drone2d.scenes.SceneManager;
import com.distlestudio.drone2d.utils.DataManager;

public class Drone2D extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer sr;
    private OrthographicCamera cam;

    private static Vector2 tap;
    private static boolean touchActionTaken;

    public static BitmapFont font;
    public static Cover cover;
    public static Game game;
    public static SceneManager sceneManager;
    public static DataManager dataManager;
    public static GameData gameData;

    public static final int WIDTH = 135;
    public static int HEIGHT = 250; // Used in desktop launch
    public static float ASPECT_RATIO;
    public static final float PPM = 40; // Pixels per meter
    public static final float MPP = 1 / PPM; // Meters per pixel
    public static final float GRAVITY = -9.81f; // In meters per second^2
    public static final int FONT_HEIGHT = 6; // In meters per second^2

    @Override
    public void create() {
        HEIGHT = (int)(WIDTH * (float)(Gdx.graphics.getHeight()) / Gdx.graphics.getWidth());
        ASPECT_RATIO = (float)WIDTH / HEIGHT;

        Res.load(); // Initialize resources

        batch = new SpriteBatch();
        sr = new ShapeRenderer();

        dataManager = DataManager.getInstance();
        dataManager.initializeGameData();
        gameData = dataManager.gameData;

        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        Drone2D.font.setColor(Res.Color_.FOREGROUND);
        Drone2D.font.setUseIntegerPositions(true);

        cover = new Cover();
        game = new Game();
        sceneManager = new SceneManager();
        sceneManager.setScene(game);

        cam = new OrthographicCamera(WIDTH, HEIGHT);

        tap = new Vector2();

        ShaderProgram.pedantic = false;
    }

    public void update() {
        touchActionTaken = false;

        sceneManager.updateScene();

        tap.set(
                (Gdx.input.getX() / (float)Gdx.graphics.getWidth() - .5f) * WIDTH,
                ((Gdx.graphics.getHeight() - Gdx.input.getY()) / (float)(Gdx.graphics.getHeight()) - .5f) * HEIGHT
        );
    }

    @Override
    public void render() {
        update();

        sceneManager.renderScene(batch, cam, sr);
    }

    @Override
    public void dispose() {
        batch.dispose();
        dataManager.saveData();
    }

    public static float getTouchX(){
        return tap.x;
    }

    public static float getTouchY(){
        return tap.y;
    }

    public static boolean checkFreeTouchAction(){
        if(!touchActionTaken) {
            touchActionTaken = true;
            return true;
        }
        return false;
    }

    public void addCoins(int coinsToAdd){
        gameData.coins += coinsToAdd;
    }
}
