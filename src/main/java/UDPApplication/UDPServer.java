package UDPApplication;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPServer {
    private DatagramSocket datagramSocket;
    private Scanner scanner = new Scanner(System.in);

    private boolean isWaitingNewDatagram = false;

    private byte[] buffer = new byte[256];

    private int sendTo;

    public UDPServer(int port, int sendTo){
        this.sendTo = sendTo;
        try {
            datagramSocket = new DatagramSocket(port);
        }catch (SocketException socketException){
            System.out.println("Не удалось создать сокет");
        }
    }

    public void start(){
        boolean isEnd = false;
        do {
            isEnd = manageActions();
        }while (!isEnd);
        stop();
    }


    private boolean manageActions(){
        if (!isWaitingNewDatagram){
            Thread thread = Thread.ofVirtual().start(()->{
                isWaitingNewDatagram = true;
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    datagramSocket.receive(packet);
                    System.out.println("Companion: " + new String(packet.getData()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                isWaitingNewDatagram = false;

            });
            try {thread.join();}
            catch (InterruptedException ignored){};

        }

        var message = scanner.nextLine();
        if (message != null){
            message = message;
            DatagramPacket packet;
            try {
                packet = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8),
                        0, message.length(), InetAddress.getByName("localhost"), sendTo);
                datagramSocket.send(packet);
            } catch (IOException ignored){}
            return "good bye".equals(message);
        }
        return false;
    }


    private void stop(){
        scanner.close();
        datagramSocket.close();
    }

    public static void main(String[] args) {
        Scanner scanner1 = new Scanner(System.in);
        var udpServer = new UDPServer(5000, 5001);
        udpServer.start();
    }

}
