package com.distlestudio.drone2d.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.button.Button;
import com.distlestudio.drone2d.button.LeaderBoardButton;
import com.distlestudio.drone2d.button.RestartButton;
import com.distlestudio.drone2d.drone.Drone;
import com.distlestudio.drone2d.utils.Animation;
import com.distlestudio.drone2d.utils.EventListener;
import com.distlestudio.drone2d.utils.EventManager;
import com.distlestudio.drone2d.utils.Funcs;

import java.util.ArrayList;

public class GameOverUI extends UI {
    Button leaderBoardButton;
    Button restartButton;

    GlyphLayout glyphLayout_highScore;
    GlyphLayout glyphLayout_score;
    GlyphLayout glyphLayout_reward;
    GlyphLayout glyphLayout_plusReward;
    GlyphLayout glyphLayout_msg;

    float yLeaderBoardButton = 82 - 125;
    float yRestartButton = 41 - 125;
    float yButtonsOutline = 39 - 125;

    float yScorePad = 113 - 125;
    float yHighScore = 170 - 125 + 7;

    float xScore = -25;
    float yScore = 124 - 125 + 7;

    float xReward = 25;
    float yReward = 125 - 125 + 7;
    float width_reward;

    int yPlusReward = 156 - 125;
    float width_plusReward;

    float xButtons = 0;
    float xScorePad = 0;

    public static final int[] REWARD_PACKAGE_SIZE = new int[]{3, 5, 7, 9, 12};
    public static final int[] REWARD_SIZE = new int[]{1, 2, 3, 4, 5};
    int currentPackage;

    static final int MAX_DST_BETWEEN_ITEMS = 10;
    int initialDstBetweenItems;
    float dstBetweenItems;
    float xItemRowBegin;
    int yItems = 153 - 125;

    int itemsToShow_previous;
    int itemsToShow;

    int reward;
    int localScore;
    float itemWaveMultiplier;


    Animation animation_coin_reward;
    Animation animation_coin_plusReward;


    ArrayList<Integer> currentItemPackage = new ArrayList<>();

    EventManager eventManager_rewards;

    float count_itemWave;

    boolean packageFull;

    static final int SPACING_COIN_AND_NUMBER = 2;

    public GameOverUI() {
        leaderBoardButton = new LeaderBoardButton(-Res.tex_leaderBoardButton.getRegionWidth() / 2f, yLeaderBoardButton);
        restartButton = new RestartButton(-Res.tex_restartButton.getRegionWidth() / 2f, yRestartButton);

        glyphLayout_highScore = new GlyphLayout();
        glyphLayout_score = new GlyphLayout();
        glyphLayout_reward = new GlyphLayout();
        glyphLayout_plusReward = new GlyphLayout();
        glyphLayout_msg = new GlyphLayout();

        glyphLayout_msg.setText(Drone2D.font, ":)");

        animation_coin_reward = new Animation(.55f, Res.tex_coin, true);
        animation_coin_plusReward = new Animation(.55f, Res.tex_coin, true);

        progressSpeed = 3;

        eventManager_rewards = new EventManager(new EventListener() {
            @Override
            public float onEvent(int event) {
                switch (event) {
                    case 0: // start displaying items
                        currentItemPackage.clear();
                        itemsToShow_previous = 0;
                        itemsToShow = 0;

                        packageFull = REWARD_PACKAGE_SIZE[currentPackage] <= Drone2D.game.collectedItems.size();

                        int max = Math.min(REWARD_PACKAGE_SIZE[currentPackage], Drone2D.game.collectedItems.size());
                        for (int i = 0; i < max; i++) {
                            currentItemPackage.add(Drone2D.game.collectedItems.get(0));
                            Drone2D.game.collectedItems.remove(0);
                        }

                        initialDstBetweenItems = MAX_DST_BETWEEN_ITEMS;
                        dstBetweenItems = initialDstBetweenItems;
                        xItemRowBegin = -dstBetweenItems * (currentItemPackage.size() - 1) / 2;

                        return currentItemPackage.size() * .4f;
                    case 1: // start merging items
                        update_itemsToShow(1);
                        if (!packageFull)
                            return -1;
                        return .5f;
                    case 2: // show reward
                        currentItemPackage.clear();
                        itemsToShow = 0;

                        Res.sound_success.play();

                        reward += REWARD_SIZE[currentPackage];
                        Drone2D.gameData.coins += reward;
                        return 1;
                    case 3: // on end
                        currentPackage++;
                        return -2;
                }
                return 0;
            }
        });
        eventManager_rewards.start();
    }

