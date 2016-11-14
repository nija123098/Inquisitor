package um.nija123098.inquisitor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Made by nija123098 on 10/22/2016
 */
public class StringHelper {
    public static String getList(List<String> list){
        switch (list.size()){
            case 0:
                return "";
            case 1:
                return list.get(0);
            case 2:
                return list.get(0) + " and " + list.get(1);
            default:
                String s = "";
                for (int i = 0; i < list.size() - 1; i++) {
                    s += list.get(i) + ", ";
                }
                s += "and " + list.get(list.size() - 1);
                return s;
        }
    }
    public static String getList(String...list){
        List<String> strings = new ArrayList<String>(list.length);
        Collections.addAll(strings, list);
        return getList(strings);
    }
    public static boolean exclusiveLetters(String s){
        for (int i = 0; i < s.length(); i++) {
            Character character = s.charAt(i);
            if (!Character.isLetter(character)){
                return false;
            }
        }
        return true;// faster than name.matches("[a-zA-Z]+")
    }
    public static String getPossessive(String name){
        return name + "'" + (name.endsWith("s") ? "" : "s");
    }
    public static String limitOneSpace(String s){
        if (s.contains("  ")){
            return limitOneSpace(s.replace("  ", " "));
        }
        return s;
    }
    public static String flip(String s){
        String n = "";
        for (int i = s.length() - 1; i > -1; --i) {
            n += s.charAt(i);
        }
        return n;
    }
}
