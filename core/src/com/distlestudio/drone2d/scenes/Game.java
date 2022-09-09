package com.distlestudio.drone2d.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.distlestudio.drone2d.*;
import com.distlestudio.drone2d.UI.GameOverUI;
import com.distlestudio.drone2d.UI.PlayUI;
import com.distlestudio.drone2d.UI.StartUI;
import com.distlestudio.drone2d.body_object.BodyObject;
import com.distlestudio.drone2d.button.Button;
import com.distlestudio.drone2d.drone.*;
import com.distlestudio.drone2d.effect.Effect;
import com.distlestudio.drone2d.utils.ActionListener;

import java.util.ArrayList;

public class Game extends Scene {

    public PlayerDrone playerDrone;
    World world;
    Box2DDebugRenderer b2dr;

    ArrayList<Drone> environmentDrones = new ArrayList<>();
    ArrayList<Goal> goals = new ArrayList<>();
    ArrayList<Goal> goalsToRemove = new ArrayList<>();
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<Button> buttons = new ArrayList<>();
    ArrayList<BatteryUpgrade> batteryUpgrades = new ArrayList<>();
    ArrayList<BatteryUpgrade> batteryUpgradesToRemove = new ArrayList<>();
    public ArrayList<Integer> collectedItems = new ArrayList<>();
    public ArrayList<BodyObject> bodyObjects = new ArrayList<>();
    public ArrayList<Effect> effects = new ArrayList<>();

    public static ArrayList<Item> itemsToRemove = new ArrayList<>();

    int platformsCreated;
    int currentPalette;
    int nextPalette;

    float angle_newGoal;

    Vector2 pos_nextGoal;
    Vector2 pos_cam_dst = new Vector2();

    Paralax paralax;
    StartUI startUI;
    GameOverUI gameOverUI;
    PlayUI playUI;

    boolean showGameOverUI;
    boolean playPropellerSound;

    public int phase = -1;

    long idPropellerSound = -1;

    static final float CAM_LERP = .2f;
    static final float START_HOVER_HEIGHT = 50;
    static final float PALETTE_SWAP_SPEED = 1.8f;

    static final int DISTANCE_BETWEEN_GOALS = (int)(Drone2D.WIDTH * .4);
    static final int MIN_GOALS = 5;

    float progress_paletteSwap;

    static public class Phase {
        public static final int START = 0;
        public static final int PLAY = 1;
        public static final int GAME_OVER = 2;
    }

    public Game() {
        world = new World(new Vector2(0, Drone2D.GRAVITY), true);
        b2dr = new Box2DDebugRenderer();
        world.setContactListener(new B2DContactListener());

        paralax = new Paralax();
        gameOverUI = new GameOverUI();
        startUI = new StartUI();
        playUI = new PlayUI();

//        Res.music_propeller.play();
//        Res.music_propeller.setLooping(true);
//        Res.music_propeller.setVolume(0);

        shader = Res.shader_palette;
    }

