package cn.xyz.testAndroid;

import android.util.Log;

import java.util.Date;

/**
 * Created by zhangzheng on 2016/8/8.
 */
public class RecodeTimeManager {

    private static String tag ="";
    private static long startTime;

    public static void init(String tag){
        RecodeTimeManager.tag = tag;
        RecodeTimeManager.startTime = new Date().getTime();
    }

    public static void print(){
        Log.i("RecodeTime",tag+":"+(new Date().getTime()-startTime));
    }

    public static void print(String message){
        Log.i("RecodeTime",message);
    }
}
