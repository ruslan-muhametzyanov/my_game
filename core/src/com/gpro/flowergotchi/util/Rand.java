package com.gpro.flowergotchi.util;

import java.util.Random;

public class Rand {
    static final Random rand = new Random();

    public static int randInt(int min, int max) {

        return rand.nextInt((max - min) + 1) + min;
    }

    public static float randFloat(float min, float max) {

        return (float) (Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
    }
}
