package com.company;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Scanner;

public class Main {

    private static final String TERMINATE = "/quit";
    private static final int SOCKET_TTL = 0;
    private static final int PORT_NUMBER = 31337;
    private static final String BROADCAST_ADDR = "239.0.0.0";
    static String name;
    static volatile boolean finished = false;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(BROADCAST_ADDR);
            int port = PORT_NUMBER;
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter username: ");
            name = sc.nextLine();
            MulticastSocket socket = new MulticastSocket(port);

            socket.setTimeToLive(SOCKET_TTL);

            socket.joinGroup(group);
            Thread t = new Thread(new ReadThread(socket, group, port));

            t.start();

            System.out.println("Ready for messages:\n");
            while (true) {
                String message;
                message = sc.nextLine();
                if (message.equalsIgnoreCase(Main.TERMINATE)) {
                    finished = true;
                    socket.leaveGroup(group);
                    socket.close();
                    break;
                }
                message = name + ": " + message;
                byte[] buffer = message.getBytes();
                System.err.println(buffer.length);
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
                socket.send(datagram);
            }
        } catch (Exception e) {
            System.err.println("Error creating socket.");
            e.printStackTrace();
        }
    }
}
