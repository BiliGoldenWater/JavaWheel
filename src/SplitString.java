package io.github.biligoldenwater.midiplayer.modules;

import java.util.Arrays;
import java.util.Objects;

public class SplitString {
    public static String[] splitString(String regex,String str){
        int count=0;

        for (int i=0;i<regex.length();++i){
            if (regex.charAt(i)=='|')count++;
        }

        char[] regexs;


        regexs=new char[count+1];


        if(count>0){
            count=0;

            for(int i=0;i<regex.length();++i){
                if(regex.charAt(i)!='|'){
                    regexs[count]=regex.charAt(i);
                    count++;
                }
            }
        }
        else if(regex.length()==1){
            regexs[0]=regex.charAt(0);
        }

        count=0;

        for (int i=1;i<str.length()-1;++i){
            for(char j:regexs){
                if(str.charAt(i)==j)count++;
            }
        }

        String[] strs = new String[count+1];
        byte flag = 1;
        count=0;

        Arrays.fill(strs, "\0");

        for(int i=0;i<str.length();++i){
            for(char j:regexs){
                if(str.charAt(i)!=j){
                    flag=0;
                }
                else if(str.charAt(i)==j){
                    flag=1;
                    if(i==0 || i+1==str.length())flag=2;
                    break;
                }
            }
            if(flag==0){
                if(Objects.equals(strs[count], "\0")){
                    strs[count]=String.valueOf(str.charAt(i));
                }
                else{
                    strs[count]=strs[count]+str.charAt(i);
                }
            }
            else if(flag==1){
                count++;
            }
        }

        return strs;
    }
}
