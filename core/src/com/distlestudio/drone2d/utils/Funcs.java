package com.distlestudio.drone2d.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.distlestudio.drone2d.Res;

import java.util.ArrayList;

public class Funcs {

    public static Class getClass(Object ud) {
        if (ud != null)
            return ud.getClass();
        else
            return null;
    }

    public static Class getSuperClass(Class c) {
        if (c != null)
            return c.getSuperclass();
        else
            return null;
    }

    public static float map(float minIn, float maxIn, float minOut, float maxOut, float value) {
        float f = MathUtils.clamp((value - minIn) / (maxIn - minIn), 0, 1);
        return minOut + f * (maxOut - minOut);
    }

    public static boolean pointInRectangle(float x, float y, float bx, float by, float bw, float bh) {
        return x > bx && x < bx + bw && y > by && y < by + bh;
    }

    public static Body destroyBody(World world, Body body) {
        if (body != null)
            world.destroyBody(body);
        return null;
    }

    public static void printStackTrace() {
        System.out.println("Printing stack trace:");
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < elements.length; i++) {
            StackTraceElement s = elements[i];
            System.out.println("\tat " + s.getClassName() + "." + s.getMethodName()
                    + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
        }
    }

    public static float[] multiplyVertices(float[] v, float factor) {
        float[] result = new float[v.length];
        for (int i = 0; i < v.length; i++)
            result[i] = v[i] * factor;
        return result;
    }

    public static void drawNumber(SpriteBatch batch, int number, Vector2 pos) {
        int digitAmount = 1;
        int power = 1;
        ArrayList<Integer> digits = new ArrayList<Integer>();
        while (number >= Math.pow(10, power)) {
            digitAmount++;
            power++;
        }
        if (number == 0) {
            digitAmount = 1;
        }
        int crunchNumber = number;
        for (int i = digitAmount - 1; i >= 0; i--) {
            digits.add((int) (crunchNumber / Math.pow(10, i)));
            crunchNumber %= Math.pow(10, i);
        }
        int width = 0;
        for (int i : digits) {
            width += Res.tex_numbers[i].getRegionWidth() + 1;
        }
        width--;
        int iWidth = 0;
        for (int i : digits) {
            batch.draw(Res.tex_numbers[i], pos.x - width / 2f + iWidth, pos.y);
            iWidth += Res.tex_numbers[i].getRegionWidth() + 1;
        }
    }

}
