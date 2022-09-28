package com.bsu.prog.balls.balls;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class HelloController {

    private static final int BALLS_COUNT = 20;
    public Button startBtn;
    public Button pauseBtn;
    public Button stopBtn;
    public Pane board;

    private  double height ;
    private  double width ;

    private  Point2D x2y2;
    private  Point2D x1y1;

    private List<BallsRender> WorkingThreads =new LinkedList<>();
    private CountDownLatch doneLatch, alertLch;
    private  Thread awitAllThr;


    public void onStart(ActionEvent actionEvent) throws InterruptedException {
        board.getChildren().clear();
        if (awitAllThr !=null && awitAllThr.isAlive()){
            awitAllThr.interrupt();
            stop();
        }

        height = board.getHeight();
        width = board.getWidth();

        x2y2 = new Point2D(width,height);
        x1y1 = new Point2D(0,0);
        doneLatch =new CountDownLatch(BALLS_COUNT);
        alertLch =new CountDownLatch(1);
        var awitAll = new Task<>() {
            @Override
            protected Void call() throws Exception {
                    doneLatch.await(); // better with time out (delete later comment)
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "All threads have stopped");

                        alert.showAndWait()
                                .filter(response -> response == ButtonType.OK)
                                .ifPresent(response -> {
                                    board.getChildren().clear();
                                    alertLch.countDown();
                                    awitAllThr = null;
                                }); //removes dots after pressing button
                    });
                return null;
            }
        };
        awitAllThr = new Thread(awitAll);
        awitAllThr.setDaemon(true);
        awitAllThr.start();


        for (int i = 0; i < BALLS_COUNT; i++) {
           WorkingThreads.add(drawBall());
        }
    }

    private BallsRender drawBall(){
        Circle circle=new Circle(5,5,4);
        circle.setFill(Color.color(Math.random(), Math.random(), Math.random()));
        board.getChildren().add(circle);
        var ball =randomBall(circle);
        var renderer = new BallsRender(ball,x1y1,x2y2,doneLatch);
        Thread th = new Thread(renderer);
        th.setDaemon(true);
        th.start();
        return  renderer;
    }
    private  Ball randomBall(Circle circle){
        Random rand =new Random();
        var coord=new Point2D(rand.nextDouble()*height,rand.nextDouble()*width);
        var speed=new Point2D(rand.nextDouble()*height*0.005,rand.nextDouble()*width*0.005);
        return  new Ball(coord,speed,0.001,circle);
    }

    public void onPause(ActionEvent actionEvent) {
        for (var render: WorkingThreads) {
            render.pause();
        }
    }

    public void onStop(ActionEvent actionEvent) throws InterruptedException {
        stop();
    }

    private void  stop() throws InterruptedException {
        for (var render: WorkingThreads) {
            render.stop();
        }
        WorkingThreads.clear();
        awitAllThr.join();
    }

    public void onResume(ActionEvent actionEvent) {
        for (var render: WorkingThreads) {
            render.resume();
        }
    }
}