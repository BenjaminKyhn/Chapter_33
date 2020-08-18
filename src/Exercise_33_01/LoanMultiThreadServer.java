package Exercise_33_01;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class LoanMultiThreadServer extends Application {
    private TextArea ta = new TextArea();

    private int clientNo = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        stage.setTitle("Loan MultiThreadServer");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8002);
                ta.appendText("MultiThreadServer started at " + new Date() + "\n");

                while (true){
                    Socket socket = serverSocket.accept();
                    clientNo++;

                    Platform.runLater(() -> {
                        ta.appendText("Starting thread for client: " + clientNo + " at " + new Date() + "\n");

                        InetAddress inetAddress = socket.getInetAddress();
                        ta.appendText("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                        ta.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                    });

                    new Thread(new HandleAClient(socket)).start();
                }
            }
            catch (IOException ex){
                System.err.println(ex);
            }
        }).start();
    }

    class HandleAClient implements Runnable{
        private Socket socket;

        public HandleAClient(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                while (true){
                    double annualInterestRate = inputFromClient.readDouble();
                    double numberOfYears = inputFromClient.readDouble();
                    double loanAmount = inputFromClient.readDouble();

                    double monthlyInterestRate = annualInterestRate / 12;
                    double payments = numberOfYears * 12;
                    double monthlyPayment = (loanAmount * monthlyInterestRate / 100) / (1 - Math.pow((1 + monthlyInterestRate / 100), -payments));
                    double totalPayment = monthlyPayment * 12 * numberOfYears;

                    outputToClient.writeDouble(monthlyPayment);
                    outputToClient.writeDouble(totalPayment);

                    Platform.runLater(() -> {
                        ta.appendText("Annual Interest Rate: " + annualInterestRate + "\n");
                        ta.appendText("Number Of Years: " + numberOfYears + "\n");
                        ta.appendText("Loan Amount: " + loanAmount + "\n");
                        ta.appendText("Monthly Payment: " + monthlyPayment + "\n");
                        ta.appendText("Total Payment: " + totalPayment + "\n");
                    });
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}

