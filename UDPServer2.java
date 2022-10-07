import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// Socket Exception
import java.io.IOException;
import java.net.SocketException;

// Lib para leitura de arquivos
import java.io.File;

// https://stackoverflow.com/questions/40044453/java-peer-to-peer-using-udp-socket
public class UDPServer2 {
    public static boolean verificaArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + "/" + nomeArquivo);
        return arquivo.isFile() ? true : false;
    }

    public static File getArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + nomeArquivo);
        return arquivo;
    }


    public static void iniciaSocket(int port, String diretorio) {
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
                        String arquivo = msg.getArquivo();
                        System.out.println("Arquivo recebido: " + arquivo);

                        if (verificaArquivo(diretorio, arquivo)) {
                            System.out.println("Tem o Arquivo");
                            msg.setConteudoArquivo(getArquivo(diretorio, arquivo));
                            System.out.println(msg.getConteudoArquivo().getAbsolutePath());

                        } else {
                            System.out.println("Não tem o arquivo");
                        }

                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                }
            }

        }).start();
    }

    public static void main(String[] args) throws Exception {
        int port = 9876;
        String diretorio = "peer2";
        iniciaSocket(port, diretorio);

    }
}