    @Override
    public void update() {
        world.step(Gdx.graphics.getDeltaTime(), 3, 8);
        playerDrone.update();

        paralax.update(pos_cam);

        for (Drone drone : environmentDrones)
            drone.update();

        for (Button button : buttons)
            button.update();

        for (Effect effect : effects)
            effect.update();

        for (BodyObject bodyObject : bodyObjects)
            bodyObject.update();

        for (BatteryUpgrade batteryUpgrade : batteryUpgrades) {
            batteryUpgrade.update();
            if (batteryUpgrade.isCollected) {
                batteryUpgradesToRemove.add(batteryUpgrade);
                batteryUpgrade.dispose();
            }
        }

        batteryUpgrades.removeAll(batteryUpgradesToRemove);
        batteryUpgradesToRemove.clear();

        for (Item item : itemsToRemove)
            item.dispose();

        items.removeAll(itemsToRemove);
        itemsToRemove.clear();

        for (Goal goal : goals) {
            if (goal.isReached()) {
                for (int i = 0; i <= goals.indexOf(goal); i++)
                    goalsToRemove.add(goals.get(i));
            }
        }

        goals.removeAll(goalsToRemove);
        goalsToRemove.clear();

        if (goals.size() < MIN_GOALS)
            createNewGoals(Math.max(1, playerDrone.battery.level));

        pos_cam.lerp(pos_cam_dst, CAM_LERP);

        pos_cam.set(Math.round(pos_cam.x), pos_cam.y);

        Res.sound_propeller.setVolume(idPropellerSound, playerDrone.getVolume());
        Res.sound_propeller.setPitch(idPropellerSound, playerDrone.getPitch());

        // Update phases and their respective UIs

        switch (phase) {
            case Phase.START:
                updateStart();
                break;
            case Phase.PLAY:
                updatePlay();
                break;
            case Phase.GAME_OVER:
                updateGameOver();
                break;
        }

        startUI.update();
        playUI.update();
        gameOverUI.update();

        playPropellerSound = false;
        for (int i = 0; i < 2; i++) {
            if (playerDrone.isThrusting(i)) {
                playUI.pressPropellerButton(i);
                playPropellerSound = true;
            }
        }

        Res.pe_spark.update(Gdx.graphics.getDeltaTime());
        Res.pe_wind.update(Gdx.graphics.getDeltaTime());

        // Palette swap
        if (currentPalette != nextPalette) {
            progress_paletteSwap += PALETTE_SWAP_SPEED * Gdx.graphics.getDeltaTime();
            if (progress_paletteSwap >= 1) {
                currentPalette = nextPalette;
                progress_paletteSwap = 0;
            }
        }

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && Drone2D.checkFreeTouchAction())
            setPhase(Phase.START);
    }

    public void setPhase(int phase) {

        // Ending previous phase

        switch (this.phase) {
            case Phase.START:

                startUI.hide();
                playerDrone.stopHover();
                playerDrone.setInfiniteBattery(false);

                break;
            case Phase.PLAY:

                playUI.hide();

                break;

            case Phase.GAME_OVER:

                gameOverUI.hide();
                showGameOverUI = false;

                break;
        }

        // Setting up new phase

        switch (phase) {
            case Phase.START:

                reset();

                createPlayerDrone();

                startUI.show();

                break;
            case Phase.PLAY:

                if (idPropellerSound == -1)
                    idPropellerSound = Res.sound_propeller.loop();

                playerDrone.setActor(Actor.PLAYER);
                playerDrone.battery.setEnergy(0);

                playUI.show();

                break;
            case Phase.GAME_OVER:

                setPalette(0);
                gameOverUI.show();

                break;
        }

        this.phase = phase;
    }

    public void updateStart() {
        pos_cam_dst.set(.1f, START_HOVER_HEIGHT); // to avoid pixel squishing at (0,0)
    }

    public void updatePlay() {
        pos_nextGoal = goals.get(0).getPosition();
        pos_cam_dst.set((pos_nextGoal.x + playerDrone.pos.x) / 2, (pos_nextGoal.y + playerDrone.pos.y) / 2);
        pos_cam_dst.add(.1f, 0); // to avoid pixel squishing at (0,0)

        if (playerDrone.leftPlayingField(pos_cam))
            gameOver(0);
    }

    public void updateGameOver() {

    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera cam) {
        Gdx.gl.glClearColor(Res.Color_.BACKGROUND.r, Res.Color_.BACKGROUND.g, Res.Color_.BACKGROUND.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.position.set(pos_cam.x, pos_cam.y, 0);
        cam.viewportWidth = Drone2D.WIDTH;
        cam.viewportHeight = Drone2D.HEIGHT;
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        paralax.render(batch, pos_cam);

        Res.pe_spark.draw(batch);
        Res.pe_wind.draw(batch);

        playerDrone.render(batch);

        for (Drone drone : environmentDrones)
            drone.render(batch);
        for (Item item : items)
            item.render(batch);
        for (BatteryUpgrade batteryUpgrade : batteryUpgrades)
            batteryUpgrade.render(batch);
        for (Effect effect : effects)
            effect.render(batch);
        for (BodyObject bodyObject : bodyObjects)
            bodyObject.render(batch);

        startUI.render(batch, cam);
        playUI.render(batch, cam);
        gameOverUI.render(batch, cam);

        batch.end();
    }

    @Override
    public void renderDebug(SpriteBatch batch, OrthographicCamera cam) {
        cam.position.set(pos_cam.x * Drone2D.MPP, pos_cam.y * Drone2D.MPP, 0);
        cam.viewportWidth = Drone2D.WIDTH * Drone2D.MPP;
        cam.viewportHeight = Drone2D.HEIGHT * Drone2D.MPP;
        cam.update();
        b2dr.render(world, cam.combined);
    }

    @Override
    public void renderShapes(ShapeRenderer sr, OrthographicCamera cam) {
        cam.position.set(pos_cam.x, pos_cam.y, 0);
        cam.viewportWidth = Drone2D.WIDTH;
        cam.viewportHeight = Drone2D.HEIGHT;
        cam.update();
        sr.setProjectionMatrix(cam.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        playerDrone.renderShapes(sr);
        sr.end();
    }

    public void setPalette(int level) {
        nextPalette = Math.min(Res.palette.length - 1, level);
    }

    @Override
    public void renderOverlay(SpriteBatch batch, OrthographicCamera cam, Texture texture_buffer) {
        if (currentPalette != nextPalette) {
            batch.setShader(Res.shader_paletteTransition);
            Res.shader_paletteTransition.setUniform3fv("colors",
                    Res.paletteFlat[nextPalette], 0, 12);
            Res.shader_paletteTransition.setUniformf("r", progress_paletteSwap);
            Res.shader_paletteTransition.setUniformf("aratio", Drone2D.ASPECT_RATIO);

            batch.draw(texture_buffer, 0, Drone2D.HEIGHT, Drone2D.WIDTH, -Drone2D.HEIGHT);
        }
    }

    @Override
    public void setBufferShader(SpriteBatch batch) {
        batch.setShader(Res.shader_palette);
        Res.shader_palette.setUniform3fv("colors", Res.paletteFlat[currentPalette], 0, 12);
    }

    public void createPlayerDrone() {
        playerDrone = new PlayerDrone(0, START_HOVER_HEIGHT, world);
        playerDrone.setActor(Actor.PLAYER);
        playerDrone.setToHover(0, START_HOVER_HEIGHT);
        playerDrone.setInfiniteBattery(true);
        playerDrone.battery.setActionListener(new ActionListener() {
            @Override
            public void action() {
                setPalette(playerDrone.battery.level);
            }
        });
        playerDrone.battery.setZero();
    }

    public void createNewGoals(int size) {
        createNewGoal(0);
        for (int i = 0; i < size; i++)
            createNewGoal(1);
    }

    static final float OFFSET_ITEM = 50;

    public void createNewGoal(int type) {
        Vector2 pos_newGoal = new Vector2(DISTANCE_BETWEEN_GOALS, 0);
        pos_newGoal.rotateRad(angle_newGoal);
        float angle_nextNewGoal = angle_newGoal + (MathUtils.random(-(float) Math.PI / 4f, (float) Math.PI / 4f));

        // Determine last goal and its position or else create a fake goal
        Goal lastGoal;
        if (goals.size() > 0) {
            lastGoal = goals.get(goals.size() - 1);
            pos_newGoal.add(lastGoal.getPosition());
        } else {
            lastGoal = createEmptyGoal(Vector2.Zero, true);
            pos_newGoal.set(lastGoal.getPosition());
        }

        // Create new goal
        if (type == 0) {
            Drone newDrone;

            if (platformsCreated % 5 == 0) {
                newDrone = new InfinitePlatformDrone(pos_newGoal.x, pos_newGoal.y, world);
                batteryUpgrades.add(new BatteryUpgrade((InfinitePlatformDrone) newDrone, world));
            } else
                newDrone = new PlatformDrone(pos_newGoal.x, pos_newGoal.y, world);

            goals.add((Goal) newDrone);
            environmentDrones.add(newDrone);

            platformsCreated++;
        } else if (type == 1) {
            Vector2 pos_itemBlob = new Vector2(pos_newGoal);
            float angle_offset = (angle_newGoal + angle_nextNewGoal) / 2 + (float) Math.PI * .5f;
            if (Math.sin(angle_offset) < 0) angle_offset += Math.PI;
            pos_itemBlob.add(OFFSET_ITEM * (float) Math.cos(angle_offset),
                    OFFSET_ITEM * (float) Math.sin(angle_offset));

            Item item = createItemBlob(pos_itemBlob);
            item.setGoalPosition(pos_newGoal);
            goals.add(item);
        }
        angle_newGoal = angle_nextNewGoal;
    }

    Goal createEmptyGoal(final Vector2 pos, final boolean isPlatform) { // TODO: is final okay?
        return new Goal() {
            @Override
            public boolean isPoint() {
                return false;
            }

            @Override
            public boolean isPlatform() {
                return isPlatform;
            }

            @Override
            public boolean isReached() {
                return false;
            }

            @Override
            public Vector2 getPosition() {
                return pos;
            }

            @Override
            public void setGoalPosition(Vector2 pos) {

            }
        };
    }

    static final float BLOB_RADIUS = 7;

    Item createItemBlob(Vector2 pos_goal) {
        int blobSize = getBlobSize();
        Vector2 d = new Vector2(BLOB_RADIUS, 0);
        Vector2 pos_nextItem = new Vector2();
        d.rotateRad((float) (Math.random() * Math.PI));

        Item item1 = null;
        Item item2;
        Item item3;

        switch (blobSize) {
            case 1:
                item1 = new Item(pos_goal.x, pos_goal.y, world);

                items.add(item1);
                break;
            case 2:
                pos_nextItem.set(pos_goal.x + d.x, pos_goal.y + d.y);
                item1 = new Item(pos_nextItem.x, pos_nextItem.y, world);

                d.rotateRad((float) (Math.PI));
                pos_nextItem.set(pos_goal.x + d.x, pos_goal.y + d.y);
                item2 = new Item(pos_nextItem.x, pos_nextItem.y, world);

                items.add(item1);
                items.add(item2);
                break;
            case 3:
                pos_nextItem.set(pos_goal.x + d.x, pos_goal.y + d.y);
                item1 = new Item(pos_nextItem.x, pos_nextItem.y, world);

                d.rotateRad((float) (2 / 3f * Math.PI));
                pos_nextItem.set(pos_goal.x + d.x, pos_goal.y + d.y);
                item2 = new Item(pos_nextItem.x, pos_nextItem.y, world);

                d.rotateRad((float) (2 / 3f * Math.PI));
                pos_nextItem.set(pos_goal.x + d.x, pos_goal.y + d.y);
                item3 = new Item(pos_nextItem.x, pos_nextItem.y, world);

                items.add(item1);
                items.add(item2);
                items.add(item3);
                break;
        }
        return item1;
    }

    int getBlobSize() {
        int itemsAmount = playerDrone.battery.level;
        if (Math.random() < .25)
            itemsAmount++;
        return MathUtils.clamp(itemsAmount, 1, 3);
    }

    @Override
    public void show() {
        setPhase(Phase.START);
    }

    @Override
    public void hide() {
        playerDrone.dispose(world);
    }

    public void start() {
        setPhase(Phase.PLAY);
    }

    public void reset() {
        // Reset cam
        pos_cam_dst.set(0, 0);

        // Dispose and clear objects

        goals.clear();

        platformsCreated = 0;

        for (Drone drone : environmentDrones)
            drone.dispose(world);
        environmentDrones.clear();

        for (Item item : items)
            item.dispose();
        items.clear();

        for (BatteryUpgrade batteryUpgrade : batteryUpgrades)
            batteryUpgrade.dispose();
        batteryUpgrades.clear();

        if (playerDrone != null)
            playerDrone.dispose(world);

        // Reset angle

        if (Math.random() < .5)
            angle_newGoal = (float) (Math.random() * Math.PI * 1 / 4f);
        else
            angle_newGoal = (float) (Math.random() * Math.PI * 1 / 4f + Math.PI * 3 / 4f);

        collectedItems.clear();
    }

    void gameOver(int delay) {
        if (phase != Phase.GAME_OVER)
            setPhase(Phase.GAME_OVER);
    }

//    When game over: cam stops lerp, gameOverUI is updated and rendered

    @Override
    public void touchDown(int button) {

    }

    @Override
    public void touchUp() {
        for (Button button : buttons)
            button.touchUp();
        if (phase == Phase.GAME_OVER)
            gameOverUI.touchUp();
    }

    @Override
    public void onKeyDown(int key) {

        if (key == Input.Keys.P)
            playerDrone.addPiece(0);

        if (key == Input.Keys.O) {
            Res.pe_wind.setPosition(0, 0);

            Res.pe_wind.start();
        }
    }
}
