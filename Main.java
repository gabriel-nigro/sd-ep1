
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
import java.nio.file.Files;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
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

    public static boolean verificaArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + "/" + nomeArquivo);
        return arquivo.isFile() && arquivo.exists() ? true : false;
    }

    public static File getArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + nomeArquivo);
        return arquivo;
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
                                System.out.println("peer com arquivo procurado: " + msg.getPeerResponse() + " " + msg.getNomeArquivo());
                            } else {
                                System.out.println("ninguem no sistema possui o arquivo " + msg.getNomeArquivo());
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

        // Armazenamento das informações do server
        String serverInfos = null;

        // Array para armazenamento dos vizinhos
        String[] peers;
        peers = new String[2];

        // Variável para salvar o diretório monitorado
        String nomeDiretorio = null;

        // Array para armazenamento de response
        ArrayList<String> historicoSearch = new ArrayList<String>();

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
                    serverInfos = entrada.nextLine();

                    System.out.println("\nNecessario informar dois vizinhos");
                    System.out.println("\nInforme o IP:PORTA do primeiro vizinho");
                    peers[0] = entrada.nextLine();

                    System.out.println("\nInforme o IP:PORTA do segundo vizinho");
                    peers[1] = entrada.nextLine();

                    System.out.println("\nDigite o diretório onde se encontram os arquivos:");
                    nomeDiretorio = entrada.nextLine();

                    System.out.print("\narquivos da pasta: ");
                    leArquivos(nomeDiretorio);

                    // Inicializa print periódico
                    periodicPrint(serverInfos, nomeDiretorio);

                    // Cria socket
                    int serverPorta = getPorta(serverInfos);
                    iniciaSocket(serverPorta, nomeDiretorio, clientSocket, serverInfos);

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
                    String arquivoBuscado = entrada.nextLine();

                    // Verifica se o arquivo já existe
                    if (verificaArquivo(nomeDiretorio, arquivoBuscado)) {
                        System.out.println("O peer já possui o arquivo.");
                        break;
                    }

                    // Verifica se já fora realizada alguma busca para o arquivo informado
                    for (String historico : historicoSearch) {
                        if (historico.contains(arquivoBuscado)) {
                            System.out.println("Requisição já processada para " + arquivoBuscado);
                            break;
                        }
                    }

                    // Seleciona um vizinho aleatoriamente
                    int numeroPeer = (int) Math.round(Math.random());
                    String ipDestino = getIp(peers[numeroPeer]);
                    int portaDestino = getPorta(peers[numeroPeer]);

                    // Envia mensagem
                    enviaMensagem(clientSocket, serverInfos, arquivoBuscado, ipDestino, portaDestino);

                    // Adiciona no histórico
                    historicoSearch.add(arquivoBuscado);

                    break;
                }
            }

        }
    }
}