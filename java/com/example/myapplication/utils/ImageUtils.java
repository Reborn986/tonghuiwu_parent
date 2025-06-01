package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageUtils {

    public static List<Integer> getDominantColors(Bitmap bitmap, int numColors) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

        Bitmap hsvBitmap = resizedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(1);
        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas canvas = new Canvas(hsvBitmap);
        canvas.drawBitmap(hsvBitmap, 0, 0, paint);

        int[] pixels = new int[hsvBitmap.getWidth() * hsvBitmap.getHeight()];
        hsvBitmap.getPixels(pixels, 0, hsvBitmap.getWidth(), 0, 0, hsvBitmap.getWidth(), hsvBitmap.getHeight());


        HashMap<Integer, Integer> colorCounts = new HashMap<>();
        for (int pixel1 : pixels) {
            int color = getSimilarColor(pixel1);
            if (color != Color.BLACK) {
                if (colorCounts.containsKey(color)) {
                    colorCounts.put(color, colorCounts.get(color) + 1);
                } else {
                    colorCounts.put(color, 1);
                }
            }
        }

        List<Map.Entry<Integer, Integer>> sortedColorCounts = new ArrayList<>(colorCounts.entrySet());
        List<Map.Entry<Integer, Integer>> resultColorCounts = mergeSimilarColors(sortedColorCounts,20,10f);
        Collections.sort(resultColorCounts, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> entry1, Map.Entry<Integer, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        List<Integer> dominantColors = new ArrayList<>();
        for (int i = 0; i < numColors && i < resultColorCounts.size(); i++) {
            dominantColors.add(resultColorCounts.get(i).getKey());
        }

        return dominantColors;
    }

    private static List<Map.Entry<Integer, Integer>> mergeSimilarColors(List<Map.Entry<Integer, Integer>> sortedColorCounts, float hueThreshold, float saturationThreshold) {
        List<Map.Entry<Integer, Integer>> mergedColors = new ArrayList<>();
        //创建一个空的结果列表(mergedColors)
        for (int i = 0; i < sortedColorCounts.size(); i++) {//遍历已排序的颜色计数列表(sortedColorCounts)
            Map.Entry<Integer, Integer> currentEntry = sortedColorCounts.get(i);
            int currentColor = currentEntry.getKey();
            int currentCount = currentEntry.getValue();
            //对于每个颜色计数项，获取颜色和计数
            boolean isSimilarColorFound = false;
            //设置一个标志变量(isSimilarColorFound)来表示是否找到相似颜色。
            for (int j = 0; j < mergedColors.size(); j++) {
                Map.Entry<Integer, Integer> mergedEntry = mergedColors.get(j);
                int mergedColor = mergedEntry.getKey();
                int mergedCount = mergedEntry.getValue();
                // 对于每个已合并的颜色计数项，获取颜色和计数。
                if (areColorsSimilar(currentColor, mergedColor, hueThreshold, saturationThreshold)) {
                    //调用areColorsSimilar方法来判断当前颜色和已合并颜色是否相似。
                    mergedColors.set(j, new AbstractMap.SimpleEntry<>(mergedColor, mergedCount + currentCount));
                    isSimilarColorFound = true;
                    break;
                    //如果相似，将已合并颜色的计数增加当前计数，并更新结果列表中的对应项。
                    //设置标志变量为true，并跳出内部循环。
                }
            }

            if (!isSimilarColorFound) {
                mergedColors.add(new AbstractMap.SimpleEntry<>(currentColor, currentCount));
            }
        }

        return mergedColors;
    }

    private static boolean areColorsSimilar(int color1, int color2, float hueThreshold, float saturationThreshold) {
        float[] hsv1 = new float[3];
        float[] hsv2 = new float[3];

        Color.colorToHSV(color1, hsv1);
        Color.colorToHSV(color2, hsv2);

        float hueDiff = Math.abs(hsv1[0] - hsv2[0]);
        float saturationDiff = Math.abs(hsv1[1] - hsv2[1]);

        return hueDiff <= hueThreshold && saturationDiff <= saturationThreshold;
    }

    private static int getSimilarColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        // 可以根据自己的需求调整颜色相似性的判断条件
        // 这里使用色调（Hue）和饱和度（Saturation）的差值来判断颜色相似性
        // 如果两个颜色的色调和饱和度的差值都小于一个阈值，就将它们合并为同一个颜色值
        float hueThreshold = 0f; // 色调差值阈值
        float saturationThreshold = 0.2f; // 饱和度差值阈值

        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];

        // 将色调映射到0-360范围内
        if (hue < 0) {
            hue += 360;
        }

        // 计算合并后的颜色值
        return Color.HSVToColor(new float[]{hue, saturation, value});
         }
    }

