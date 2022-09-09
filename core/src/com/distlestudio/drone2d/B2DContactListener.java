package com.distlestudio.drone2d;

import com.badlogic.gdx.physics.box2d.*;
import com.distlestudio.drone2d.drone.*;
import com.distlestudio.drone2d.scenes.Game;

public class B2DContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Object udA = contact.getFixtureA().getBody().getUserData();
        Object udB = contact.getFixtureB().getBody().getUserData();

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Class classA = getClass(udA);
        Class classB = getClass(udB);

        for (int i = 0; i < 2; i++) {

            if (classA == PlayerDrone.class && classB == Item.class) {
                PlayerDrone playerDrone = (PlayerDrone) udA;
                Item item = (Item) udB;
                if(!item.isCollected) {
                    playerDrone.addPiece_delayed(item.type);
                    item.onCollect();
                }
            }

            if(Drone2D.game.phase == Game.Phase.PLAY) {
                if (classA == PlayerDrone.class && classB == BatteryUpgrade.class) {
                    PlayerDrone playerDrone = (PlayerDrone) udA;
                    BatteryUpgrade batteryUpgrade = (BatteryUpgrade) udB;
                    if (!batteryUpgrade.isCollected) {
                        batteryUpgrade.onCollect();
                        playerDrone.battery.upgrade();
                    }
                }
            }

            if (classA == Piece.class && (classB == PlatformDrone.class || classB == InfinitePlatformDrone.class)) {
                Piece piece = (Piece) udA;
                piece.onTouchGround();
            }

            if (classA == PlayerDrone.class && classB == PlatformDrone.class) {
                PlayerDrone playerDrone = (PlayerDrone) udA;
                PlatformDrone platformDrone = (PlatformDrone) udB;

                if (!fixA.isSensor()){
                    playerDrone.battery.setCharge(platformDrone.battery);
                    platformDrone.battery.setDrain(true);
                    platformDrone.onSuccessfulPlayerCollision();
                    playerDrone.onLand();
                }
            }

            if (classA == PlayerDrone.class && classB == InfinitePlatformDrone.class) {
                PlayerDrone playerDrone = (PlayerDrone) udA;
                InfinitePlatformDrone infinitePlatformDrone = (InfinitePlatformDrone) udB;

                if (!fixA.isSensor()){
                    infinitePlatformDrone.onSuccessfulPlayerCollision();
                    playerDrone.onLand();
                }
            }

            if (udA instanceof Drone && udB instanceof Drone) {
                Drone drone = (Drone) udA;
                if (fixA.isSensor())
                    drone.propellerCollision(fixA, contact.getWorldManifold().getPoints()[0]);
            }

            udA = contact.getFixtureB().getBody().getUserData();
            udB = contact.getFixtureA().getBody().getUserData();
            classA = getClass(udA);
            classB = getClass(udB);
            fixA = contact.getFixtureB();
            fixB = contact.getFixtureA();
        }
    }

    @Override
    public void endContact(Contact contact) {
        Object udA = contact.getFixtureA().getBody().getUserData();
        Object udB = contact.getFixtureB().getBody().getUserData();

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Class classA = getClass(udA);
        Class classB = getClass(udB);

        for (int i = 0; i < 2; i++) {

            if (classA == PlayerDrone.class && classB == PlatformDrone.class) {
                PlayerDrone playerDrone = (PlayerDrone) udA;
                PlatformDrone platformDrone = (PlatformDrone) udB;

                if (!fixA.isSensor()) {
                    platformDrone.battery.setDrain(false);
                    playerDrone.battery.stopCharge();
                }
            }

            udA = contact.getFixtureB().getBody().getUserData();
            udB = contact.getFixtureA().getBody().getUserData();
            classA = getClass(udA);
            classB = getClass(udB);
            fixA = contact.getFixtureB();
            fixB = contact.getFixtureA();

        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Object udA = contact.getFixtureA().getBody().getUserData();
        Object udB = contact.getFixtureB().getBody().getUserData();

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Class classA = getClass(udA);
        Class classB = getClass(udB);

        for (int i = 0; i < 2; i++) {
            udA = contact.getFixtureB().getBody().getUserData();
            udB = contact.getFixtureA().getBody().getUserData();
            classA = getClass(udA);
            classB = getClass(udB);
            fixA = contact.getFixtureB();
            fixB = contact.getFixtureA();
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    Class getClass(Object ud) {
        if (ud != null)
            return ud.getClass();
        else
            return null;
    }

    Class getSuperClass(Class c) {
        if (c != null)
            return c.getSuperclass();
        else
            return null;
    }
}
