package com.parse.makemymosaic;

/**
 * Created by Lenovo on 06-03-2017.
 */


import android.graphics.Bitmap;
import android.graphics.Color;

public class Utility {

    public static int[] getAverageColorRGB(Bitmap bitmap) {
        int R, G, B;
        int pixelColor;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;

        R = G = B = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixelColor = bitmap.getPixel(x, y);
                if (pixelColor == 0) {
                    size--;
                    continue;
                }
                R += Color.red(pixelColor);
                G += Color.green(pixelColor);
                B += Color.blue(pixelColor);


            }
        }

        if (R!=0 || G!=0 || B!=0){
            R /= size;
            G /= size;
            B /= size;


        }

        return (new int[]{ R, G, B });
    }
}


