package com.cowbraingames.optimalmatcher_gemsofwar;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class BoardDetection {
    static{ System.loadLibrary("opencv_java4"); }
    private final Mat board;
    private final ImageView testImg;

    public BoardDetection(Bitmap boardBitmap, ImageView testImg){
        this.testImg = testImg;
        this.board = new Mat();
        Bitmap bmp32 = boardBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, this.board);
        Imgproc.cvtColor(board, board, Imgproc.COLOR_RGB2GRAY);
        Bitmap edges = matToBitmap(edgeDetection());
        testImg.setImageBitmap(edges);
    }

    private Mat edgeDetection(){
        Mat edges = new Mat();
        Mat lines = new Mat();
        Imgproc.Canny(board, edges, 50, 100);
        Imgproc.HoughLines(edges, lines, 1.0, Math.PI/180.0, 1000);

        Point p1 = new Point();
        Point p2 = new Point();
        double a, b;
        double x0, y0;
        for(int i = 0; i < lines.rows(); i++){
            double[] vec = lines.get(i, 0);
            double rho = vec[0];
            double theta = vec[1];
            a = Math.cos(theta);
            b = Math.sin(theta);
            x0 = a*rho;
            y0 = b*rho;

            p1.x = Math.round(x0 + 10000*(-b));
            p1.y = Math.round(y0 + 10000*a);
            p2.x = Math.round(x0 - 10000*(-b));
            p2.y = Math.round(y0 - 10000*a);

            Imgproc.line(edges, p1, p2, new Scalar(255,255,255), 1, Imgproc.LINE_AA, 0);
        }
        System.out.println("Edges: " + edges.rows() + ' ' + edges.cols() + ' ' + edges.depth());
        System.out.println("Lines: " + lines.rows() + ' ' + lines.cols() + ' ' + lines.depth());
        return edges;
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
