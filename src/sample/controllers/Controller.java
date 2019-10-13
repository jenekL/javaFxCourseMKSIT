package sample.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import sample.exceptions.OutOfBorderException;
import sample.imageHandler.ImageHandler;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;

public class Controller {
    @FXML
    private Pane panelka;

    @FXML
    private MenuBar mainMenu;

    @FXML
    private ImageView img1;

    @FXML
    private ImageView img2;

    @FXML
    private Label zoomlabel;


    private Point2D press = new Point2D.Double(0, 0);
    private boolean pressedBtn = false;
    private boolean pressedImage = false;
    private Rectangle rectangle = new Rectangle();
    private Rectangle r = new Rectangle();

    private int marginLeft = 40;
    private int marginTop = 40;
    private final int MAX_IMAGE_WIDTH = 1100;
    private final int MAX_IMAGE_HEIGHT = 500;
    private final double ZOOM_STEP = 0.05;

    private double zoom = 1;
    private int xImg, yImg, wImg, hImg; // переменные в которых хранятся координаты части картинки которую над показывать.

    private ImageHandler imageHandler = new ImageHandler();

    private static Image image1 = null;
    private static Image image2 = null;
    private FileChooser fileChooser = new FileChooser();

    @FXML
    void initialize() {
        fileChooser.setInitialDirectory(new File("."));

        r = new Rectangle(400, 400, wImg, hImg);
        r.setStroke(Color.rgb(255, 255, 0));
        r.setStrokeWidth(2);
        r.setFill(Color.rgb(255, 255, 255, 0.001));

        zoomlabel.setText("Zoom: " + (int)((zoom / 1) * 100) + "%");

        setMenus();
        setPaneEvents();
    }

    private void clearObjects() {
        panelka.getChildren().remove(img2);
        panelka.getChildren().remove(img1);
        img2.setImage(null);
        image1 = null;
        image2 = null;
        wImg = 0;
        hImg = 0;
        xImg = 0;
        yImg = 0;
        zoom = 1;
    }

    private void setMenus() {
        javafx.scene.control.Menu fileMenu = new javafx.scene.control.Menu("Файл");
        javafx.scene.control.Menu editMenu = new javafx.scene.control.Menu("Фильтры");

        // Create MenuItems
        javafx.scene.control.MenuItem openFileItem = new javafx.scene.control.MenuItem("Открыть файл");
        javafx.scene.control.MenuItem saveFileItem = new javafx.scene.control.MenuItem("Сохранить файл");
        javafx.scene.control.MenuItem exitItem = new javafx.scene.control.MenuItem("Выход");
        javafx.scene.control.MenuItem clearItem = new javafx.scene.control.MenuItem("Очистить");
        javafx.scene.control.MenuItem mirrorImageItem = new javafx.scene.control.MenuItem("Зеркальное отображение");
        javafx.scene.control.MenuItem putImageItem = new javafx.scene.control.MenuItem("Поместить");


        openFileItem.setOnAction(e -> {
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("TGA images (*.tga)", "*.tga"));
            File file = fileChooser.showOpenDialog(panelka.getScene().getWindow());
            if (file != null) {
                clearObjects();

                imageHandler.openFile(file.getPath());
                image1 = imageHandler.getImage();
                img1.setImage(SwingFXUtils.toFXImage((BufferedImage) image1, null));

//                int width, height;
//                if(imageHandler.getwImg() > MAX_IMAGE_WIDTH){
//                    width = MAX_IMAGE_WIDTH;
//                }
//                else{
//                    width = imageHandler.getwImg();
//                }
//                if(imageHandler.gethImg() > MAX_IMAGE_HEIGHT){
//                    height = MAX_IMAGE_HEIGHT;
//                }else{
//                    height = imageHandler.gethImg();
//                }

                marginLeft = (int) panelka.getScene().getWindow().getWidth() / 2 - imageHandler.getwImg() / 2;
                marginTop = (int) panelka.getScene().getWindow().getHeight() / 2 - imageHandler.gethImg() / 2;

                img1.setLayoutX(marginLeft);
                img1.setLayoutY(marginTop);

                img1.setFitHeight(imageHandler.gethImg());
                img1.setFitWidth(imageHandler.getwImg());
                panelka.getChildren().add(img1);
            }
        });

