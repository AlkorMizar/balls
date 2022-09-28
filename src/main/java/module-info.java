module com.bsu.prog.balls.balls {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.bsu.prog.balls.balls to javafx.fxml;
    exports com.bsu.prog.balls.balls;
}