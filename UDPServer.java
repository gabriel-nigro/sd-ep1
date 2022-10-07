import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) throws Exception {
        DatagramSocket serveSocket = new DatagramSocket(9876);

        while (true) {
            try {
            // Declaração do buffer de recebimento
            byte[] recBuffer = new byte[1024];

            // Criação do datagrama a ser recebido
            DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);
            System.out.println( "Esperando alguma mensagem");
            // Recebimento do datagrama do host remoto (método bloqueante)
            serveSocket.receive(recPkt);

            ByteArrayInputStream in = new ByteArrayInputStream(recPkt.getData());
            ObjectInputStream is = new ObjectInputStream(in);
            System.out.println(is.readObject().getClass());
            
            System.out.println( "Recebi a mensagem!");

            /*byte[] sendBuf = new byte[1024];
            sendBuf =  "sou o servidor".getBytes();

            InetAddress IPAddress = recPkt.getAddress();
            int port =recPkt.getPort();

            DatagramPacket sendPkt = new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);
            serveSocket.send(sendPkt);
            System.out.println( "Enviando mensagem");
            */
            } catch (Exception e) {
                serveSocket.close();
            }
            
        }
     }
}
