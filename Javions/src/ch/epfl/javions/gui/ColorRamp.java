package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;

import javafx.scene.paint.Color;


import java.util.List;


public final class ColorRamp {

    public static final ColorRamp PLASMA = new ColorRamp(List.of(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff")));

    public static Color colorFromPlasma(double altitude){
        return PLASMA.at(Math.pow(altitude/12000, 1d/3d));
    }
    private final List<Color> colors;
    private static final int MIN_COLORS_NB = 2;

    public ColorRamp(List<Color> colorList) {
        Preconditions.checkArgument(colorList.size() >= MIN_COLORS_NB);
        colors = List.copyOf(colorList);
    }

    public Color at(double index) {
        if (index < 0)
            return colors.get(0);
        if (index > 1)
            return colors.get(colors.size() - 1);
        index *= (colors.size() - 1);
        double remainder = index % 1;
        index -= remainder;
        return (remainder == 0) ? colors.get((int) index) :
                colors.get((int) index).interpolate(colors.get((int)++index),remainder);
    }
}
