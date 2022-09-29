package com.bsu.prog.balls.balls;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.LinkedList;
import java.util.List;

public class Ball {
    private static final double eps = 0.001;
    public static final Point2D STOP = new Point2D(-1,-1);
    List<Point2D> lastPos;
    Point2D coord, speed,slowDown;
    double slowDownPercent;

    Circle circle;
    public Ball(Point2D _coord,Point2D _speed,double _slowDownPercent,Circle _circle){
        coord = _coord;
        speed = _speed;
        slowDownPercent=_slowDownPercent;
        slowDown =speed.multiply(slowDownPercent);
        lastPos=new LinkedList<>();
        lastPos.add(coord);
        circle =_circle;
    }


    public Point2D calcNewPos(Point2D x1y1,Point2D x2y2 ){

        if (Math.abs(speed.getX())<eps && Math.abs(speed.getY())<eps){
            return STOP;
        }

        var newCoord=coord.add(speed).subtract(slowDown.multiply(0.5));
        if (x1y1.getX()>=newCoord.getX() || x1y1.getY()>=newCoord.getY() ||x2y2.getX()<=newCoord.getX() ||x2y2.getY()<=newCoord.getY()){
            Point2D axe,xy,n;
            if (newCoord.getY()<=x1y1.getY() || newCoord.getY()>=x2y2.getY()){
                axe = new Point2D(1,0);
                n = new Point2D(0,newCoord.getY()<=x1y1.getY()?1:-1);
                xy=newCoord.getY()<=x1y1.getY() ?new Point2D(0, x1y1.getY()):new Point2D(0,x2y2.getY());

            }else{
                axe = new Point2D(0,1);
                n=new Point2D(newCoord.getX()<=x1y1.getX()?1:-1,0);
                xy=newCoord.getX()<=x1y1.getX() ?new Point2D(x1y1.getX(), 0):new Point2D(x2y2.getX(),0);
            }

            var matr =new double[][]{
                    {1*axe.getX()-1*axe.getY(), 0,                         axe.getY()*xy.getX()},
                    {0,                        -1*axe.getX()+1*axe.getY(), axe.getX()*xy.getY()},
                    {0,                         0,                         1},

            };

            double x= newCoord.getX(),
                    y= newCoord.getY();
            x=x*matr[0][0]+y*matr[0][1]+2*matr[0][2];
            y=x*matr[1][0]+y*matr[1][1]+2*matr[1][2];
            newCoord=new Point2D(x,y);

            speed=speed.subtract(n.multiply(2*speed.dotProduct(n)));
            slowDown=speed.multiply(slowDownPercent);

        }
        speed=speed.subtract(slowDown);
        lastPos.add(newCoord);
        coord=newCoord;
        return  coord;
    }
}
