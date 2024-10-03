package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe ColorRamp du sous-paquetage gui, publique, finale et immuable, représente un dégradé de couleurs.
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public final class ColorRamp{
    public static final int LIMIT_COLOR = 31;
    public static final int FULL_PERCENTAGE = 100;
    private final List<Color> colors;
    public static final ColorRamp PLASMA = new ColorRamp(
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
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));


    /**
     * Le constructeur de ColorRamp en argument une séquence de couleurs JavaFX de type Color ,
     * et lève une exception si elle ne contient pas au moins deux couleurs.
     * @param colors une séquence de couleurs JavaFx.
     */
    public ColorRamp(Color... colors) {
        Preconditions.checkArgument(!(colors.length < 2));
        this.colors = List.of(colors);
    }



    /**
     * méthode publique at, prend un argument de type double et retourne la couleur correspondante.
     * @param c un double.
     * @return la couleur correspondante à ce double.
     */
    public Color at(double c) {
        List<Double> grades = new ArrayList<>(colors.size());
        for (int i = 0; i < colors.size(); i++) {
            grades.add(i / (double) (colors.size() - 1));}
        int index = (int) Math.floor(c / (1d / (colors.size() - 1)));
        double percentage = (grades.get(Math.min(LIMIT_COLOR, index + 1)) - c) * FULL_PERCENTAGE;
        Color color = colors.get(Math.min(LIMIT_COLOR, index + 1));
        return colors.get(index).interpolate(color, percentage);
    }
}