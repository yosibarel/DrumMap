package Utils;

import java.util.ArrayList;

import interfaces.TextValueFormater;

/**
 * Created by yossibarel on 29/04/16.
 */
public class TextFormaterCreator {

    public static String[] stepList = {"1/4", "1/2", "1", "2", "3", "4"};

    public static ArrayList<TextValueFormater> getCollection() {
        ArrayList<TextValueFormater> formaters = new ArrayList<>();

        formaters.add(new TextValueFormater() {
            @Override
            public String getValue(double value) {
                return (int) (value * 127) + "";
            }
        });
        formaters.add(new TextValueFormater() {
            @Override
            public String getValue(double value) {
                return (int) ((value - 0.5) * 24.0) + "";
            }
        });
        formaters.add(new TextValueFormater() {
            @Override
            public String getValue(double value) {
                return String.format("%.02f", value);
            }
        });
        formaters.add(new TextValueFormater() {
            @Override
            public String getValue(double value) {
                return stepList[(int) (value * 5)];
            }
        });
        formaters.add(new TextValueFormater() {
            @Override
            public String getValue(double value) {
                return String.format("%.02f", value);
            }
        });
        formaters.add(new TextValueFormater() {
            @Override
            public String getValue(double value) {
                return String.format("%.02f", value);
            }
        });

        return formaters;
    }
}
