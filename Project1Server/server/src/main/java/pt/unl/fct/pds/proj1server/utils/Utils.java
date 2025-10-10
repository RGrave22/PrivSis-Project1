package pt.unl.fct.pds.proj1server.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class Utils {


    public static double addNoise(double trueValue, double sensitivity, double epsilon) {
        double scale = sensitivity / epsilon;

        double noise = laplace(0.0, scale);
        return trueValue + noise;
    }

    //de acordo com o stackOverflow que encontrei
    public static double laplace(double mu, double b) {
        double u = Math.random() - 0.5;
        return mu - (b * Math.signum(u) * Math.log(1 - 2 * Math.abs(u)));
    }

    //Nao sei bem se vamos usar ou não o average
    public static double getSensitivity(String queryType, List<?> data, double min, double max) {
        switch (queryType.toLowerCase()) {
            case "count":
                return 1.0;
            case "average":
                if (data.isEmpty()) return 0.0;
                return (max - min) / data.size();//TODO nao sei se ta bem feito - ver livro!!!
            case "gCount":
                return 0; //TODO
            default:
                throw new IllegalArgumentException("Query unknown.");
        }
    }
}
