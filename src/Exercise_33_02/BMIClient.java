package Exercise_33_02;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BMIClient extends Application {
    // IO Streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;

    @Override
    public void start(Stage stage) throws Exception {
        Label lblHeight = new Label("Height in m");
        Label lblWeight = new Label("Weight in kg");

        TextField tfHeight = new TextField();
        tfHeight.setAlignment(Pos.CENTER_RIGHT);
        TextField tfWeight = new TextField();
        tfWeight.setAlignment(Pos.CENTER_RIGHT);

        Button btnSubmit = new Button("Submit");

        GridPane gridPane = new GridPane();
        gridPane.add(lblHeight, 0, 0);
        gridPane.add(lblWeight, 0, 1);
        gridPane.add(tfHeight, 1, 0);
        gridPane.add(tfWeight, 1, 1);
        gridPane.add(btnSubmit, 2, 1);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        BorderPane mainPane = new BorderPane();
        TextArea ta = new TextArea();
        ta.setPrefHeight(400);
        mainPane.setCenter(new ScrollPane(ta));
        mainPane.setTop(gridPane);

        Scene scene = new Scene(mainPane, 450, 300);
        stage.setTitle("BMI Client");
        stage.setScene(scene);
        stage.show();

        btnSubmit.setOnAction(e -> {
            try {
                double height = Double.parseDouble(tfHeight.getText().trim());
                double weight = Double.parseDouble(tfWeight.getText().trim());
                toServer.writeDouble(height);
                toServer.writeDouble(weight);
                toServer.flush();

                double bmi = fromServer.readDouble();

                ta.appendText("Height: " + height + " m\n");
                ta.appendText("Weight: " + weight + " kg\n");
                ta.appendText("BMI: " + bmi + "\n");
            } catch (IOException ex) {
                System.err.println(ex);
            }
        });

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 8006);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException ex){
            ta.appendText(ex.toString() + "\n");
        }
    }
}
