import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    private Socket socket;
    private String address;
    private int port;
    private String message;
    private PrintWriter writer;
    private BufferedReader reader;

    public TCPClient(String address, int port, String message) {
        try {
            this.socket = new Socket(InetAddress.getByName(address), port);
            this.port = port;
            this.message = message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage()
    {
        try {
            this.writer = new PrintWriter(this.socket.getOutputStream(), true);
            this.writer.println(this.message);
            // this.writer.close(); // nicht schließen, weil der Socket sonst geschlossen wird
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String receiveMessage()
    {
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String response = this.reader.readLine();
            this.reader.close();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeSocket()
    {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
