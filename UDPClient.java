import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    public static void main(String[] args) throws Exception {
        // Endereço IP do host remoto (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        
        // Canal de comunicação NÃO orientado à conexão.
        // clientSocket terá uma porta desiganada pelo SO
        DatagramSocket clientSocket = new DatagramSocket();

        // declaração e preenchimento do buffer de envio
        byte[] sendData = new byte[1024];
        sendData = "Sou um cliente".getBytes();

        // Criação do datagrama com endereço e porta do host remoto
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);

        System.out.println("mensagem enviada para o servidor");

        
        byte[] recBuffer = new byte[1024];
        // Resposta do servidor
        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);
        clientSocket.receive(recPkt); // Bloqueante

        String informacao = new String(recPkt.getData(), recPkt.getOffset(), recPkt.getLength());

        System.out.println("recebido do servidor: " + informacao);

        clientSocket.close();
    }
}