//    public static List<Integer> getDominantColors(String imagePath, int numColors) {
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
//
//        Bitmap hsvBitmap = resizedBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation(1); // 将饱和度设置为1，保留鲜艳度
//        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
//        Paint paint = new Paint();
//        paint.setColorFilter(colorFilter);
//        Canvas canvas = new Canvas(hsvBitmap);
//        canvas.drawBitmap(hsvBitmap, 0, 0, paint);
//
//        int[] pixels = new int[hsvBitmap.getWidth() * hsvBitmap.getHeight()];
//        hsvBitmap.getPixels(pixels, 0, hsvBitmap.getWidth(), 0, 0, hsvBitmap.getWidth(), hsvBitmap.getHeight());
//
//        HashMap<Integer, Integer> colorCounts = new HashMap<>();
//        for (int pixel : pixels) {
//            if (colorCounts.containsKey(pixel)) {
//                colorCounts.put(pixel, colorCounts.get(pixel) + 1);
//            } else {
//                colorCounts.put(pixel, 1);
//            }
//        }
//
//        List<Map.Entry<Integer, Integer>> sortedColorCounts = new ArrayList<>(colorCounts.entrySet());
//        Collections.sort(sortedColorCounts, new Comparator<Map.Entry<Integer, Integer>>() {
//            @Override
//            public int compare(Map.Entry<Integer, Integer> entry1, Map.Entry<Integer, Integer> entry2) {
//                float[] hsv1 = new float[3];
//                float[] hsv2 = new float[3];
//                Color.colorToHSV(entry1.getKey(), hsv1);
//                Color.colorToHSV(entry2.getKey(), hsv2);
//                float saturation1 = hsv1[1];
//                float saturation2 = hsv2[1];
//                float brightness1 = hsv1[2];
//                float brightness2 = hsv2[2];
//                return (saturation2 * brightness2) - (saturation1 * brightness1) > 0 ? 1 : -1;
//            }
//        });
//
//        List<Integer> dominantColors = new ArrayList<>();
//        for (int i = 0; i < numColors && i < sortedColorCounts.size(); i++) {
//            dominantColors.add(sortedColorCounts.get(i).getKey());
//        }
//
//        return dominantColors;
//    }

//    public static List<Integer> getDominantColors(String imagePath, int numColors) {
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
//
//        int[] pixels = new int[resizedBitmap.getWidth() * resizedBitmap.getHeight()];
//        resizedBitmap.getPixels(pixels, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());
//
//        HashMap<Integer, Integer> colorCounts = new HashMap<>();
//        for (int pixel : pixels) {
//            if (colorCounts.containsKey(pixel)) {
//                colorCounts.put(pixel, colorCounts.get(pixel) + 1);
//            } else {
//                colorCounts.put(pixel, 1);
//            }
//        }
//
//        List<Map.Entry<Integer, Integer>> sortedColorCounts = new ArrayList<>(colorCounts.entrySet());
//        Collections.sort(sortedColorCounts, new Comparator<Map.Entry<Integer, Integer>>() {
//            @Override
//            public int compare(Map.Entry<Integer, Integer> entry1, Map.Entry<Integer, Integer> entry2) {
//                return entry2.getValue().compareTo(entry1.getValue());
//            }
//        });
//
//        List<Integer> dominantColors = new ArrayList<>();
//        for (int i = 0; i < numColors && i < sortedColorCounts.size(); i++) {
//            dominantColors.add(sortedColorCounts.get(i).getKey());
//        }
//
//        return dominantColors;
//    }
