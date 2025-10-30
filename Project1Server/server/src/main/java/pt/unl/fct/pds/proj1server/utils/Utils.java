package pt.unl.fct.pds.proj1server.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class Utils {


    public static double addNoise(double trueValue, double sensitivity, double epsilon) {
        double scale = sensitivity / epsilon;

        double noise = laplace(0.0, scale);
        System.out.println("noise: " + noise);
        return trueValue + noise;
    }

    //de acordo com o stackOverflow que encontrei
    public static double laplace(double mu, double b) {
        double u = Math.random() - 0.5;
        return mu - (b * Math.signum(u) * Math.log(1 - 2 * Math.abs(u)));
    }


    public static String[] reorder(String a1, String a2, String v1, String v2) {
        String x = a1.toLowerCase();
        String y = a2.toLowerCase();

        boolean xy = x.compareTo(y) <= 0;

        String leftAttr  = xy ? x  : y;
        String rightAttr = xy ? y  : x;

        String leftVal  = a1.equalsIgnoreCase(leftAttr)  ? v1 : v2;
        String rightVal = a2.equalsIgnoreCase(rightAttr) ? v2 : v1;

        return new String[]{ leftAttr, rightAttr, leftVal, rightVal };
    }
}
