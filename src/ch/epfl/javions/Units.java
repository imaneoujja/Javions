package ch.epfl.javions;

/**
 * Le type Units contient la définition des préfixes SI utiles au projet et des methodes de conversion
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class Units {

    public static final double CENTI = 1e-2;
    public static final double KILO = 1e+3;
    public static final int SIXTY = 60;
    public static final int TURN_DEGREE = 360;


    /**
     * La classe Angle est une classe imbriquée non instanciable contenant la définition des unités d'angle
     */
    public static class Angle {
        private Angle() {
        }


        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN / TURN_DEGREE;
        public static final double T32 = Math.scalb(TURN, -32);

    }


    /**
     * La classe Length est une classe imbriquée non instanciable contenant la définition des unités de longueur
     */
    public static class Length {
        private Length() {
        }

        ;
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTI * METER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852 * METER;


    }

    /**
     * La classe Time est une classe imbriquée non instanciable contenant la définition des unités temporelles
     */

    public static class Time {
        private Time() {
        }

        public static final double SECOND = 1;
        public static final double MINUTE = SIXTY * SECOND;
        public static final double HOUR = SIXTY * MINUTE;

    }


    /**
     * La classe Speed est une classe imbriquée non instanciable contenant la définition des unités de vitesse
     */
    public static class Speed {
        private Speed() {
        }

        public static final double METRE_PER_SECOND = 1;
        public static final double KNOT = Units.Length.NAUTICAL_MILE / Units.Time.HOUR;
        public static final double KILOMETER_PER_HOUR = KILO * Units.Length.METER / Units.Time.HOUR;

    }


    /**
     * Cette methode convertit la valeur donnée, exprimée dans l'unité fromUnit, en l'unité toUnit
     *
     * @param value    la valeur à convertir
     * @param fromUnit l'unité de départ
     * @param toUnit   l'unité d'arrivée
     * @return value convertie en toUnit
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);

    }


    /**
     * Cette methode est équivalente à convert lorsque l'unité d'arrivée (toUnit) est l'unité de base et vaut donc 1
     *
     * @param value    la valeur à convertir
     * @param fromUnit l'unité de départ
     * @return value convertie en toUnit (unité de base)
     */
    public static double convertFrom(double value, double fromUnit) {
        return convert(value, fromUnit, 1);
    }


    /**
     * Cette methode est équivalente à convert lorsque l'unité de départ (fromUnit) est l'unité de base et vaut donc 1.
     *
     * @param value  la valeur à convertir
     * @param toUnit l'unité d'arrivée
     * @return value convertie de l'unite de base en toUnit (unité de base)
     */

    public static double convertTo(double value, double toUnit) {
        return convert(value, 1, toUnit);
    }


    /**
     * Le constructeur par defaut prive de Units
     */
    private Units() {
    }

}