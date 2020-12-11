package io.github.biligoldenwater.midiplayer.modules;

public class int2hexStrOrHexStr2int {
    public static String int2hexStr(int integer){
        if(integer == 0){
            return "00";
        }
        StringBuilder output = new StringBuilder();
        while (integer>=1){
            output.append(intMod16(integer));
            integer = integer / 16;
        }
        if(output.length()<2){
            output.append('0');
        }
        output.reverse();
        return output.toString();
    }

    public static int hexStr2int(String hexStr){
        hexStr = hexStr.toUpperCase();
        char[] chars = hexStr.toCharArray();
        int output = 0;
        char[] m = chars;
        chars = new char[m.length];
        int j=0;

        for(int i = chars.length-1;i>=0;--i){
            chars[j]=m[i];
            j++;
        }

        for(int i = 0;i<chars.length;++i){
            if(chars[i]>=0x41){
                output = output + ( (chars[i]-0x41+0xA) * (int) Math.pow(16,i) );
            }
            else {
                output = output + ((chars[i]-0x30) * (int) Math.pow(16,i));
            }
        }

        return output;
    }

    private static String intMod16(int integer){
        if(integer%16<10){
            return String.valueOf(integer%16);
        }
        else {
            return String.valueOf((char) (integer%16-10+0x41));
        }
    }
}
