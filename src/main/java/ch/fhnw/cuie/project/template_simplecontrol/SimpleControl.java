package ch.fhnw.cuie.project.template_simplecontrol;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.util.Duration;

import java.util.List;

public class SimpleControl extends Region {
    // needed for StyleableProperties
    private static final StyleablePropertyFactory<SimpleControl> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    private static final double ARTBOARD_WIDTH  = 100;
    private static final double ARTBOARD_HEIGHT = 100;

    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;

    private static final double MINIMUM_WIDTH  = 25;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;

    private static final double MAXIMUM_WIDTH = 800;


    // Todo: declare all parts
    private Text   display;
    private Region battery;
    private Region electro;
    private Region shiny;
    private Region container;
    private Line distanceLine;
    private Line distanceNumberLine;

    private double newWidth;

    //Transitions
    ResizeWidthTranslation regionTransition;
    ParallelTransition turnOn;
    FadeTransition fadeTransition;


    // Todo: declare all Properties
    private final DoubleProperty value = new SimpleDoubleProperty();
    private final DoubleProperty maxValue = new SimpleDoubleProperty();
    private StringProperty distanceValue = new SimpleStringProperty();


    //Todo: declare all CSS stylable properties
    private static final CssMetaData<SimpleControl, Color> BASE_COLOR_META_DATA = FACTORY.createColorCssMetaData("-base-color", s -> s.baseColor);

    private final StyleableObjectProperty<Color> baseColor = new SimpleStyleableObjectProperty<Color>(BASE_COLOR_META_DATA, this, "baseColor") {
        @Override
        protected void invalidated() {
            setStyle(getCssMetaData().getProperty() + ": " + colorToCss(getBaseColor()) + ";");
            applyCss();
        }
    };

    // needed for resizing
    private Pane drawingPane;

    public SimpleControl() {
        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupValueChangeListeners();
        setupBinding();
    }

    private void initializeSelf() {
        // load stylesheets
        String fonts = getClass().getResource("fonts.css").toExternalForm();
        getStylesheets().add(fonts);

        String stylesheet = getClass().getResource("style.css").toExternalForm();
        getStylesheets().add(stylesheet);

        getStyleClass().add("simpleControl");
    }

    private void initializeParts() {

        //how much of the container should be filled in relation to the new value selected
         newWidth = ARTBOARD_WIDTH*calculateThePercentage(maxValue,value);

         //the display of the distance in text
         display = new Text();
         display.setVisible(false);


        // always needed
        drawingPane = new Pane();
        drawingPane.getStyleClass().add("drawingPane");
        drawingPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);

        //the actual size of the selected car battery
        battery = new Region();
        battery.getStyleClass().addAll("battery");

        //the white container with the maximum value possible for a car's battery
        container = new Region();
        container.getStyleClass().addAll("container");

        //the electricity sign
        electro = new Region();
        electro.getStyleClass().addAll("electro");

        //the shiny part in the battery, just as an effect
        shiny = new Region();
        shiny.getStyleClass().addAll("shiny");

        //the horizontal line under the battery
        distanceLine = new Line();
        distanceNumberLine = new Line();

        //the vertical line under the battery
        distanceNumberLine.setVisible(false);
        distanceLine.setVisible(false);

