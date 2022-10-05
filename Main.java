import java.util.Scanner;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Main {
    private static Scanner entrada;

    public static void main(String[] args) throws Exception {
        entrada = new Scanner(System.in);
        int acao;
        // Variavel de estado para inicializacao
        boolean isInitialized = false;

        // Array para armazenamento dos vizinhos
        String[] peers;
        peers = new String[2];
        
        // Cria o clientSocket
        DatagramSocket clientSocket = new DatagramSocket();

        // declaração e preenchimento do buffer de envio
        byte[] sendData = new byte[1024];

        while (true) {
            System.out.println("\nMenu de acoes.");
            System.out.println("Digite uma opção:");
            if (!isInitialized) System.out.println("1 - INICIALIZA");
            System.out.println("2 - SEARCH");
            acao = Integer.parseInt(entrada.nextLine());

            switch (acao) {
                case 1: {
                    if (isInitialized) {
                      System.out.println("\nO peer encontra-se inicializado.");
                      break;
                    }  
                    // Pega IP e Porta
                    System.out.println("\nInforme o IP:PORTA");
                    String peerInfos = entrada.nextLine();
                    String[] peerInfosSplited = peerInfos.split(":");
                    String ip = peerInfosSplited[0];
                    int porta = Integer.parseInt(peerInfosSplited[1]);

                    System.out.println("\nNecessario informar dois vizinhos");
                    System.out.println("\nInforme o IP:PORTA do primeiro vizinho");
                    peers[0] = entrada.nextLine();

                    System.out.println("\nInforme o IP:PORTA do segundo vizinho");
                    peers[1] = entrada.nextLine();

                    System.out.println("\nDigite o diretório onde se encontram os arquivos:");
                    String nomeDiretorio = entrada.nextLine();
                    File diretorio = new File(nomeDiretorio);
                    File arquivos[] = diretorio.listFiles();

                    System.out.println("\nArquivos da pasta:");
                    for (File arquivo : arquivos) {
                        System.out.println(arquivo.getName());
                    }

                    // Cria socket
                    DatagramSocket serverSocket = new DatagramSocket(porta);
                    // Seta estado de inicializado como "true"
                    isInitialized = true;
                        

                    break;
                }
                case 2: {
                  
                    if (!isInitialized) {
                      System.out.println("\nNecessario realizar inicializacao. Para isso, digite 1 no menu de acoes.");
                      break;
                    }

                    // Seleciona um vizinho aleatoriamente
                    int numeroPeer = (int)Math.round(Math.random());
                    System.out.println(peers);
                    String[] peerInfos = peers[numeroPeer].split(":");
                    String peerIp = peerInfos[0];
                    int peerPorta = Integer.parseInt(peerInfos[1]);
                    
                    // Criação do datagrama com endereço e porta do host remoto
                    // DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(peerIp), peerPorta);
                  
                    // clientSocket.send(sendPacket);
                    break;
                }
            }

        }
    }
}