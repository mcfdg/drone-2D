package com.distlestudio.drone2d.button;

import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.scenes.Game;

public class RestartButton extends Button {
    public RestartButton(float x, float y) {
        super(Res.tex_restartButton, Res.tex_restartButton_pressed, x, y);
    }

    @Override
    public void action() {
        Drone2D.game.setPhase(Game.Phase.START);
    }
}
