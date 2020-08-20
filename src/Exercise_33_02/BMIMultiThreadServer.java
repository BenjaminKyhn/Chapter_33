package Exercise_33_02;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class BMIMultiThreadServer extends Application {
    private TextArea ta = new TextArea();

    private int clientNo = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        stage.setTitle("BMI MultiThreadServer");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8006);
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
                    double height = inputFromClient.readDouble();
                    double weight = inputFromClient.readDouble();

                    double bmi = weight / (Math.pow(height, 2));

                    outputToClient.writeDouble(bmi);

                    Platform.runLater(() -> {
                        ta.appendText("Height: " + height + " m\n");
                        ta.appendText("Weight: " + weight + " kg\n");
                        ta.appendText("BMI: " + bmi + "\n");
                    });
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}

