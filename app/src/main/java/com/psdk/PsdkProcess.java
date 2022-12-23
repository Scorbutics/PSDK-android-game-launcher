package com.psdk;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PsdkProcess {

    public static String readFromAssets(Context context, String assetScriptName) throws IOException {
        BufferedReader asset = new BufferedReader(new InputStreamReader(context.getAssets().open(assetScriptName)));

        StringBuffer scriptContent = new StringBuffer();
        String s;
        while ((s = asset.readLine()) != null) {
            scriptContent.append(s + "\n");
        }
        return scriptContent.toString();
    }
}
