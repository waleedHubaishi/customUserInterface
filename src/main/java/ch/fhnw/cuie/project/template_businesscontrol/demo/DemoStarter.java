package ch.fhnw.cuie.project.template_businesscontrol.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class DemoStarter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        PresentationModel model = new PresentationModel();
        Region rootPanel = new DemoPane(model);

        Scene scene = new Scene(rootPanel, 900,900);

        primaryStage.setTitle("Business Control Demo");
        primaryStage.setScene(scene);

       primaryStage.setMaxHeight(540);
        primaryStage.setMinHeight(540);

       primaryStage.setMaxWidth(950);
        primaryStage.setMinWidth(950);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
