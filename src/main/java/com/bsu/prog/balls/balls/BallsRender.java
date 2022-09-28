package com.bsu.prog.balls.balls;

import javafx.application.Platform;
import javafx.geometry.Point2D;

import java.util.concurrent.CountDownLatch;

public class BallsRender implements Runnable {
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private  Ball ball;
    private  Point2D x1y1,x2y2;
    CountDownLatch doneLatch;

    public  BallsRender(Ball _ball, Point2D _x1y1, Point2D _x2y2, CountDownLatch _doneLatch){
        ball=_ball;
        x1y1=_x1y1;
        x2y2=_x2y2;
        doneLatch= _doneLatch;
    }
    @Override
    public void run() {
        var pos=new Point2D(0,0);
        while (running && pos!=Ball.STOP) {
            synchronized (pauseLock) {
                if (!running) { // may have changed while waiting to
                    // synchronize on pauseLock
                    break;
                }
                if (paused) {
                    try {
                        pauseLock.wait(); // will cause this Thread to block until
                        // another thread calls pauseLock.notifyAll()
                        // Note that calling wait() will
                        // relinquish the synchronized lock that this
                        // thread holds on pauseLock so another thread
                        // can acquire the lock to call notifyAll()
                        // (link with explanation below this code)
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) { // running might have changed since we paused
                        break;
                    }
                }
            }
            pos=ball.calcNewPos(x1y1,x2y2);
            Platform.runLater(() -> {
                ball.circle.setCenterX(ball.coord.getX());
                ball.circle.setCenterY(ball.coord.getY());
            });
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        doneLatch.countDown();
    }

    public void stop() {
        running = false;
        // you might also want to interrupt() the Thread that is
        // running this Runnable, too, or perhaps call:
        resume();
        // to unblock
    }

    public void pause() {
        // you may want to throw an IllegalStateException if !running
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
        }
    }
};