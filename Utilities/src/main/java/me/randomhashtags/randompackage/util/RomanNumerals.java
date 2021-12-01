package me.randomhashtags.randompackage.util;

import java.util.TreeMap;

public interface RomanNumerals {
    TreeMap<Integer, String> ROMAN_MAP = new TreeMap<Integer, String>() {{
        put(1000, "M");
        put(900, "CM");
        put(500, "D");
        put(400, "CD");
        put(100, "C");
        put(90, "XC");
        put(50, "L");
        put(40, "XL");
        put(10, "X");
        put(9, "IX");
        put(5, "V");
        put(4, "IV");
        put(1, "I");
    }};
    default String toRoman(int number) {
        /* This code is from "bhlangonijr" at https://stackoverflow.com/questions/12967896 */
        if(number <= 0) {
            return "";
        }
        int l = ROMAN_MAP.floorKey(number);
        if(number == l) {
            return ROMAN_MAP.get(number);
        }
        return ROMAN_MAP.get(l) + toRoman(number-l);
    }
    enum RomanNumeralValues {
        I(1), X(10), C(100), M(1000), V(5), L(50), D(500);
        private final int value;
        RomanNumeralValues(int value) {
            this.value = value;
        }
        public int asInt() {
            return value;
        }
    }
    default int fromRoman(String input) {
        /*
            TODO: when Spigot calls this, stripColor (ChatColor.stripColor)
         */
        /* This code is from "batman" at https://stackoverflow.com/questions/9073150 */
        input = input.toUpperCase();
        int intNum = 0, prev = 0;
        for(int i = input.length()-1; i >= 0; i--) {
            final String character = input.substring(i, i+1);
            int temp = RomanNumeralValues.valueOf(character).asInt();
            if(temp < prev) {
                intNum -= temp;
            } else {
                intNum += temp;
            }
            prev = temp;
        }
        return intNum;
    }
}
