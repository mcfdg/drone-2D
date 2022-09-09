package com.distlestudio.drone2d;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Res {
    public static TextureAtlas atlas;

    public static TextureRegion tex_rope;
    public static TextureRegion tex_dot;
    public static TextureRegion tex_battery;
    public static TextureRegion tex_batteryFill;
    public static TextureRegion tex_batteryCross;
    public static TextureRegion tex_screen;
    public static TextureRegion tex_propellerButton_propeller;
    public static TextureRegion tex_propellerButton;
    public static TextureRegion tex_propellerButton_cross;
    public static TextureRegion tex_propellerButton_pressed;
    public static TextureRegion tex_playerDrone;
    public static TextureRegion tex_normalDrone_background;
    public static TextureRegion tex_platformDrone;
    public static TextureRegion tex_platformDrone_fill;
    public static TextureRegion tex_infinitePlatformDrone;
    public static TextureRegion tex_buttonsBackground;
    public static TextureRegion tex_press_to_start;
    public static TextureRegion tex_outlineGameOverButtons;

    public static TextureRegion tex_leaderBoardButton;
    public static TextureRegion tex_leaderBoardButton_pressed;
    public static TextureRegion tex_restartButton;
    public static TextureRegion tex_restartButton_pressed;
    public static TextureRegion tex_scorePad;
    public static TextureRegion tex_coinBox;
    public static TextureRegion tex_batteryUpgrade;

    public static TextureRegion[] tex_platformDronePropeller;
    public static TextureRegion[] tex_infinitePlatformDronePropeller;
    public static TextureRegion[] tex_playerDronePropeller;
    public static TextureRegion[] tex_numbers;
    public static TextureRegion[] tex_buildings;
    public static TextureRegion[] tex_propellerAir;
    public static TextureRegion[] tex_propeller_background;
    public static TextureRegion[] tex_coin;
    public static TextureRegion[] tex_propellerObject;

    public static PolygonShape shape_ropePiece;
    public static PolygonShape shape_ropeDot;
    public static PolygonShape shape_dot;
    public static BodyDef bodyDef_dynamic;
    public static BodyDef bodyDef_static;
    public static BodyDef bodyDef_rope;
    public static BodyDef bodyDef_ropeDot;
    public static BodyDef bodyDef_dot;
    public static FixtureDef fixtureDef_ropeDot;
    public static FixtureDef fixtureDef_rope;
    public static FixtureDef fixtureDef_dot;
    public static FixtureDef fixtureDef_batteryUpgrade;
    public static FixtureDef fixtureDef_propeller;

    public static ItemDefinition[] itemDefinitions;

    public static DroneDefinition definition_playerDrone;
    public static DroneDefinition definition_platformDrone;

    public static Color[][] palette;
    public static float[][] paletteFlat;
    public static Color[] paletteTutorial;
    public static float[] paletteTutorialFlat;

    public static Sound sound_hurt;
    public static Sound sound_success;
    public static Sound sound_land;
    public static Sound sound_batteryEmpty;
    public static Sound sound_propeller;
    public static Sound sound_collect;
    public static Music music_propeller;

    public static ShaderProgram shader_palette;
    public static ShaderProgram shader_paletteTransition;

    public static ParticleEffect pe_spark;
    public static ParticleEffect pe_wind;

    public static final short MASK_ZERO = 1;
    public static final short MASK_DRONE = 2;
    public static final short MASK_ROPE = 4;
    public static final short MASK_ITEM = 8;

    public static void createAtlas() {
        atlas = new TextureAtlas(Gdx.files.internal("images.atlas"));
    }

    public static void load() {
        new World(Vector2.Zero, true); // Trick to load Box2D
        createAtlas();
        tex_playerDrone = atlas.findRegion("drone");
        tex_normalDrone_background = atlas.findRegion("drone_normal_background");
        tex_rope = atlas.findRegion("rope");
        tex_dot = atlas.findRegion("dot");
        tex_platformDrone_fill = atlas.findRegion("drone_platform_fill");
        tex_platformDrone = atlas.findRegion("drone_platform");
        tex_infinitePlatformDrone = atlas.findRegion("drone_infinite_platform");
        tex_battery = atlas.findRegion("battery");
        tex_batteryFill = atlas.findRegion("battery_fill");
        tex_batteryCross = atlas.findRegion("battery_cross");
        tex_propellerButton_cross = tex_batteryCross;
        tex_screen = atlas.findRegion("screen");
        tex_propellerButton = atlas.findRegion("button_propeller");
        tex_propellerButton_propeller = atlas.findRegion("button_propeller_propeller");
        tex_propellerButton_pressed = atlas.findRegion("button_propeller_pressed");
        tex_buttonsBackground = atlas.findRegion("buttons_background");
        tex_press_to_start = atlas.findRegion("press_to_start");
        tex_batteryUpgrade = atlas.findRegion("battery_upgrade");

        tex_leaderBoardButton = atlas.findRegion("button_leader_board");
        tex_leaderBoardButton_pressed = atlas.findRegion("button_leader_board_pressed");
        tex_restartButton = atlas.findRegion("button_restart");
        tex_restartButton_pressed = atlas.findRegion("button_restart_pressed");
        tex_scorePad = atlas.findRegion("score_pad");
        tex_coinBox = atlas.findRegion("coin_box");
        tex_outlineGameOverButtons = atlas.findRegion("outline_game_over_buttons");

        tex_buildings = new TextureRegion[]{
                atlas.findRegion("buildings", 0),
                atlas.findRegion("buildings", 1),
                atlas.findRegion("buildings", 2),
        };

        tex_coin = new TextureRegion[]{
                atlas.findRegion("coin", 0),
                atlas.findRegion("coin", 1),
                atlas.findRegion("coin", 2),
                atlas.findRegion("coin", 3),
                atlas.findRegion("coin", 4),
                atlas.findRegion("coin", 5),
        };

        pe_spark = new ParticleEffect();
        pe_spark.load(Gdx.files.internal("particle_spark.p"), Res.atlas);

        pe_wind = new ParticleEffect();
        pe_wind.load(Gdx.files.internal("particle_fly.p"), Res.atlas);

        tex_numbers = new TextureRegion[10];
        for (int i = 0; i < tex_numbers.length; i++)
            tex_numbers[i] = atlas.findRegion("numbers/" + i);

        tex_playerDronePropeller = new TextureRegion[]{
                atlas.findRegion("propeller", 0),
                atlas.findRegion("propeller", 1),
                atlas.findRegion("propeller", 2),
                atlas.findRegion("propeller", 3),
                atlas.findRegion("propeller", 2),
                atlas.findRegion("propeller", 1),
        };

        tex_propeller_background = new TextureRegion[]{
                atlas.findRegion("propeller_background", 0),
                atlas.findRegion("propeller_background", 1),
                atlas.findRegion("propeller_background", 2),
                atlas.findRegion("propeller_background", 3),
                atlas.findRegion("propeller_background", 2),
                atlas.findRegion("propeller_background", 1),
        };

        tex_propellerAir = new TextureRegion[]{
                atlas.findRegion("propeller_air", 0),
                atlas.findRegion("propeller_air", 1),
                atlas.findRegion("propeller_air", 2),
                atlas.findRegion("propeller_air", 3),
                atlas.findRegion("propeller_air", 4),
                atlas.findRegion("propeller_air", 5),
        };

        tex_platformDronePropeller = new TextureRegion[]{
                atlas.findRegion("platformPropeller", 0),
                atlas.findRegion("platformPropeller", 1),
                atlas.findRegion("platformPropeller", 2),
                atlas.findRegion("platformPropeller", 3),
                atlas.findRegion("platformPropeller", 2),
                atlas.findRegion("platformPropeller", 1),
        };

        tex_infinitePlatformDronePropeller = new TextureRegion[]{
                atlas.findRegion("propeller_infinite_platform", 0),
                atlas.findRegion("propeller_infinite_platform", 1),
                atlas.findRegion("propeller_infinite_platform", 2),
                atlas.findRegion("propeller_infinite_platform", 3),
                atlas.findRegion("propeller_infinite_platform", 2),
                atlas.findRegion("propeller_infinite_platform", 1),
        };

        tex_propellerObject = new TextureRegion[]{
                atlas.findRegion("propeller_object", 0),
                atlas.findRegion("propeller_object", 1),
                atlas.findRegion("propeller_object", 2),
                atlas.findRegion("propeller_object", 3),
                atlas.findRegion("propeller_object", 2),
                atlas.findRegion("propeller_object", 1),
        };

        itemDefinitions = new ItemDefinition[]{
                new ItemDefinition(atlas.findRegion("item_syringe"), 1),
                new ItemDefinition(atlas.findRegion("item_heart"), 1),
                new ItemDefinition(atlas.findRegion("item_apple"), 1),
                new ItemDefinition(atlas.findRegion("item_bottle"), 1),
                new ItemDefinition(atlas.findRegion("item_dumbell"), 100),
        };

        bodyDef_dynamic = new BodyDef();
        bodyDef_dynamic.type = BodyDef.BodyType.DynamicBody;

        bodyDef_static = new BodyDef();
        bodyDef_static.type = BodyDef.BodyType.StaticBody;

        // Drones
        definition_playerDrone = new DroneDefinition(
//                new float[]{7.5f, 2f, 4.5f, -4f, -4.5f, -4f, -7.5f, 2f},
                new float[]{7.5f, 2f, 5.5f, -4f, -5.5f, -4f, -7.5f, 2f},
                new float[]{-10.5f, 4, -3.5f, 4, -3.5f, 3, -10.5f, 3},
                new float[]{10.5f, 4, 3.5f, 4, 3.5f, 3, 10.5f, 3}
        );
        definition_platformDrone = new DroneDefinition(
                new float[]{-15.5f, 3, -15.5f, -3, 15.5f, -3, 15.5f, 3},
                new float[]{-24.5f, 3, -17.5f, 3, -17.5f, 2, -24.5f, 2},
                new float[]{24.5f, 3, 17.5f, 3, 17.5f, 2, 24.5f, 2},
                MASK_ZERO, MASK_ROPE
        );


        // bg dark
        // bg light
        // fg dark
        // fg light

        palette = new Color[][]{
                new Color[]{ // lvl 0
                        Color.valueOf("404040"),
                        Color.valueOf("696969"),
                        Color.valueOf("c3c3c3"),
                        Color.valueOf("ffffff"),
                },
                new Color[]{ // lvl 1
                        Color.valueOf("1e65e3"),
                        Color.valueOf("3586ff"),
                        Color.valueOf("20d570"),
                        Color.valueOf("57ff3c"),
                },
                new Color[]{ // lvl 2
                        Color.valueOf("5c17c3"),
                        Color.valueOf("8529d6"),
                        Color.valueOf("ff7011"),
                        Color.valueOf("ffaa00"),
                },
        };

        paletteFlat = new float[palette.length][12];
        for (int i = 0; i < paletteFlat.length; i++) {
            for (int j = 0; j < 4; j++) {
                paletteFlat[i][j * 3] = palette[i][j].r;
                paletteFlat[i][j * 3 + 1] = palette[i][j].g;
                paletteFlat[i][j * 3 + 2] = palette[i][j].b;
            }
        }

        paletteTutorial = new Color[]{
                Color.valueOf("5e6dac"),
                Color.valueOf("5e6dac"),
                Color.valueOf("c4cae4"),
                Color.valueOf("ffffff"),
        };

        paletteTutorialFlat = new float[12];
        for (int j = 0; j < 4; j++) {
            paletteTutorialFlat[j * 3] = paletteTutorial[j].r;
            paletteTutorialFlat[j * 3 + 1] = paletteTutorial[j].g;
            paletteTutorialFlat[j * 3 + 2] = paletteTutorial[j].b;
        }

        sound_hurt = Gdx.audio.newSound(Gdx.files.internal("sounds/hurt.mp3"));
        sound_success = Gdx.audio.newSound(Gdx.files.internal("sounds/success.mp3"));
        sound_land = Gdx.audio.newSound(Gdx.files.internal("sounds/land.mp3"));
        sound_batteryEmpty = Gdx.audio.newSound(Gdx.files.internal("sounds/battery_empty.mp3"));
        sound_collect = Gdx.audio.newSound(Gdx.files.internal("sounds/collect.mp3"));
        sound_propeller = Gdx.audio.newSound(Gdx.files.internal("sounds/propeller_extended.mp3"));

        music_propeller = Gdx.audio.newMusic(Gdx.files.internal("sounds/propeller_extended.mp3"));

        shader_palette = new ShaderProgram(Gdx.files.internal("shaders/palette.vert"),
                Gdx.files.internal("shaders/palette.frag"));
        shader_paletteTransition = new ShaderProgram(Gdx.files.internal("shaders/palette_transition.vert"),
                Gdx.files.internal("shaders/palette_transition.frag"));

        // Rope piece
        bodyDef_rope = new BodyDef();
        bodyDef_rope.type = BodyDef.BodyType.DynamicBody;
        shape_ropePiece = new PolygonShape();
        shape_ropePiece.setAsBox(4 * Drone2D.MPP / 2, 1 * Drone2D.MPP / 2);
        fixtureDef_rope = new FixtureDef();
        fixtureDef_rope.density = 1;
        fixtureDef_rope.shape = shape_ropePiece;
        fixtureDef_rope.filter.categoryBits = MASK_ROPE;
        fixtureDef_rope.filter.maskBits = MASK_ZERO;

        // Dot on rope
        bodyDef_ropeDot = new BodyDef();
        bodyDef_ropeDot.type = BodyDef.BodyType.DynamicBody;
        shape_ropeDot = new PolygonShape();
        shape_ropeDot.setAsBox(3 * Drone2D.MPP / 2, 3 * Drone2D.MPP / 2);
        fixtureDef_ropeDot = new FixtureDef();
        fixtureDef_ropeDot.density = 1;
        fixtureDef_ropeDot.shape = shape_ropeDot;
        fixtureDef_ropeDot.filter.categoryBits = MASK_ROPE;
        fixtureDef_ropeDot.filter.maskBits = (short) (MASK_ZERO);

        // Dot
        bodyDef_dot = new BodyDef();
        bodyDef_dot.type = BodyDef.BodyType.StaticBody;
        shape_dot = new PolygonShape();
        shape_dot.setAsBox(3 * Drone2D.MPP / 2, 3 * Drone2D.MPP / 2);
        fixtureDef_dot = new FixtureDef();
        fixtureDef_dot.density = 1;
        fixtureDef_dot.shape = shape_dot;
        fixtureDef_dot.filter.categoryBits = MASK_ITEM;
        fixtureDef_dot.filter.maskBits = MASK_DRONE;
        fixtureDef_dot.isSensor = true;

        // Battery Upgrade
        PolygonShape shape_batteryUpgrade = new PolygonShape();
        shape_batteryUpgrade.setAsBox(13 * Drone2D.MPP / 2, 6 * Drone2D.MPP / 2);
        fixtureDef_batteryUpgrade = new FixtureDef();
        fixtureDef_batteryUpgrade.density = 1;
        fixtureDef_batteryUpgrade.shape = shape_batteryUpgrade;
        fixtureDef_batteryUpgrade.filter.categoryBits = MASK_ITEM;
        fixtureDef_batteryUpgrade.filter.maskBits = MASK_DRONE;
        fixtureDef_batteryUpgrade.isSensor = false;

        PolygonShape shape_propeller = new PolygonShape();
        shape_propeller.setAsBox(7 * Drone2D.MPP / 2, 1 * Drone2D.MPP / 2);
        fixtureDef_propeller = new FixtureDef();
        fixtureDef_propeller.density = 1;
        fixtureDef_propeller.shape = shape_propeller;
        fixtureDef_propeller.filter.categoryBits = MASK_ITEM;
        fixtureDef_propeller.filter.maskBits = MASK_DRONE;
        fixtureDef_propeller.isSensor = false;

    }

    public static final class Color_ {
        public static final Color FOREGROUND = Color.valueOf("57ff3c");
        public static final Color BACKGROUND = Color.valueOf("1e65e3");
    }
}
