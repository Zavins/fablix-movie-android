package edu.uci.ics.fablixmobile.utils;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.widget.TextView;

public class MakeGradient {
    public static void make(TextView text){
        TextPaint paint = text.getPaint();
        float width = paint.measureText("Search");
        Shader textShader = new LinearGradient(0, 0, width, text.getTextSize(),
                new int[]{
                        Color.parseColor("#FA8C4E"),
                        Color.parseColor("#FDC54E"),
                        Color.parseColor("#66C678"),
                        Color.parseColor("#478BEA"),
                        Color.parseColor("#9446DC"),
                }, null, Shader.TileMode.CLAMP);
        text.getPaint().setShader(textShader);
    }
}
