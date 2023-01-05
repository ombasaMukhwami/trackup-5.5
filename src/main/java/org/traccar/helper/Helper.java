package org.traccar.helper;

import org.h2.util.StringUtils;

import java.util.Calendar;
import java.util.Date;

public final class Helper {

    private Helper() {
    }
    public static byte verify(byte[] b) {
        byte a = 0;
        for (int i = 0; i < b.length; i++) {
            a = (byte) (a ^ b[i]);
        }
        return a;
    }

    public static String asciiToHexString(String ascii) {
        char[] ch = ascii.toCharArray();

        StringBuilder builder = new StringBuilder();
        for (char c : ch) {
            // Step-2 Use %H to format character to Hex
            String hexCode = String.format("%H", c);
            builder.append(hexCode);
        }

        return  builder.toString();
    }

    public static String toBinaryx(int no) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (no > 0) {
            result.append(no % 2);
            i++;
            no = no / 2;
        }
        result.reverse();
        return result.toString();
    }

    public static String toBinary(int numVal) {
        // Declare a few variables we're going to need
        StringBuilder binaryResult = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            binaryResult.append(numVal % 2);
            numVal = numVal / 2;
        }
        return binaryResult.reverse().toString();

    }
    public static String reversString(String s) {
        // Declare a few variables we're going to need
        if (s.length() == 2) {
            return  s;
        }
        StringBuilder  result = new StringBuilder();
        for (int i = 0; i <= s.length() - 2; i = i + 2) {
            result.append(new StringBuilder(s.substring(i, i + 2)).reverse());
        }
        return  result.reverse().toString();

    }
    public static String toBinary(int numVal, int bit) {
        // Declare a few variables we're going to need
        StringBuilder binaryResult = new StringBuilder();
        for (int i = 0; i < bit; i++) {
            binaryResult.append(numVal % 2);
            numVal = numVal / 2;
        }
        return binaryResult.reverse().toString();

    }
    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static int toInteger(String s) {
        int x = 128;
        int total = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                total = total + (x * 1);
            }
            x = x / 2;
        }

        return  total;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i),  16) << 4) + Character.digit(s.charAt(i + 1),  16));
        }
        return data;
    }
    public static byte[] hexStringToByteArrayTest(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index,  index + 2),  16);
            b[i] = (byte) v;
        }
        return b;
    }
    public static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public  static  String byteToHex(int b) {
        int i = b & 0XFF;
        //return String.format("%02s",  Integer.toHexString(i));
        return String.format("%02x", i).toUpperCase();
    }

    public static String getTimeString(int nDateTime) {
        int ndate = 0;
        StringBuilder sb = new StringBuilder();
        ndate = nDateTime >> 26; //year
        sb.append((ndate / 10));
        sb.append((ndate % 10) + "-");
        ndate = nDateTime >> 22 & 0x0f; //month f=15
        sb.append((ndate / 10));
        sb.append((ndate % 10) + "-");
        ndate = nDateTime >> 17 & 0x1f; //day 2f=31
        sb.append((ndate / 10));
        sb.append((ndate % 10) + " ");
        ndate = nDateTime >> 12 & 0x1f; //hour 3f=63
        sb.append((ndate / 10));
        sb.append((ndate % 10) + ":");
        ndate = nDateTime >> 6 & 0x3f; //minute
        sb.append((ndate / 10));
        sb.append((ndate % 10) + ":");
        ndate = nDateTime & 0x3f; //second
        sb.append((ndate / 10));
        sb.append((ndate % 10));

        return sb.toString();
    }
    public static String removeZero(String str) {
        if (StringUtils.isNumber(str)) {
            return str; // return if it is valid number
        }
        // Count leading zeros
        int i = 0;
        while (i < str.length() && str.charAt(i) == '0') {
            i++;
        }

        StringBuffer sb = new StringBuffer(str);
        // The  StringBuffer replace function removes
        // i characters from given index (0 here)
        sb.replace(0, i, "");
        return sb.toString();  // return in String
    }
    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
