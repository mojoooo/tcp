public class Main {
    public static void main(String[] args) {
        TCPServer server = new TCPServer(9876);
        new Thread(() -> server.run()).start();

        for (int i = 1; i <= 3; i++) {
            final int index = i;
            new Thread(() -> {
                TCPClient client = new TCPClient("localhost", 9876,  "3000");
                client.sendMessage();
                System.out.println(client.receiveMessage());
                client.closeSocket();
            }).start();
        }
    }
}