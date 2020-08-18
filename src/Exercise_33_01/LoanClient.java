package Exercise_33_01;

import javafx.application.Application;
import javafx.geometry.Insets;
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

public class LoanClient extends Application {
    // IO Streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;

    @Override
    public void start(Stage stage) throws Exception {
        Label lblInterestRate = new Label("Annual Interest Rate");
        Label lblYears = new Label("Number Of Years");
        Label lblAmount = new Label("Loan Amount");

        TextField tfInterestRate = new TextField();
        tfInterestRate.setAlignment(Pos.CENTER_RIGHT);
        TextField tfYears = new TextField();
        tfYears.setAlignment(Pos.CENTER_RIGHT);
        TextField tfAmount = new TextField();
        tfAmount.setAlignment(Pos.CENTER_RIGHT);

        Button btnSubmit = new Button("Submit");

        GridPane gridPane = new GridPane();
        gridPane.add(lblInterestRate, 0, 0);
        gridPane.add(lblYears, 0, 1);
        gridPane.add(lblAmount, 0, 2);
        gridPane.add(tfInterestRate, 1, 0);
        gridPane.add(tfYears, 1, 1);
        gridPane.add(tfAmount, 1, 2);
        gridPane.add(btnSubmit, 2, 1);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        BorderPane mainPane = new BorderPane();
        TextArea ta = new TextArea();
        ta.setPrefHeight(400);
        mainPane.setCenter(new ScrollPane(ta));
        mainPane.setTop(gridPane);

        Scene scene = new Scene(mainPane, 450, 300);
        stage.setTitle("Loan Client");
        stage.setScene(scene);
        stage.show();

        btnSubmit.setOnAction(e -> {
            try {
                double annualInterestRate = Double.parseDouble(tfInterestRate.getText().trim());
                double numberOfYears = Double.parseDouble(tfYears.getText().trim());
                double loanAmount = Double.parseDouble(tfAmount.getText().trim());
                toServer.writeDouble(annualInterestRate);
                toServer.writeDouble(numberOfYears);
                toServer.writeDouble(loanAmount);
                toServer.flush();

                double monthlyPayment = fromServer.readDouble();
                double totalPayment = fromServer.readDouble();

                ta.appendText("Annual Interest Rate: " + annualInterestRate + "\n");
                ta.appendText("Number Of Years: " + numberOfYears + "\n");
                ta.appendText("Loan Amount: " + loanAmount + "\n");
                ta.appendText("Monthly Payment: " + monthlyPayment + "\n");
                ta.appendText("Total Payment: " + totalPayment + "\n");
            } catch (IOException ex) {
                System.err.println(ex);
            }
        });

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 8002);

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
