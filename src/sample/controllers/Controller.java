package sample.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
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

    private Point2D press = new Point2D.Double(0, 0);
    private boolean pressedBtn = false;
    private boolean pressedImage = false;
    private Rectangle rectangle = new Rectangle();
    private Rectangle r = new Rectangle();

    private double zoom = 1;
    private int xImg, yImg, wImg, hImg; // переменные в которых хранятся координаты части картинки которую над показывать.

    ImageHandler imageHandler = new ImageHandler();

    private static Image image1 = null;
    private static Image image2 = null;
    private FileChooser fileChooser = new FileChooser();

    @FXML
    void initialize() {
        fileChooser.setInitialDirectory(new File("."));

        r = new Rectangle(400, 400, wImg, hImg);
        r.setStroke(Color.rgb(0, 155, 100));
        r.setStrokeWidth(2);
        r.setFill(Color.rgb(255, 255, 255, 0.001));

        setMenus();
        setPaneEvents();

    }

    private void setMenus() {
        javafx.scene.control.Menu fileMenu = new javafx.scene.control.Menu("Файл");
        javafx.scene.control.Menu editMenu = new javafx.scene.control.Menu("Фильтры");

        // Create MenuItems
        javafx.scene.control.MenuItem openFileItem = new javafx.scene.control.MenuItem("Открыть файл");
        javafx.scene.control.MenuItem saveFileItem = new javafx.scene.control.MenuItem("Сохранить файл");
        javafx.scene.control.MenuItem exitItem = new javafx.scene.control.MenuItem("Выход");
        javafx.scene.control.MenuItem mirrorImageItem = new javafx.scene.control.MenuItem("Зеркальное отображение");
        javafx.scene.control.MenuItem putImageItem = new javafx.scene.control.MenuItem("Поместить");


        openFileItem.setOnAction(e -> {
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("TGA images (*.tga)", "*.tga"));
            File file = fileChooser.showOpenDialog(panelka.getScene().getWindow());
            if (file != null) {
                imageHandler.openFile(file.getPath());
                image1 = imageHandler.getImage();
                img1.setImage(SwingFXUtils.toFXImage((BufferedImage) image1, null));
                img1.setLayoutX(40);
                img1.setLayoutY(40);
                img1.setFitHeight(imageHandler.gethImg());
                img1.setFitWidth(imageHandler.getwImg());
            }
        });

        saveFileItem.setOnAction(e -> {
            if (image1 == null) {
                showAlertWithHeaderText();
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
                showAlertWithHeaderText();
            } else {
                imageHandler.MirrorFilter();
                image1 = imageHandler.getImage();
                img1.setFitWidth(image1.getWidth(null));
                img1.setFitHeight(image1.getHeight(null));
                img1.setImage(SwingFXUtils.toFXImage((BufferedImage) image1, null));
            }
        });

        //TODO realise putting
        putImageItem.setOnAction(e -> {

        });

        // Add menuItems to the Menus
        fileMenu.getItems().addAll(openFileItem, exitItem, saveFileItem);
        editMenu.getItems().addAll(mirrorImageItem);

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

                        if (checkImageClick((int) e.getX(), (int) e.getY(), 20, img1)) {
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
                            img2.setLayoutX(e.getX() - r.getWidth() / 2);
                            img2.setLayoutY(e.getY() - r.getHeight() / 2);
                        }
                    }
                }
        );
        panelka.addEventFilter(MouseEvent.MOUSE_PRESSED, e ->
        {
            if (checkImageClick((int) e.getX(), (int) e.getY(), 0, img2)) {
                pressedImage = true;

                r.setX(img2.getLayoutX());
                r.setY(img2.getLayoutY());
                r.setWidth(img2.getFitWidth());
                r.setHeight(img2.getFitHeight());
                if(!panelka.getChildren().contains(r)) {
                    panelka.getChildren().add(r);
                }

            } else {
                if (checkImageClick((int) e.getX(), (int) e.getY(), 20, img1)) {
                    pressedBtn = true;
                    press = new Point2D.Double(e.getX(), e.getY());
                    panelka.getChildren().remove(r);
                }
                else{
                    pressedImage = false;
                    panelka.getChildren().remove(r);
                }
            }
        });

        panelka.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            pressedImage = false;
            panelka.getChildren().remove(rectangle);

            //TODO realise mashtab
            if (pressedBtn) {
                if ((int) rectangle.getWidth() != 0 && (int) rectangle.getHeight() != 0) {
                    xImg = (int) rectangle.getX() - (int) img1.getLayoutX();// / zoom;
                    yImg = (int) rectangle.getY() - (int) img1.getLayoutY();// / zoom;

                    wImg = (int) rectangle.getWidth();
                    hImg = (int) rectangle.getHeight();

//                    if ((rectangle.getX() + rectangle.getWidth()) <= img1.getLayoutX() + img1.getFitWidth()) {
//                        wImg = (int) (rectangle.getWidth());// / zoom);
//                    } else {
//                        wImg = ((int) (img1.getLayoutX() + img1.getFitWidth()) - (int) rectangle.getX()); /// zoom);
//                    }
//                    if ((rectangle.getY() + rectangle.getHeight()) <= img1.getLayoutY() + img1.getFitHeight()) {
//                        hImg = (int) (rectangle.getHeight());// / zoom);
//                    } else {
//                        hImg = ((int) (img1.getLayoutY() + img1.getFitHeight()) - (int) rectangle.getY());// / zoom);
//                    }
                    //zoom = 1;

                    image2 = imageHandler.getCut(xImg, yImg, wImg, hImg);
                    img2.setLayoutX(xImg);
                    img2.setLayoutY(yImg);
                    img2.setFitWidth(wImg);
                    img2.setFitHeight(hImg);
                    img2.setImage(SwingFXUtils.toFXImage((BufferedImage)image2, null));

                    //img1.setImage(SwingFXUtils.toFXImage((BufferedImage)imageHandler.getImage(), null));


                    //panelka.getChildren().add(r);
                    pressedBtn = false;
                }


            }
        });

        panelka.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() < 0) {
                zoom += 0.1;
            } else {
                zoom -= 0.1;
            }
            if (img1 != null) {
                image1 = imageHandler.getImage();
                img1.setFitWidth(image1.getWidth(null) * zoom);
                img1.setFitHeight(image1.getHeight(null) * zoom);
                img1.setImage(SwingFXUtils.toFXImage((BufferedImage) image1, null));
            }
        });
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

    private void showAlertWithHeaderText() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Внимание:");
        alert.setContentText("Данное действие запрещено!");

        alert.showAndWait();
    }
}
