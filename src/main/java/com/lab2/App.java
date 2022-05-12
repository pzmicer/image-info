package com.lab2;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import java.nio.file.*;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private List<Path> images;

    @Override
    public void start(Stage stage) throws IOException {
        // scene = new Scene(loadFXML("primary"), 640, 480);
        // stage.setScene(scene);
        // stage.show();

        stage.setTitle("Image info");

        DirectoryChooser chooser = new DirectoryChooser();

        Button button = new Button();
        button.setText("Browse directory");
        button.setPrefWidth(200);
        button.setPrefHeight(200);
        button.setOnAction(e -> {
            File selected = chooser.showDialog(stage);
            // File selected = new File("c:\\Projects\\BSU Projects\\Computer Graphics\\image-info");
            //File selected = new File("c:\\Projects\\BSU Projects\\Computer Graphics\\image-info\\Для проверки Lab2");
            if (selected != null) {
                if (selected.isDirectory()) {
                    try {
                        images = Files.list(Paths.get(selected.getPath())).collect(Collectors.toList());

                        stage.setScene(getSecondScene());
                    } catch (IOException e1) {
                        System.out.println(e1.getMessage());
                    }
                }
                else {
                    
                }
            }
        });

        VBox vBox = new VBox(button);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 500, 500);
        
        stage.setScene(scene);
        stage.show();
    }

    Scene getSecondScene() {
        ObservableList<String> imagesStrings = FXCollections.observableArrayList();
        for (Path image : images) {
            imagesStrings.add(image.getFileName().toString());
        }
        ListView<String> listView = new ListView<String>(imagesStrings);
        listView.setPrefWidth(300);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        Label size = new Label();
        Label colorType = new Label();
        Label bpp = new Label();
        Label compression = new Label();
        
        listView.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            File image = images.get(newValue.intValue()).toFile();
            
            try {
                Image img = new Image(image.toURI().toString());
                imageView.setImage(img);

                ImageInfo info = Imaging.getImageInfo(image);
                size.setText(String.format("Size: (%s, %s)%n", info.getWidth(), info.getHeight()));
                colorType.setText(String.format("Color Type: %s%n", info.getColorType()));
                bpp.setText(String.format("Bits per Pixel: %s%n", info.getBitsPerPixel()));
                bpp.setText(String.format("Dots per inch (DPI): (%s, %s)%n", info.getPhysicalHeightDpi(), info.getPhysicalWidthDpi()));
                compression.setText(String.format("Compression: %s %n%n", info.getCompressionAlgorithm()));

                System.out.printf("Size: (%s, %s)%n", info.getWidth(), info.getHeight());
                System.out.printf("Color Type: %s%n", info.getColorType());
                System.out.printf("Bits per Pixel: %s%n", info.getBitsPerPixel());
                System.out.printf("Dots per inch (DPI): (%s, %s)%n", info.getPhysicalHeightDpi(), info.getPhysicalWidthDpi());
                System.out.printf("Compression: %s %n%n", info.getCompressionAlgorithm());
            } catch (ImageReadException | IOException e) {
                //e.printStackTrace();
                System.out.println(e.getMessage());
                
                size.setText("");
                colorType.setText("");
                bpp.setText("");
                compression.setText("");

                Alert alert = new Alert(AlertType.ERROR, "Not an image!");
                alert.showAndWait();
            }
        });

        VBox vBox1 = new VBox(listView);
        vBox1.setAlignment(Pos.CENTER);
        
        VBox vBox2 = new VBox(imageView, size, colorType, bpp, compression);
        vBox2.setAlignment(Pos.CENTER);
        //vBox2.setPadding(new Insets(0, 0, 0, 50));

        HBox hBox = new HBox(vBox1, vBox2);
        hBox.setSpacing(50);
        hBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(hBox, 1500, 700);
        return scene;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}