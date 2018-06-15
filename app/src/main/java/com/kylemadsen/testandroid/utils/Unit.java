package com.kylemadsen.testandroid.utils;

public class Unit {

    private static Unit unit = new Unit();

    public static Unit create() {
        return unit;
    }

}