        drawingPane.getChildren().addAll(container,battery,electro,distanceLine,distanceNumberLine,display);
        getChildren().add(drawingPane);
    }

    //this calculates how much percent of the container should be filled, where max is sent from the DemoPane clasee
    public double calculateThePercentage(DoubleProperty max, DoubleProperty carDistance){
        double percentage = carDistance.getValue()/max.getValue();
        return percentage;
    }

    private void layoutParts() {
        battery.setPrefHeight(50);
        battery.setPrefWidth(0.0);
        battery.setLayoutY(ARTBOARD_HEIGHT*0.25);
        battery.setLayoutX(1.0);

        container.setPrefHeight(50);
        container.setPrefWidth(ARTBOARD_WIDTH);
        container.setLayoutY(ARTBOARD_HEIGHT*0.25);
        container.setLayoutX(1.0);

        electro.setLayoutY(battery.getLayoutY()+(battery.getPrefHeight()*0.4));
        electro.setPrefWidth(newWidth*0.5);
        electro.setPrefHeight(electro.getPrefWidth()*0.25);
        electro.setLayoutX(battery.getLayoutX()+(newWidth/4));

        shiny.setPrefHeight(5);
        shiny.setPrefWidth(ARTBOARD_WIDTH*0.8);
        shiny.setLayoutY(ARTBOARD_HEIGHT*0.28);
        shiny.setLayoutX(ARTBOARD_WIDTH/28);
        shiny.setVisible(false);

    }

    private void setupEventHandlers() {
        //todo

    }

    public void initilizeTransitions(){

        //the fade transition of the electricity sign
        fadeTransition = new FadeTransition(Duration.millis(1000), electro);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setAutoReverse(false);

        //battery color change, showing the electricity sign and drawing the lines under the battery and the text
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(1000));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                Color vColor = new Color(0, 1, 0, frac);
                battery.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
                electro.setVisible(true);
                electro.setBackground(new Background(new BackgroundFill(Color.web("#FFFF00"), CornerRadii.EMPTY, Insets.EMPTY)));

                distanceLine.setStartX( container.getLayoutX());
                distanceLine.setStartY(container.getLayoutY()+container.getPrefHeight()+10);
                distanceLine.setEndX(container.getLayoutX()+battery.getWidth());
                distanceLine.setEndY(container.getLayoutY()+container.getPrefHeight()+10);
                distanceLine.setVisible(true);

                distanceNumberLine.setStartX(container.getLayoutX()+battery.getWidth());
                distanceNumberLine.setStartY(container.getLayoutY()+container.getPrefHeight()+5);
                distanceNumberLine.setEndX(container.getLayoutX()+battery.getWidth());
                distanceNumberLine.setEndY(container.getLayoutY()+container.getPrefHeight()+15);
                distanceNumberLine.setVisible(true);

                display.setLayoutX(distanceLine.getLayoutX()+((distanceLine.getEndX()-distanceLine.getStartX())*0.25));
                display.setLayoutY(container.getLayoutY()+container.getPrefHeight()+23);
                display.setFont(new Font(10));
                display.setText(distanceValue.getValue()+" Km");
                display.setVisible(true);

            }
        };

        //this transition controls both the mentioned above transitions
        turnOn = new ParallelTransition();
        turnOn.getChildren().addAll(animation,fadeTransition);

        //the customized transition to change the width of the battery selected in relation to the container
        regionTransition = new ResizeWidthTranslation(Duration.millis(1500), battery, newWidth );
        regionTransition.play();

        //after the width of the new battery is set, the two above mentioned transitions should play
        regionTransition.setOnFinished(event -> {
            turnOn.play();
        });
    }


    private void setupValueChangeListeners() {
        valueProperty().addListener((observable, oldValue, newValue) ->{
            if(oldValue != newValue) {

                //if the value of the battery (capacity) changed, set the battery color to grey and remove the text and the lines
                battery.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
                electro.setVisible(false);
                distanceLine.setVisible(false);
                distanceNumberLine.setVisible(false);
                display.setVisible(false);

            }
            //calculate the corresponding new percentage of the space to be filled
            newWidth = ARTBOARD_WIDTH * calculateThePercentage(maxValue, new SimpleDoubleProperty(newValue.doubleValue()));
            layoutParts();
            initilizeTransitions();

        });
    }

    private void setupBinding() {
        //the distance value is a holder property to have the selected battery capacity, then linked to the display text
        //and another string "Km"
        distanceValue.bind(valueProperty().asString("%.0f"));
    }



    //resize by scaling
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    private void resize() {
        Insets padding         = getPadding();
        double availableWidth  = getWidth() - padding.getLeft() - padding.getRight();
        double availableHeight = getHeight() - padding.getTop() - padding.getBottom();

        double width = Math.max(Math.min(Math.min(availableWidth, availableHeight * ASPECT_RATIO), MAXIMUM_WIDTH), MINIMUM_WIDTH);

        double scalingFactor = width / ARTBOARD_WIDTH;

        if (availableWidth > 0 && availableHeight > 0) {
            relocateCentered();
            drawingPane.setScaleX(scalingFactor);
            drawingPane.setScaleY(scalingFactor);
        }
    }

    private void relocateCentered() {
        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, (getHeight() - ARTBOARD_HEIGHT) * 0.5);
    }

    private void relocateCenterBottom(double scaleY, double paddingBottom) {
        double visualHeight = ARTBOARD_HEIGHT * scaleY;
        double visualSpace  = getHeight() - visualHeight;
        double y            = visualSpace + (visualHeight - ARTBOARD_HEIGHT) * 0.5 - paddingBottom;

        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, y);
    }

    private void relocateCenterTop(double scaleY, double paddingTop) {
        double visualHeight = ARTBOARD_HEIGHT * scaleY;
        double y            = (visualHeight - ARTBOARD_HEIGHT) * 0.5 + paddingTop;

        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, y);
    }

    // some handy functions

    private double percentageToValue(double percentage, double minValue, double maxValue){
        return ((maxValue - minValue) * percentage) + minValue;
    }

    private double valueToPercentage(double value, double minValue, double maxValue) {
        return (value - minValue) / (maxValue - minValue);
    }

    private double valueToAngle(double value, double minValue, double maxValue) {
        return percentageToAngle(valueToPercentage(value, minValue, maxValue));
    }

    private double mousePositionToValue(double mouseX, double mouseY, double cx, double cy, double minValue, double maxValue){
        double percentage = angleToPercentage(angle(cx, cy, mouseX, mouseY));

        return percentageToValue(percentage, minValue, maxValue);
    }

    private double angleToPercentage(double angle){
        return angle / 360.0;
    }

    private double percentageToAngle(double percentage){
        return 360.0 * percentage;
    }

    private double angle(double cx, double cy, double x, double y) {
        double deltaX = x - cx;
        double deltaY = y - cy;
        double radius = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx     = deltaX / radius;
        double ny     = deltaY / radius;
        double theta  = Math.toRadians(90) + Math.atan2(ny, nx);

        return Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
    }

    private Point2D pointOnCircle(double cX, double cY, double radius, double angle) {
        return new Point2D(cX - (radius * Math.cos(Math.toRadians(angle - 90))),
                           cY + (radius * Math.sin(Math.toRadians(angle - 90))));
    }

    private Text createCenteredText(String styleClass) {
        return createCenteredText(ARTBOARD_WIDTH * 0.5, ARTBOARD_HEIGHT * 0.5, styleClass);
    }

    private Text createCenteredText(double cx, double cy, String styleClass) {
        Text text = new Text();
        text.getStyleClass().add(styleClass);
        text.setTextOrigin(VPos.CENTER);
        text.setTextAlignment(TextAlignment.CENTER);
        double width = cx > ARTBOARD_WIDTH * 0.5 ? ((ARTBOARD_WIDTH - cx) * 2.0) : cx * 2.0;
        text.setWrappingWidth(width);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setY(cy);
        text.setX(cx - (width / 2.0));

        return text;
    }

    private Group createTicks(double cx, double cy, int numberOfTicks, double overallAngle, double tickLength, double indent, double startingAngle, String styleClass) {
        Group group = new Group();

        double degreesBetweenTicks = overallAngle == 360 ?
                                     overallAngle /numberOfTicks :
                                     overallAngle /(numberOfTicks - 1);
        double outerRadius         = Math.min(cx, cy) - indent;
        double innerRadius         = Math.min(cx, cy) - indent - tickLength;

        for (int i = 0; i < numberOfTicks; i++) {
            double angle = 180 + startingAngle + i * degreesBetweenTicks;

            Point2D startPoint = pointOnCircle(cx, cy, outerRadius, angle);
            Point2D endPoint   = pointOnCircle(cx, cy, innerRadius, angle);

            Line tick = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
            tick.getStyleClass().add(styleClass);
            group.getChildren().add(tick);
        }

        return group;
    }

    private String colorToCss(final Color color) {
  		return color.toString().replace("0x", "#");
  	}


    // compute sizes

    @Override
    protected double computeMinWidth(double height) {
        Insets padding           = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return MINIMUM_WIDTH + horizontalPadding;
    }

    @Override
    protected double computeMinHeight(double width) {
        Insets padding         = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return MINIMUM_HEIGHT + verticalPadding;
    }

    @Override
    protected double computePrefWidth(double height) {
        Insets padding           = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return ARTBOARD_WIDTH + horizontalPadding;
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets padding         = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return ARTBOARD_HEIGHT + verticalPadding;
    }

    // all getter and setter

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public Color getBaseColor() {
        return baseColor.get();
    }

    public StyleableObjectProperty<Color> baseColorProperty() {
        return baseColor;
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor.set(baseColor);
    }

    public double getMaxValue() {
        return maxValue.get();
    }

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue.set(maxValue);
    }

    public String getDistanceValue() {
        return distanceValue.get();
    }

    public StringProperty distanceValueProperty() {
        return distanceValue;
    }

    public void setDistanceValue(String distanceValue) {
        this.distanceValue.set(distanceValue);
    }

}

class ResizeWidthTranslation extends Transition {

    protected Region region;
    protected Line line;
    protected double startWidth;
    protected double newWidth;
    protected double widthDiff;

    public ResizeWidthTranslation(Duration duration, Region region, double newWidth ) {
        setCycleDuration(duration);
        this.region = region;
        this.newWidth = newWidth;
        this.startWidth = region.getWidth();
        this.widthDiff = newWidth - startWidth;
    }

    @Override
    protected void interpolate(double fraction) {
        region.setMinWidth( startWidth + ( widthDiff * fraction ) );
    }
}
