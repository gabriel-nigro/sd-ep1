// Lib para leitura de input do usuário
import java.util.Scanner;
// Lib para leitura de arquivos
import java.io.File;
// Lib para lidar com arrays
import java.util.ArrayList;
// Libs para socket
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
// Libs para print periódico
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Scanner entrada;

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
    
    static void leArquivos(String nomeDiretorio) {
        File diretorio = new File(nomeDiretorio);
        File arquivos[] = diretorio.listFiles();

        for (File arquivo : arquivos) {
            System.out.print(arquivo.getName() + " ");
        }

        System.out.print("\n");
    }


    static void periodicPrint(String peerInfos, String nomeDiretorio) {
        Runnable runnable = new Runnable() {
            public void run() {
                File diretorio = new File(nomeDiretorio);
                File arquivos[] = diretorio.listFiles();
                System.out.print("Sou peer " + peerInfos + " com os arquivos ");

                for (File arquivo : arquivos) {
                    System.out.print(arquivo.getName() + " ");
                }

                System.out.print("\n");
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        // Variável que guarda o input do usuário
        entrada = new Scanner(System.in);

        // Ação escolhida pelo usuário
        int acao;

        // Variavel de estado para inicializacao
        boolean isInitialized = false;

        // Array para armazenamento dos vizinhos
        String[] peers;
        peers = new String[2];

        // Array para armazenamento de response
        ArrayList<String> responses = new ArrayList<String>();

        // Cria o clientSocket
        DatagramSocket clientSocket = new DatagramSocket();

        while (true) {
            System.out.println("\nMenu de acoes.");
            System.out.println("Digite uma opção:");
            if (!isInitialized)
                System.out.println("1 - INICIALIZA");
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
                    String serverInfos = entrada.nextLine();

                    System.out.println("\nNecessario informar dois vizinhos");
                    System.out.println("\nInforme o IP:PORTA do primeiro vizinho");
                    peers[0] = entrada.nextLine();

                    System.out.println("\nInforme o IP:PORTA do segundo vizinho");
                    peers[1] = entrada.nextLine();

                    System.out.println("\nDigite o diretório onde se encontram os arquivos:");
                    String nomeDiretorio = entrada.nextLine();

                    System.out.print("\narquivos da pasta: ");
                    leArquivos(nomeDiretorio);

                    // Inicializa print periódico
                    periodicPrint(serverInfos, nomeDiretorio);

                    // Cria socket
                    int serverPorta = getPorta(serverInfos);
                    DatagramSocket serverSocket = new DatagramSocket(serverPorta);
                    
                    // Seta estado de inicializado como "true"
                    isInitialized = true;
                    break;
                }
                case 2: {
                    // Verifica se o servidor já foi inicializado
                    if (!isInitialized) {
                        System.out
                                .println("\nNecessario realizar inicializacao. Para isso, digite 1 no menu de acoes.");
                        break;
                    }

                    // O arquivo desejado
                    System.out.println("\nDigite o arquivo com a sua extensão:");
                    String arquivo = entrada.nextLine();

                    // Seleciona um vizinho aleatoriamente
                    int numeroPeer = (int) Math.round(Math.random());
                    String peerIp = getIp(peers[numeroPeer]);
                    int peerPorta = getPorta(peers[numeroPeer]);

                    // Cria objeto de mensagem
                    Mensagem mensagem = new Mensagem(peers[numeroPeer], arquivo);

                    // declaração e preenchimento do buffer de envio
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(mensagem);
                    final byte[] sendMessage = baos.toByteArray();

                    // Criação do datagrama com endereço e porta do host remoto
                    DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, InetAddress.getByName(peerIp), peerPorta);

                    clientSocket.send(sendPacket);
                    break;
                }
            }

        }
    }
}