    @Override
    void update_visible() {
        eventManager_rewards.update();

        animation_coin_reward.update();
        animation_coin_plusReward.update();

        xButtons = Funcs.map(0, 1, -Drone2D.WIDTH, 0, progress);
        xScorePad = Funcs.map(0, 1, Drone2D.WIDTH, 0, progress);

        leaderBoardButton.pos.x = xButtons - Res.tex_leaderBoardButton.getRegionWidth() / 2f;
        restartButton.pos.x = xButtons - Res.tex_restartButton.getRegionWidth() / 2f;
        leaderBoardButton.update();
        restartButton.update();

        glyphLayout_highScore.setText(Drone2D.font, Integer.toString(Drone2D.gameData.highScore));
        glyphLayout_score.setText(Drone2D.font, Integer.toString(localScore));
        glyphLayout_reward.setText(Drone2D.font, Integer.toString(reward));
        glyphLayout_plusReward.setText(Drone2D.font, "+" + REWARD_SIZE[currentPackage]);

        width_reward = glyphLayout_reward.width + SPACING_COIN_AND_NUMBER + Res.tex_coin[0].getRegionWidth();
        width_plusReward = glyphLayout_plusReward.width + SPACING_COIN_AND_NUMBER + Res.tex_coin[0].getRegionWidth();

        switch (eventManager_rewards.getEvent()) {
            case 0:
                update_itemsToShow(eventManager_rewards.getProgress());
                itemWaveMultiplier = 1;
                break;
            case 1:
                if (packageFull) {
                    dstBetweenItems = initialDstBetweenItems * (1 - eventManager_rewards.getProgress());
                    xItemRowBegin = -dstBetweenItems * (currentItemPackage.size() - 1) / 2;
                    itemWaveMultiplier = (1 - eventManager_rewards.getProgress());
                }
                break;
            case 2:
                break;
        }

        count_itemWave = (float) ((count_itemWave + 5 * Gdx.graphics.getDeltaTime()) % (2 * Math.PI));
    }

    void update_itemsToShow(float progress) {
        itemsToShow = (int) (currentItemPackage.size() * progress);
        if (itemsToShow != itemsToShow_previous) {
            localScore++;
            Res.sound_collect.play();
        }
        itemsToShow_previous = itemsToShow;
    }

    @Override
    void render_visible(SpriteBatch batch, OrthographicCamera cam) {
        batch.draw(Res.tex_scorePad,
                -Res.tex_scorePad.getRegionWidth() / 2f + cam.position.x + xScorePad, yScorePad + cam.position.y);

        batch.draw(Res.tex_outlineGameOverButtons,
                (int) (cam.position.x + xButtons - Res.tex_outlineGameOverButtons.getRegionWidth() / 2) - .5f,
                cam.position.y + yButtonsOutline);
        restartButton.render(batch, cam);
        leaderBoardButton.render(batch, cam);

        Drone2D.font.draw(batch, glyphLayout_score,
                xScorePad + cam.position.x - glyphLayout_score.width / 2 + xScore, cam.position.y + yScore);

        if(reward > 0) {
            batch.draw(animation_coin_reward.getTexture(), -.5f + xScorePad + cam.position.x - width_reward / 2 + xReward,
                    cam.position.y + yReward - Drone2D.FONT_HEIGHT - 1);
            Drone2D.font.draw(batch, glyphLayout_reward, xScorePad + cam.position.x - width_reward / 2 +
                    SPACING_COIN_AND_NUMBER + Res.tex_coin[0].getRegionWidth() + xReward, cam.position.y + yReward);
        }

        Drone2D.font.draw(batch, glyphLayout_highScore, xScorePad + cam.position.x - glyphLayout_highScore.width / 2,
                cam.position.y + yHighScore);

        if (eventManager_rewards.getEvent() == 2) {
            Drone2D.font.draw(batch, glyphLayout_plusReward,
                    xScorePad + cam.position.x - glyphLayout_plusReward.width / 2, cam.position.y + yPlusReward);
        } else {

            if (currentItemPackage.size() == 0 && reward > 0) {
                Drone2D.font.draw(batch, glyphLayout_msg,
                        xScorePad + cam.position.x - glyphLayout_msg.width / 2, cam.position.y + yPlusReward);
            } else {
                TextureRegion itemTex;
                for (int i = 0; i < itemsToShow; i++) {
                    itemTex = Res.itemDefinitions[currentItemPackage.get(i)].getRegion();
                    batch.draw(itemTex, cam.position.x + xItemRowBegin + i * dstBetweenItems - itemTex.getRegionWidth() / 2f,
                            (int) (cam.position.y + yItems - itemTex.getRegionHeight() / 2f + itemWaveMultiplier * 1.2f * (float) Math.sin(count_itemWave + i)));
                }
            }
        }
    }

    public void touchUp() {
        leaderBoardButton.touchUp();
        restartButton.touchUp();
    }

    @Override
    public void show() {
        super.show();

        // reset variables;
        currentPackage = 0;
        reward = 0;
        localScore = 0;

        eventManager_rewards.start();
        if (Drone2D.game.playerDrone.score > Drone2D.gameData.highScore)
            Drone2D.gameData.highScore = Drone2D.game.playerDrone.score;
    }
}
