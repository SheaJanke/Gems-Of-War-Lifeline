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
