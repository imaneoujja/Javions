package ch.epfl.javions.adsb;
import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

import static ch.epfl.javions.Units.Angle.*;
import static ch.epfl.javions.Units.convert;

/**
 * La classe représentant le Décodeur Cpr
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public  class CprDecoder{

    private static final double ZPHI0 = 60d;
    private static final double ZPHI1 = 59d;
    private static final double BOUND= 0.5d;

    /**
     * Le constructeur par défaut de la classe CprDecoder
     */
    public CprDecoder(){}




    /**
     * Cette méthode public représente un décodeur de position CPR
     * @param x0 longitude locale d'un message pair
     * @param y0 latitude locale d'un message pair
     * @param x1 longitude locale d'un message impair
     * @param y1 latitude locale d'un message impair
     * @param mostRecent index (0 ou 1)
     * @throws IllegalArgumentException si mostRecent diffère de 0 ou de 1
     * @return la position géographique correspondant aux positions locales normalisées données ou null
     * si la latitude de la position décodée n'est pas valide
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {

        Preconditions.checkArgument((mostRecent ==0 || mostRecent == 1));

        double zphi = Math.rint(y0 * ZPHI1 - y1 * ZPHI0);
        double zphi0;
        double zphi1;
        double lambda0= x0;
        double lambda1= x1;
        double phi0=y0;
        double phi1=y1;
        double zl;
        double zl0;
        double zl1;


        if (zphi < 0) {
            zphi0 = zphi + ZPHI0;
            zphi1 = zphi + ZPHI1;
        } else {
            zphi0 = zphi;
            zphi1 = zphi;
        }
        double phi0Turn =(zphi0 + phi0) / ZPHI0;
        double phi1Turn =(zphi1 + phi1) / ZPHI1;
        double phi0Radian = convert((zphi0 + phi0)* 1 / ZPHI0 , TURN, RADIAN);
        double phi1Radian = convert((zphi1 + phi1)*1 / ZPHI1,TURN, RADIAN);

        phi1 = convert((zphi1 + phi1)*1 / ZPHI1 , TURN, RADIAN);
        double c0= (1-Math.cos(2d * Math.PI / ZPHI0)) / Math.pow(((Math.cos(phi0Radian))), 2);
        double c1= (1-Math.cos(2d * Math.PI / ZPHI0)) / Math.pow(((Math.cos(phi1))), 2);
        double A0 = Math.acos(1 - c0);
        double A1 = Math.acos(1 - c1);
        double Zl0 = Math.floor(2d * Math.PI / A0);
        double Zl00 = Math.floor(2d * Math.PI / A1);
        double Zl1 = Zl0 - 1;

        if(Double.isNaN(A0)){
            zl=1d;
            Zl0=1d;
        } else{
            if(Zl00!=Zl0){
                return null;
            }
            zl= Math.rint(lambda0*Zl1 -lambda1*Zl0);
        }

        if (zl < 0) {
            zl0 = zl + Zl0;
            zl1 = zl + Zl1;
        } else {
            zl0 = zl;
            zl1 = zl;
        }

        double lambda0Ret= (zl0 + lambda0)/ Zl0;
        double lambda1Ret= (zl1 + lambda1)/ Zl1;


        if(Zl0==1){
            lambda0Ret = lambda0;
            lambda1Ret = lambda1;
        }
        if (phi0Turn >= BOUND) {
            phi0Turn -= 1;
        }
        if (phi1Turn >= BOUND) {
            phi1Turn -= 1;
        }
        if (lambda0Ret >= BOUND) {
            lambda0Ret -= 1;
        }
        if (lambda1Ret >= BOUND) {
            lambda1Ret -= 1;
        }

        lambda0Ret = Math.rint(convert(lambda0Ret, TURN, T32));
        lambda1Ret =  Math.rint(convert(lambda1Ret, TURN, T32));
        phi0Turn = Math.rint(convert(phi0Turn, TURN, T32));
        phi1Turn = Math.rint(convert(phi1Turn, TURN, T32));



        if ((!GeoPos.isValidLatitudeT32((int) phi0Turn))) {
            return null;
        }
        if ((!GeoPos.isValidLatitudeT32((int)phi1Turn))) {
            return null;
        }

        if (mostRecent == 0) {
            return new GeoPos((int) lambda0Ret, (int) phi0Turn);
        } else {
            return new GeoPos((int) lambda1Ret, (int) phi1Turn);
        }

    }

}