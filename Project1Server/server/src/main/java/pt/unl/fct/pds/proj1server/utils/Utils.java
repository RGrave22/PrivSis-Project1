package pt.unl.fct.pds.proj1server.utils;

import java.util.List;

public class Utils {

    public static double addNoise(double trueValue, double sensitivity, double epsilon) {
        double scale = sensitivity / epsilon;
        double u = Math.random() - 0.5;
        //Não sei se isto esta bem
        double noise = -scale * Math.signum(u) * Math.log(1 - 2 * Math.abs(u));
        return trueValue + noise;
    }

    //Nao sei bem se vamos usar ou não o average
    public static double getSensitivity(String queryType, List<?> data, double min, double max) {
        switch (queryType.toLowerCase()) {
            case "count":
                return 1.0;
            case "average":
                if (data.isEmpty()) return 0.0;
                return (max - min) / data.size();
            default:
                throw new IllegalArgumentException("Query unknown.");
        }
    }
}
