package creepersan.com.cfilemanager.util;

import android.os.Bundle;
import android.support.annotation.NonNull;

/** Bundle建造器
 * Created by CreeperSan on 2017/11/19.
 */

public class BundleBuilder {
    private Bundle bundle;

    public BundleBuilder() {
        bundle = new Bundle();
    }

    public BundleBuilder put(String key,int value){
        bundle.putInt(key, value);
        return this;
    }

    public BundleBuilder put(String key,String value){
        bundle.putString(key, value);
        return this;
    }

    public BundleBuilder put(String key,float value){
        bundle.putFloat(key, value);
        return this;
    }

    public BundleBuilder put(String key,double value){
        bundle.putDouble(key, value);
        return this;
    }

    public BundleBuilder put(String key,char value){
        bundle.putChar(key, value);
        return this;
    }

    public BundleBuilder put(Bundle value){
        bundle.putAll(value);
        return this;
    }

    public @NonNull Bundle build(){
        return bundle;
    }
}
