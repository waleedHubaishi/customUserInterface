package ch.fhnw.cuie.project.template_simplecontrol.demo;

import ch.fhnw.cuie.project.template_simplecontrol.SimpleControl;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

class DemoPane extends BorderPane {

    private final PresentationModel pm;

    // declare the custom control
    private SimpleControl cc;

    // all controls
    private Slider slider;

    DemoPane(PresentationModel pm) {
        this.pm = pm;
        initializeControls();
        layoutControls();
        setupBindings();
    }

    private void initializeControls() {

        setPadding(new Insets(10));
        slider = new Slider();

        //the maximum value possible of a battery should be set here
        slider.setMax(700);
        slider.setShowTickLabels(false);

        cc = new SimpleControl();

    }

    private void layoutControls() {
        VBox controlPane = new VBox(new Label("SimpleControl Properties"),
                                    slider, new Label("please wait till the animation ends before selecting a new Value"));
        controlPane.setPadding(new Insets(0, 50, 0, 50));
        controlPane.setSpacing(10);

        setCenter(cc);
        setRight(controlPane);
    }

    private void setupBindings() {
        slider.valueProperty().bindBidirectional(pm.pmValueProperty());

        cc.valueProperty().bindBidirectional(pm.pmValueProperty());
        cc.baseColorProperty().bindBidirectional(pm.baseColorProperty());

        //bind the maximum value in the slider to the maxValue in the simpleControl
        cc.maxValueProperty().bind(slider.maxProperty());
    }

}

