import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TCPServer {
    private ServerSocket serverSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String message;
    private LocalTime startTime;
    private LocalTime endTime;

    public TCPServer(int port)
    {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Serververbindung steht auf Port: " + this.serverSocket.getLocalPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String formatTime(LocalTime time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return time.format(formatter);
    }

    public String getDuration() {
        Duration duration = Duration.between(this.startTime, this.endTime);
        long seconds = duration.getSeconds();

        return String.format("%d", seconds);
    }

    public synchronized void receiveMessage(Socket clientSocket)
    {
        try {

            if (this.startTime == null)
            {
                this.startTime = LocalTime.now();
            }
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.message = this.reader.readLine();

            long sleepTime = Long.parseLong(this.message);

            Thread.sleep(sleepTime);

            this.endTime = LocalTime.now();
            this.getDuration();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void sendMessage(Socket clientSocket)
    {
        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.writer.print("Der Server hat angefangen um: " + this.formatTime(this.startTime) + " | ");
            this.writer.print("Der Server hat aufgehört um: " + this.formatTime(this.endTime) + " | ");
            this.writer.println("Das Ganze hat " + this.getDuration() + " Sekunden gedauert.");
            this.writer.flush();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            while (true) {
                Socket clientSocket = this.serverSocket.accept();

                new Thread(() -> {
                    try {
                        this.receiveMessage(clientSocket);
                        this.sendMessage(clientSocket);
                    } catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            this.reader.close();
                            this.writer.close();
                            clientSocket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
