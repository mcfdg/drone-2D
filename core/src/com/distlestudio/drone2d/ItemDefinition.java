package com.distlestudio.drone2d;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemDefinition {
    private TextureRegion tex;
    private float density;
    public ItemDefinition(TextureRegion tex, float density){
        this.tex = tex;
        this.density = density;
    }

    public TextureRegion getRegion(){
        return tex;
    }

    public float getDensity(){
        return density;
    }
}
