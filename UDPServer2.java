import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

// Socket Exception
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

// Lib para leitura de arquivos
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

// https://stackoverflow.com/questions/40044453/java-peer-to-peer-using-udp-socket
public class UDPServer2 {
    public static boolean verificaArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + "/" + nomeArquivo);
        return arquivo.isFile() ? true : false;
    }

    public static File getArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + "/" + nomeArquivo);
        return arquivo;
    }

    static String getIp(String peerInfos) {
        String[] infosSplited = peerInfos.split(":");
        String ip = infosSplited[0];
        return ip;
    }

    static int getPorta(String peerInfos) {
        String[] infosSplited = peerInfos.split(":");
        int porta = Integer.parseInt(infosSplited[1]);
        return porta;
    }

    public static void iniciaSocket(int port, String diretorio, DatagramSocket clientSocket, String serverInfos) {
        (new Thread() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    // Cria o socket
                    socket = new DatagramSocket(port);

                } catch (SocketException ex) {
                    ex.printStackTrace();
                }
                // Declaração do buffer de recebimento
                byte[] recBuffer = new byte[1024];

                // Criação do datagrama a ser recebido
                DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);

                while (true) {
                    try {
                        socket.receive(recPkt);

                        // Transforma o pacote em uma instância da classe Mensagem.
                        ByteArrayInputStream in = new ByteArrayInputStream(recPkt.getData());
                        ObjectInputStream is = new ObjectInputStream(in);
                        Mensagem msg = (Mensagem) is.readObject();
                        if (msg.getIsResponse()) {
                            if (msg.getConteudoArquivo() != null) {
                                File novoArquivo = new File(diretorio + "/" + msg.getNomeArquivo());
                                Files.copy(msg.getConteudoArquivo().toPath(), novoArquivo.toPath());
                                System.out.println("peer com arquivo procurado: " + recPkt.getAddress() + msg.getNomeArquivo());
                            }
                        } else {
                            String arquivo = msg.getNomeArquivo();

                            if (verificaArquivo(diretorio, arquivo)) {
                                msg.setConteudoArquivo(getArquivo(diretorio, arquivo));
                                msg.setIsResponse(true);
                                msg.setPeerResponse(serverInfos);
                                System.out.println("tem o arquivo");
                                retornaMensagem(clientSocket, msg);
                            } else {
                                System.out.println("Não tem o arquivo");
                            }
                        }

                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                }
            }

        }).start();
    }

    public static void retornaMensagem(DatagramSocket clientSocket, Mensagem mensagem) throws IOException {
        (new Thread() {
            @Override
            public void run() {
                try {
                    // declaração e preenchimento do buffer de envio
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(mensagem);
                    final byte[] sendMessage = baos.toByteArray();

                    String ipDestino = getIp(mensagem.getSenderInfos());
                    int portaDestino = getPorta(mensagem.getSenderInfos());

                    // Criação do datagrama com endereço e porta do host remoto
                    DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length,
                            InetAddress.getByName(ipDestino), portaDestino);

                    clientSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }

    public static void enviaMensagem(DatagramSocket clientSocket, String serverInfos, String arquivoBuscado,
            String ipDestino, int portaDestino) {
        (new Thread() {
            @Override
            public void run() {
                // Cria objeto de mensagem
                Mensagem mensagem = new Mensagem(serverInfos, arquivoBuscado, false);
                try {
                    // declaração e preenchimento do buffer de envio
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(mensagem);
                    final byte[] sendMessage = baos.toByteArray();

                    // Criação do datagrama com endereço e porta do host remoto
                    DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length,
                            InetAddress.getByName(ipDestino), portaDestino);

                    clientSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }

    public static void main(String[] args) throws Exception {
        int port = 8081;
        String diretorio = "peer2";
        String serverInfos = "127.0.0.1:8081";
        // Cria o clientSocket
        DatagramSocket clientSocket = new DatagramSocket();
        iniciaSocket(port, diretorio, clientSocket, serverInfos);

    }
}