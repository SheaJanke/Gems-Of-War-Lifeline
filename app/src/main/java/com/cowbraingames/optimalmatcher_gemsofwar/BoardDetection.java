package com.cowbraingames.optimalmatcher_gemsofwar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.checkerframework.checker.units.qual.A;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BoardDetection {
    static{ System.loadLibrary("opencv_java4"); }
    private final Mat board, edges, circles;
    private Bitmap compressedBoard;

    public BoardDetection(Bitmap boardBitmap, ImageView testImg, Activity mainActivity){
        board = new Mat();
        edges = new Mat();
        circles = new Mat();
        Bitmap bmp32 = boardBitmap.copy(Bitmap.Config.ARGB_8888, true);
        compressedBoard = Bitmap.createScaledBitmap(bmp32, bmp32.getWidth()/6, bmp32.getHeight()/6, true);
        Utils.bitmapToMat(compressedBoard, board);
        detectEdges();
        detectCircles();
        drawCircles();
        Bitmap img = matToBitmap(edges);
        mainActivity.runOnUiThread(() -> testImg.setImageBitmap(img));

    }

    private void detectEdges(){
        System.out.println("Starting Edge Detection");
        double totalPixels = board.rows() * board.cols();
        double curWhite = 0;
        int threshold1 = 500;
        while(curWhite/totalPixels < 0.08){
            Imgproc.Canny(board, edges, threshold1, 2.5*threshold1);
            curWhite = Core.countNonZero(edges);
            System.out.println("Threshold: " + threshold1 + " ratio: " + curWhite/totalPixels);
            threshold1*= 0.95;
        }
        System.out.println("Finished Edge Detection");

    }



    private void detectCircles(){
        System.out.println("Starting HoughCircles");
        int minDimension = Math.min(edges.rows(), edges.cols());
        int maxRadius = minDimension/16;
        int minRadius = maxRadius/2;
        int curThreshold = 200;
        while(circles.cols() < 65){
            Imgproc.HoughCircles(edges, circles, Imgproc.CV_HOUGH_GRADIENT, 1.5, 2*minRadius, 10, curThreshold, minRadius,  maxRadius);
            System.out.println("curThreshold: " + curThreshold + " Circles found: " + circles.cols());
            curThreshold *= 0.95;
        }
    }

    private void drawCircles(){
        ArrayList<Pair<Integer, Point>> coords = new ArrayList<>();
        for (int x = 0; x < circles.cols(); x++)
        {
            double vCircle[] = circles.get(0,x);

            if (vCircle == null)
                break;

            Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int)Math.round(vCircle[2]);
            coords.add(Pair.create(radius, pt));

        }
        coords.sort((p1, p2) -> p1.first - p2.first);
        int medianRadius = coords.get(coords.size()/3).first;
        coords.removeIf(pt -> pt.first/(double)medianRadius < 0.85 || pt.first/(double)medianRadius > 1.15);
        coords.sort((p1, p2) -> (int) (p1.second.x - p2.second.x));
        double medianX = coords.get(coords.size()/2).second.x;
        coords.sort((p1, p2) -> (int) (p1.second.y - p2.second.y));
        double medianY = coords.get(coords.size()/2).second.y;
        coords.sort((p1, p2) -> (int) (distance(p1.second, medianX, medianY) - distance(p2.second, medianX, medianY)));
        boolean[] visited = new boolean[coords.size()];
        Map<Integer, Pair<Integer,Integer>> pos = new HashMap<>();
        pos.put(0, Pair.create(0, 0));
        dfs(coords, pos, visited,0, medianRadius);
        System.out.println("Coords.size: " + coords.size());
        for(int i = 0; i < coords.size(); i++){
            Imgproc.circle(edges, coords.get(i).second, coords.get(i).first, new Scalar(255,255,255), visited[i] ? 5 : 2);
        }
    }

    void dfs(ArrayList<Pair<Integer, Point>> coords, Map<Integer, Pair<Integer,Integer>> pos, boolean[] visited, int cur, int medianRadius){
        if(visited[cur]){
            return;
        }
        Point p = coords.get(cur).second;
        for(int i = 0; i < coords.size(); i++){
            if(i != cur && !visited[i]){
                visited[cur] = true;
                Point p2 = coords.get(i).second;
                double threshold = Math.pow(medianRadius, 2)/4;
                boolean works = false;
                Pair<Integer, Integer> curPos = pos.get(cur);
                for(int j = 1; j <= 3; j++){
                    if(distance(p2, p.x - j*2*medianRadius, p.y) < threshold){
                        pos.put(i, Pair.create(curPos.first - j, curPos.second));
                        works = true;
                    }
                    if(distance(p2, p.x + j*2*medianRadius, p.y) < threshold){
                        pos.put(i, Pair.create(curPos.first + j, curPos.second));
                        works = true;
                    }
                    if(distance(p2, p.x, p.y-j*2*medianRadius) < threshold){
                        pos.put(i, Pair.create(curPos.first, curPos.second - j));
                        works = true;
                    }
                    if(distance(p2, p.x, p.y+j*2*medianRadius) < threshold){
                        pos.put(i, Pair.create(curPos.first, curPos.second + j));
                        works = true;
                    }
                }
                if(works){
                    dfs(coords, pos, visited, i, medianRadius);
                }
            }
        }
    }

    private int[][] hasBoard(Map<Integer, Pair<Integer,Integer>> pos){
        int minX = 100, maxX = -100, minY = 100, maxY = -100;
        for(Pair<Integer, Integer> val: pos.values()){
            minX = Math.min(minX, val.first);
            maxX = Math.max(maxX, val.first);
            minY = Math.min(minY, val.second);
            maxY = Math.max(maxY, val.second);
        }
        int[] x = new int[maxX-minX+1];
        int[] y = new int[maxY-minY+1];
        for(Pair<Integer, Integer> val: pos.values()){
            x[val.first-minX]++;
            y[val.second-minY]++;
        }
        int l = 0, r = x.length-1;
        while(r-l+1 > 8){
            if(x[l] > x[r]){
                r--;
            }else{
                l++;
            }
        }
        if(r-l+1 < 8){
            return new int[0][0];
        }
        for(int i = l; i <= r; i++){
            if(x[i] < 2){
                return new int[0][0];
            }
        }
        int xCorrection = l;
        l = 0;
        r = y.length-1;
        while(r-l+1 > 8){
            if(y[l] > y[r]){
                r--;
            }else{
                l++;
            }
        }
        if(r-l+1 < 8){
            return new int[0][0];
        }
        for(int i = l; i <= r; i++){
            if(y[i] < 2){
                return new int[0][0];
            }
        }
        int yCorrection = l;
        int[][] answer = new int[8][8];
        for(int key: pos.keySet()){
            Pair<Integer, Integer> val = pos.get(key);
            if(0 <= val.first - xCorrection  && val.first - xCorrection < 8){
                if(0 <= val.second - yCorrection  && val.second - yCorrection < 8){
                    answer[val.first - xCorrection][val.second - yCorrection] = key;
                }
            }
        }
        return answer;
    }

    private double distance(Point p, double targetX, double targetY){
        return Math.pow(p.x - targetX, 2) + Math.pow(p.y - targetY, 2);
    }


    private Bitmap matToBitmap(Mat img){
        Bitmap bmp = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888);
        Mat tmp = new Mat (img.width(), img.height(), CvType.CV_8UC1,new Scalar(4));
        try {
            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
            Imgproc.cvtColor(img, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
            Utils.matToBitmap(tmp, bmp);
        } catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }
}
