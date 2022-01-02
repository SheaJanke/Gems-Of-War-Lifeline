package com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.cowbraingames.optimalmatcher_gemsofwar.Exceptions.BoardNotFoundException;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BoardDetection {
    static{ System.loadLibrary("opencv_java4"); }
    private final Mat board, edges, circles;
    private final Bitmap fullBoard;
    private final Bitmap[][] orbs;

    public BoardDetection(Bitmap boardBitmap) throws BoardNotFoundException {
        board = new Mat();
        edges = new Mat();
        circles = new Mat();
        fullBoard = boardBitmap;
        orbs = new Bitmap[8][8];
        Bitmap bmp32 = boardBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap compressedBoard = Bitmap.createScaledBitmap(bmp32, bmp32.getWidth() / 6, bmp32.getHeight() / 6, true);
        Utils.bitmapToMat(compressedBoard, board);
        try {
            detectEdges();
            detectCircles();
        } catch (Exception e) {
            throw new BoardNotFoundException();
        }
    }

    private void detectEdges(){
        double totalPixels = board.rows() * board.cols();
        double curWhite = 0;
        int threshold1 = 500;
        while(curWhite/totalPixels < 0.1){
            Imgproc.Canny(board, edges, threshold1, 2.5*threshold1);
            curWhite = Core.countNonZero(edges);
            threshold1*= 0.95;
        }
    }



    private void detectCircles() throws BoardNotFoundException{
        final int MIN_THRESHOLD = 12;
        int minDimension = Math.min(edges.rows(), edges.cols());
        int maxRadius = minDimension/14;
        int minRadius = (int)(maxRadius/1.5);
        int curThreshold = 200;
        boolean foundBoard = false;
        while(!foundBoard){
            if(curThreshold < MIN_THRESHOLD){
                throw new BoardNotFoundException();
            }
            Imgproc.HoughCircles(edges, circles, Imgproc.CV_HOUGH_GRADIENT, 1.5, 2*minRadius, 10, curThreshold, minRadius,  maxRadius);
            foundBoard = drawCircles();
            curThreshold *= 0.95;
        }
    }

    private boolean drawCircles(){
        ArrayList<Pair<Integer, Point>> coords = new ArrayList<>();
        for (int x = 0; x < circles.cols(); x++)
        {
            double[] vCircle = circles.get(0,x);

            if (vCircle == null)
                break;

            Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int)Math.round(vCircle[2]);
            coords.add(Pair.create(radius, pt));

        }
        if(coords.isEmpty()){
            return false;
        }
        Collections.sort(coords, (p1, p2) -> p1.first - p2.first);
        int medianRadius = coords.get(coords.size()/3).first;
        ArrayList<Pair<Integer, Point>> filteredCoords = new ArrayList<>();
        for(Pair<Integer, Point> pt: coords) {
            if(pt.first/(double)medianRadius >= 0.85 && pt.first/(double)medianRadius <= 1.15) {
                filteredCoords.add(pt);
            }
        }
        coords = filteredCoords;
        Collections.sort(coords, (p1, p2) -> (int) (p1.second.x - p2.second.x));
        double medianX = coords.get(coords.size()/2).second.x;
        Collections.sort(coords, (p1, p2) -> (int) (p1.second.y - p2.second.y));
        double medianY = coords.get(coords.size()/2).second.y;
        Collections.sort(coords, (p1, p2) -> (int) (distance(p1.second, medianX, medianY) - distance(p2.second, medianX, medianY)));
        boolean[] visited = new boolean[coords.size()];
        Map<Integer, Pair<Integer,Integer>> pos = new HashMap<>();
        pos.put(0, Pair.create(0, 0));
        dfs(coords, pos, visited,0, medianRadius);
        int[][] boardPos = getBoardPos(pos);
        if(boardPos.length < 8){
            return false;
        }
        Point[][] circleCenters = new Point[8][8];
        for(int i = 0; i < 8; i++){
            int lowestIndex = 7, highestIndex = 0;
            for(int j = 0; j < 8; j++){
                if(boardPos[i][j] != -1){
                    lowestIndex = Math.min(j, lowestIndex);
                    highestIndex = Math.max(j, highestIndex);
                }
            }
            Point l = coords.get(boardPos[i][lowestIndex]).second;
            Point r = coords.get(boardPos[i][highestIndex]).second;
            double dx = (r.x - l.x)/(highestIndex-lowestIndex);
            double dy = (r.y - l.y)/(highestIndex-lowestIndex);
            for(int j = 0; j < 8; j++){
                circleCenters[i][j] = new Point(l.x + dx*(j-lowestIndex), l.y + dy*(j-lowestIndex));
            }
        }
        for(int j = 0; j < 8; j++){
            int lowestIndex = 7, highestIndex = 0;
            for(int i = 0; i < 8; i++){
                if(boardPos[i][j] != -1){
                    lowestIndex = Math.min(i, lowestIndex);
                    highestIndex = Math.max(i, highestIndex);
                }
            }
            Point top = coords.get(boardPos[lowestIndex][j]).second;
            Point bottom = coords.get(boardPos[highestIndex][j]).second;
            double dx = (bottom.x - top.x)/(highestIndex-lowestIndex);
            double dy = (bottom.y - top.y)/(highestIndex-lowestIndex);
            for(int i = 0; i < 8; i++){
                Point center = new Point(top.x + dx*(i-lowestIndex), top.y + dy*(i-lowestIndex));
                Point averageCenter = averagePoint(center, circleCenters[i][j]);
                int squareDim = (int)(2.2*medianRadius);
                Rect rectCrop = new Rect((int)averageCenter.x - squareDim/2, (int)averageCenter.y - squareDim/2, squareDim, squareDim);
                Imgproc.rectangle(edges, rectCrop, new Scalar(255,255,255), 3);
                orbs[j][i] = getCroppedBitmap(averageCenter, medianRadius);
            }
        }
        return true;
    }

    private Bitmap getCroppedBitmap(Point circleCenter, int radius){
        int squareDim = (int)(2.2*radius*6);
        int targetX = (int) (circleCenter.x * 6 - squareDim/2);
        int targetY = (int) (circleCenter.y * 6 - squareDim/2);
        int x = Math.max(targetX, 0);
        int y = Math.max(targetY, 0);
        int width = Math.min(squareDim, fullBoard.getWidth() - x);
        int height = Math.min(squareDim, fullBoard.getHeight() - y);
        return Bitmap.createBitmap(fullBoard, x, y, width, height);
    }

    private Point averagePoint(Point p1, Point p2){
        return new Point((p1.x + p2.x)/2, (p1.y + p2.y)/2);
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
                double threshold = Math.pow(medianRadius, 2)/6;
                boolean works = false;
                Pair<Integer, Integer> curPos = pos.get(cur);
                assert curPos != null;
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

    private int[][] getBoardPos(Map<Integer, Pair<Integer,Integer>> pos){
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
        int xCorrection = minX + l;
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
        int yCorrection = minY + l;
        int[][] answer = new int[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                answer[i][j] = -1;
            }
        }
        for(int key: pos.keySet()){
            Pair<Integer, Integer> val = pos.get(key);
            assert val != null;
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

    public Bitmap[][] getOrbs(){
        return orbs;
    }
}