        saveFileItem.setOnAction(e -> {
            if (image1 == null) {
                showAlertWithHeaderText("Данное действие запрещено!");
            } else {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("BMP images (*.bmp)", "*.bmp"));

                File file = fileChooser.showSaveDialog(panelka.getScene().getWindow());

                if (file != null) {
                    imageHandler.saveFile(file.getPath());
                }
            }
        });

        mirrorImageItem.setOnAction(e -> {
            if (img1 == null) {
                showAlertWithHeaderText("Данное действие запрещено!");
            } else {
                imageHandler.MirrorFilter();
                image1 = imageHandler.getImage();
                img1.setFitWidth(image1.getWidth(null));
                img1.setFitHeight(image1.getHeight(null));
                img1.setImage(SwingFXUtils.toFXImage((BufferedImage) image1, null));
            }
        });

        clearItem.setOnAction(e -> clearObjects());

        putImageItem.setOnAction(e -> {

            int x, y, w = 0, h = 0;
            boolean changedW = false;
            boolean changedH = false;

            if (img2.getLayoutX() >= img1.getLayoutX()) {
                x = (int) img2.getLayoutX();
            } else {
                x = (int) img1.getLayoutX();
                w = (int) img2.getLayoutX() + (int) img2.getFitWidth() - (int) img1.getLayoutX();
                changedW = true;
            }
            if (img2.getLayoutY() >= img1.getLayoutY()) {
                y = (int) img2.getLayoutY();
            } else {
                y = (int) img1.getLayoutY();
                h = (int) img2.getLayoutY() + (int) img2.getFitHeight() - (int) img1.getLayoutY();
                changedH = true;
            }
            if (!changedW) {
                if (img2.getLayoutX() + img2.getFitWidth() <= img1.getLayoutX() + img1.getFitWidth()) {
                    w = (int) img2.getFitWidth();// / zoom);
                } else {
                    w = ((int) (img1.getLayoutX() + img1.getFitWidth()) - (int) img2.getLayoutX()); /// zoom);
                }
            }
            if (!changedH) {
                if (img2.getLayoutY() + img2.getFitHeight() <= img1.getLayoutY() + img1.getFitHeight()) {
                    h = (int) img2.getFitHeight();// / zoom);
                } else {
                    h = ((int) (img1.getLayoutY() + img1.getFitHeight()) - (int) img2.getLayoutY());// / zoom);
                }
            }

            imageHandler.placeImage((int)((x - marginLeft) / zoom), (int)((y - marginTop) / zoom), w, h);
            img1.setImage(SwingFXUtils.toFXImage((BufferedImage) imageHandler.getImage(), null));
        });

        // Add menuItems to the Menus
        fileMenu.getItems().addAll(openFileItem, saveFileItem, clearItem, exitItem);
        editMenu.getItems().addAll(mirrorImageItem, putImageItem);

        // Add Menus to the MenuBar
        mainMenu.getMenus().addAll(fileMenu, editMenu);
    }

    private Boolean checkImageClick(int x, int y, int mistake, ImageView image) {
        return x >= image.getLayoutX() - mistake
                && x <= image.getFitWidth() + image.getLayoutX() + mistake
                && y >= image.getLayoutY() - mistake
                && y <= image.getFitHeight() + image.getLayoutY() + mistake;
    }

    private void setPaneEvents() {
        panelka.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
                    if (image1 != null && !pressedImage) {
                        double x, y, w, h;

                        if (checkImageClick((int) e.getX(), (int) e.getY(), 0, img1)) {
                            if (e.getX() > (int) press.getX()) {
                                x = press.getX();
                                w = e.getX() - press.getX();
                            } else {
                                x = e.getX();
                                w = press.getX() - e.getX();
                            }

                            if (e.getY() > (int) press.getY()) {
                                y = press.getY();
                                h = e.getY() - press.getY();
                            } else {
                                y = e.getY();
                                h = press.getY() - e.getY();
                            }
                            setRect(new Rectangle(x, y, w, h));
                        }
                    }

                    if (image2 != null) {
                        if (pressedImage) {
                            r.setX(e.getX() - r.getWidth() / 2);
                            r.setY(e.getY() - r.getHeight() / 2);
                            img2.setLayoutX(e.getX() - img2.getFitWidth() / 2);
                            img2.setLayoutY(e.getY() - img2.getFitHeight() / 2);
                        }
                    }
                }
        );
        panelka.addEventFilter(MouseEvent.MOUSE_PRESSED, e ->
        {
            //pressed on cut image
            if (checkImageClick((int) e.getX(), (int) e.getY(), 0, img2)) {
                if(e.getClickCount() == 2){
                    try {
                        if(zoom != 1) {
                            image2 = imageHandler.getCut((int) ((r.getX()- (int) (marginLeft)) / zoom),
                                    (int) ((r.getY() - (marginTop)) / zoom),
                                    (int) (r.getWidth() / zoom), (int) (r.getHeight() / zoom));
                        }
                        else{
                            image2 = imageHandler.getCut((int) (r.getX() / zoom) - (int) (marginLeft), (int) (r.getY() / zoom) - (int) (marginTop),
                                    (int) (r.getWidth() / zoom), (int) (r.getHeight() / zoom));
                        }
                        img2.setImage(SwingFXUtils.toFXImage((BufferedImage) image2, null));
                    }
                    catch (Exception ex){
                        showAlertWithHeaderText("За границами нельзя");
                    }
                }
                pressedImage = true;
                setShinyRect();
            } else {
                //press on default image
                if (checkImageClick((int) e.getX(), (int) e.getY(), 0, img1)) {
                    pressedBtn = true;
                    press = new Point2D.Double(e.getX(), e.getY());
                    panelka.getChildren().remove(r);
                    pressedImage = false;
                }
                //not pressed anywhere
                else {
                    pressedImage = false;
                    panelka.getChildren().remove(r);
                }
            }
        });

        panelka.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            panelka.getChildren().remove(rectangle);

            if (pressedBtn) {
                if ((int) rectangle.getWidth() != 0 && (int) rectangle.getHeight() != 0) {
                    xImg = (int) (rectangle.getX() / zoom);
                    yImg = (int) (rectangle.getY() / zoom);

                    if ((rectangle.getX() + rectangle.getWidth()) <= img1.getLayoutX() + img1.getFitWidth()) {
                        wImg = (int) ((rectangle.getWidth()) / zoom);
                    } else {
                        wImg = (int) (((img1.getLayoutX() + img1.getFitWidth()) - (int) rectangle.getX()) / zoom);
                    }
                    if ((rectangle.getY() + rectangle.getHeight()) <= img1.getLayoutY() + img1.getFitHeight()) {
                        hImg = (int) (rectangle.getHeight() / zoom);
                    } else {
                        hImg = (int) (((img1.getLayoutY() + img1.getFitHeight()) - (int) rectangle.getY()) / zoom);
                    }

                    if(image1 != null) {
                        if (!panelka.getChildren().contains(img2)) {
                            panelka.getChildren().add(img2);
                        }
                        image2 = imageHandler.getCut(xImg - (int) (img1.getLayoutX() / zoom), yImg - (int) (img1.getLayoutY() / zoom),
                                wImg, hImg);
                        if(zoom == 1) {
                            img2.setLayoutX(xImg);
                            img2.setLayoutY(yImg);
                        }
                        else{
                            img2.setLayoutX(0);
                            img2.setLayoutY(100);
                        }
                        img2.setFitWidth(wImg);
                        img2.setFitHeight(hImg);
                        img2.setImage(SwingFXUtils.toFXImage((BufferedImage) image2, null));
                        setShinyRect();
                    }

                    pressedBtn = false;
                }


            }
        });

        panelka.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (!pressedImage) {
                //if(zoom - ZOOM_STEP * 2 > ZOOM_STEP) {
                    if (e.getDeltaY() < 0) {
                        zoom += ZOOM_STEP;
                    } else {
                        zoom -= ZOOM_STEP;
                    }
                    if(zoom <= 0){
                        zoom += ZOOM_STEP;
                    }
                    if (img1 != null) {
                        zoomlabel.setText("Zoom: " + (int)((zoom / 1) * 100) + "%");
                        //image1 = imageHandler.getImage();
                        //image1 = imageHandler.resize(zoom);
                        img1.setFitWidth(image1.getWidth(null) * zoom);
                        img1.setFitHeight(image1.getHeight(null) * zoom);
                        //img1.setImage(SwingFXUtils.toFXImage((BufferedImage) image1, null));
                    }
               // }
            }
        });
    }

    private void setShinyRect() {
        r.setX(img2.getLayoutX());
        r.setY(img2.getLayoutY());
        r.setWidth(img2.getFitWidth());
        r.setHeight(img2.getFitHeight());
        if (!panelka.getChildren().contains(r)) {
            panelka.getChildren().add(r);
        }
    }

    public void draw() {
        if (pressedBtn) {
            panelka.getChildren().add(rectangle);
        }
    }

    public void setRect(Rectangle rect) {
        panelka.getChildren().remove(rectangle);
        this.rectangle = rect;
        rectangle.setStroke(Color.rgb(0, 0, 0));
        rectangle.setStrokeWidth(2);
        rectangle.setFill(Color.rgb(255, 255, 255, 0.001));
        draw();
    }

    private void showAlertWithHeaderText(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Внимание:");
        alert.setContentText(text);

        alert.showAndWait();
    }
}
