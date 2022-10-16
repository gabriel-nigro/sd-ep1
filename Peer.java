
// Lib para leitura de input do usuário
import java.util.Scanner;
// Lib para leitura de arquivos
import java.io.File;
import java.nio.file.Files;
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
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
// Lib para validar input do usuário
import java.util.regex.Pattern;
// Lib para timeout de requisição, caso ninguém possua o arquivo
import java.util.Date;
import static java.util.concurrent.TimeUnit.*;

public class Peer {
    private static Scanner entrada;

    /*
     * O método é responsável por obter o IP a partir de uma string no formato
     * IPV4:PORTA
     * 
     * @param peerInfos Informações de IP:PORTA de um peer.
     * 
     * @return String O IP do peer.
     */
    static String getIp(String peerInfos) {
        String[] infosSplited = peerInfos.split(":");
        String ip = infosSplited[0];
        return ip;
    }

    /*
     * O método é responsável por obter a porta a partir de uma string no formato
     * IPV4:PORTA
     * 
     * @param peerInfos Informações de IP:PORTA de um peer.
     * 
     * @return int O IP do peer.
     */
    static int getPorta(String peerInfos) {
        String[] infosSplited = peerInfos.split(":");
        int porta = Integer.parseInt(infosSplited[1]);
        return porta;
    }

    /*
     * O método é responsável por validar a string a partir de um regex IPV4:PORTA.
     * O mesmo é utilizado para validar o input do usuário.
     * 
     * @param peerInfos Informações de IP:PORTA de um peer.
     * 
     * @return boolean O valor booleano para validar as informações.
     */
    static boolean infoValida(String peerInfos) {
        Pattern pattern = Pattern
                .compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5]):[0-9]{1,5}$");
        boolean isValid = pattern.matcher(peerInfos).matches();
        if (!isValid)
            System.out.println("Informações inválidas. A formatação deve ser IP:PORTA");
        return isValid;
    }

    /*
     * O método é responsável por realizar a leitura dos arquivos, no padrão
     * informado no EP.
     * 
     * @param nomeDiretorio
     */
    static void leArquivos(String nomeDiretorio) {
        File diretorio = new File(nomeDiretorio);
        File arquivos[] = diretorio.listFiles();

        for (File arquivo : arquivos) {
            System.out.print(arquivo.getName() + " ");
        }

        System.out.print("\n");
    }

    /*
     * O método é responsável por verificar se o arquivo existe no diretório
     * informado.
     * 
     * @param nomeDiretorio
     * @param nomeArquivo
     * @return boolean O valor booleano referente a existência do arquivo.
     */
    public static boolean verificaArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + "/" + nomeArquivo);
        return arquivo.isFile() && arquivo.exists() ? true : false;
    }

    /*
     * O método é responsável por retornar o arquivo informado.
     * 
     * @param nomeDiretorio
     * 
     * @param nomeArquivo
     * 
     * @return boolean O objeto de classe File
     */
    public static File getArquivo(String nomeDiretorio, String nomeArquivo) {
        File arquivo = new File(nomeDiretorio + "/" + nomeArquivo);
        return arquivo;
    }

    /*
     * O método é responsável por verificar se a mensagem fora enviada a tempo
     * suficiente para receber timeout, quandor recebido um pacote de response.
     * 
     * @param inicial O momento de envio da mensagem
     * 
     * @return boolean O valor booleano referente ao timeout.
     */
    public static boolean verificaTimeoutMensagem(Date inicial) {
        Date agora = new Date();

        long tempoTimeout = MILLISECONDS.convert(30, SECONDS);

        long duracao = agora.getTime() - inicial.getTime();

        return duracao >= tempoTimeout ? true : false;

    }

    /*
     * O método é responsável por indicar timeout na mensagem caso não seja recebido
     * o pacote de response.
     * Assim, é criada uma thread com sleep de 30 segundos, após esse tempo é
     * verificada a presença do arquivo
     * no diretorio do peer. Caso negativo, entende-se que houve um timeout.
     * 
     * @param nomeDiretorio
     * 
     * @param nomeArquivo
     */
    public static void verificaResposta(String nomeDiretorio, String nomeArquivo) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                    if (!verificaArquivo(nomeDiretorio, nomeArquivo))
                        System.out.println("ninguém no sistema possui o arquivo " + nomeArquivo);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    /*
     * O método é responsável por iniciar o socket no peer. As funcionalidades
     * presentes são as seguintes:
     * - Inicialização do socket via "new DatagramSocket"
     * - Recebimento de pacotes
     * - Verificar se a mensagem recebida no pacote é um response, através da
     * propriedade isResponse da classe Mensagem.
     * - Caso positivo, verifica-se se a mensagem já fora processada, através do
     * array de responses.
     * - Caso negativo, há a possibilidade de ser um timeout ou o encaminhamento
     * para o próximo peer
     * - É verificada a propriedade isTimeout da classe Mensagem, printando a
     * mensagem descrita no EP
     * - Caso seja necessário encaminhar a mensagem para outros peers, leva-se em
     * conta se a mensagem já fora
     * encaminhada para os peers "alvos", para que não haja disperdício de
     * reprocessamento.
     * Vale ressaltar que a inicialização utiliza-se do sistema de Threads.
     * Funcionalidade: 4.a)
     * @param port Valor inteiro para criação do socket na porta informada
     * 
     * @param nomeDiretorio
     * 
     * @param clientSocket Socket para encaminhamento de mensagem para outros peers,
     * seja para envio do arquivo ou para continuidade da procura
     * 
     * @param serverInfos informações do servidor no formato IPV4:PORTA
     * 
     * @param responses Respostas já recebidas pelo peer, para não processá-las
     * novamente
     * 
     * @param peers Vizinhos informados na inicialização
     */
    public static void iniciaSocket(int port, String nomeDiretorio, DatagramSocket clientSocket, String serverInfos,
            ArrayList<String> responses, String[] peers) {
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

                // Recebimento contínuo de pacotes enviados por outros peers
                while (true) {
                    try {
                        socket.receive(recPkt);

                        // Transforma o pacote em uma instância da classe Mensagem.
                        ByteArrayInputStream in = new ByteArrayInputStream(recPkt.getData());
                        ObjectInputStream is = new ObjectInputStream(in);
                        Mensagem msg = (Mensagem) is.readObject();

                        // Se a mensagem recebida pelo socket é um response
                        if (msg.getIsResponse()) {
                            // Verifica se já fora realizada alguma busca para o arquivo informado
                            // Funcionalidade: 4.f) já processadas
                            boolean jaProcessada = false;
                            for (String response : responses) {
                                if (response.contains(msg.getNomeArquivo())) {
                                    System.out.println("Requisição já processada para " + msg.getNomeArquivo());
                                }
                            }

                            // Caso o response ainda não tenha sido processada
                            if (!jaProcessada) {
                                File novoArquivo = new File(nomeDiretorio + "/" + msg.getNomeArquivo());
                                Files.copy(msg.getConteudoArquivo().toPath(), novoArquivo.toPath());
                                System.out.println("peer com arquivo procurado: " + msg.getPeerResponse() + " "
                                        + msg.getNomeArquivo());
                                responses.add(msg.getNomeArquivo());
                            }

                        } else if (msg.getIsTimeout()) {
                            System.out.println("ninguém no sistema possui o arquivo " + msg.getNomeArquivo());
                        } else {
                            if (verificaTimeoutMensagem(msg.getHorarioDeEnvio())) {
                                msg.setIsResponse(false);
                                msg.setIsTimeout(true);
                                String ipDestino = getIp(msg.getSenderInfos());
                                int portaDestino = getPorta(msg.getSenderInfos());
                                enviaMensagem(clientSocket, msg, ipDestino, portaDestino);
                            }
                            // Se a mensagem recebida pelo socket é de procura do arquivo
                            String arquivo = msg.getNomeArquivo();
                            // Verifica se o arquivo existe no diretório informado na inicialização
                            // Caso exista, o mesmo é enviado para o peer solicitante
                            // Funcionalidade: 4.e) responde diretamente a quem inicialmente realizou o search
                            if (verificaArquivo(nomeDiretorio, arquivo)) {
                                msg.setConteudoArquivo(getArquivo(nomeDiretorio, arquivo));
                                msg.setIsResponse(true);
                                msg.setPeerResponse(serverInfos);
                                System.out.println("Tenho o arquivo " + msg.getNomeArquivo() + ". Encaminhando para " + msg.getSenderInfos());
                                String ipDestino = getIp(msg.getSenderInfos());
                                int portaDestino = getPorta(msg.getSenderInfos());
                                enviaMensagem(clientSocket, msg, ipDestino, portaDestino);
                            } else {
                                // Seleciona um vizinho aleatoriamente
                                int numeroPeer = (int) Math.round(Math.random());

                                // Cria controle de pesquisa
                                boolean jaPesquisado = false;

                                // Primeira tentativa
                                for (String pesquisado : msg.getHistoricoPeer()) {
                                    if (pesquisado.contains(peers[numeroPeer])) {
                                        jaPesquisado = true;
                                    }
                                }

                                // Muda o nó escolhido
                                if (jaPesquisado) {
                                    if (numeroPeer == 0)
                                        numeroPeer = 1;
                                    else
                                        numeroPeer = 0;
                                }

                                // Segunda tentativa
                                jaPesquisado = false;
                                for (String pesquisado : msg.getHistoricoPeer()) {
                                    if (pesquisado.contains(peers[numeroPeer])) {
                                        jaPesquisado = true;
                                        System.out.println(
                                                "Não tenho " + msg.getNomeArquivo() + ", e meus vizinhos também não.");
                                    }
                                }

                                // Caso algum dos vizinhos ainda não tenha processado a mensagem
                                if (!jaPesquisado) {
                                    String ipDestino = getIp(peers[numeroPeer]);
                                    int portaDestino = getPorta(peers[numeroPeer]);

                                    // Adiciona a si próprio na lista de peers procurados
                                    msg.addHistoricoPeer(serverInfos);
                                    // Funcionalidade: 4.e) não tem, logo encaminha
                                    System.out.println("Não tenho " + msg.getNomeArquivo() + ", encaminhando para "
                                            + peers[numeroPeer]);
                                    // Envia mensagem
                                    enviaMensagem(clientSocket, msg, ipDestino, portaDestino);
                                }
                            }
                        }

                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                }
            }

        }).start();
    }

    /*
     * O método é responsável por relizar o envio de mensagens aos peers
     * 
     * @param clientSocket Socket para encaminhamento de mensagem para outros peers
     * @param mensagem
     * @param ipDestino IP para qual será realizado o envio da mensagem
     * @param portaDestino Porta para qual será realizado o envio da mensagem
     */
    public static void enviaMensagem(DatagramSocket clientSocket, Mensagem mensagem, String ipDestino, int portaDestino) {
        (new Thread() {
            @Override
            public void run() {
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
  
    /*
     * O método é responsável por relizar o print periódico dos arquivos presentes no peer. Para isso, utiliza-se a função Thread.sleep e em sequência a lógica de verificação de arquivos.
     * Funcionalidade: 4.c)
     * @param peerInfos informações do servidor no formato IPV4:PORTA
     * @param nomeDiretorio 
     */
    static void periodicPrint(String peerInfos, String nomeDiretorio) {
        (new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(30000);
                        File diretorio = new File(nomeDiretorio);
                        File arquivos[] = diretorio.listFiles();
                        System.out.print("Sou peer " + peerInfos + " com os arquivos ");

                        for (File arquivo : arquivos) {
                            System.out.print(arquivo.getName() + " ");
                        }

                        System.out.print("\n");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
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

        // Array para armazenamento de histórico de pesquisa e response
        ArrayList<String> historicoSearch = new ArrayList<String>();
        ArrayList<String> responses = new ArrayList<String>();

        // Cria o clientSocket
        DatagramSocket clientSocket = new DatagramSocket();

        // loop infinito para manter o funcionamento das ações enquanto a aplicação estiver rodando
        while (true) {
            System.out.println("\nMenu de acoes.");
            System.out.println("Digite uma opção:");

            // Se o peer já foi inicializado, não mostra a opção de inicialização
            if (!isInitialized)
                System.out.println("1 - INICIALIZA");
            System.out.println("2 - SEARCH");
            acao = Integer.parseInt(entrada.nextLine());

            switch (acao) {
                case 1: {
                    // Funcionalidade 4.b)
                    if (isInitialized) {
                        System.out.println("\nO peer encontra-se inicializado.");
                        break;
                    }
                    // Pega IP e Porta
                    System.out.println("\nInforme o IP:PORTA");
                    serverInfos = entrada.nextLine();
                    if (!infoValida(serverInfos))
                        break;

                    // Coleta informações do primeiro vizinho
                    System.out.println("\nNecessario informar dois vizinhos");
                    System.out.println("\nInforme o IP:PORTA do primeiro vizinho");
                    peers[0] = entrada.nextLine();
                    // Valida as informações coletadas
                    if (!infoValida(peers[0]))
                        break;

                    // Coleta informações do segundo vizinho
                    System.out.println("\nInforme o IP:PORTA do segundo vizinho");
                    peers[1] = entrada.nextLine();
                    // Valida as informações coletadas
                    if (!infoValida(peers[1]))
                        break;

                    System.out.println("\nDigite o diretório onde se encontram os arquivos:");
                    nomeDiretorio = entrada.nextLine();

                    System.out.print("\narquivos da pasta: ");
                    leArquivos(nomeDiretorio);

                    // Inicializa print periódico
                    periodicPrint(serverInfos, nomeDiretorio);

                    // Cria socket
                    int serverPorta = getPorta(serverInfos);
                    iniciaSocket(serverPorta, nomeDiretorio, clientSocket, serverInfos, responses, peers);

                    // Seta estado de inicializado como "true"
                    isInitialized = true;
                    break;
                }
                case 2: {
                    // Funcionalidade: 4.d)
                  
                    // Verifica se o servidor já foi inicializado
                    if (!isInitialized) {
                        System.out
                                .println("\nNecessario realizar inicializacao. Para isso, digite 1 no menu de acoes.");
                        break;
                    }

                    // O arquivo desejado
                    System.out.println("\nDigite o arquivo com a sua extensão:");
                    String arquivoBuscado = entrada.nextLine();

                    // Funcionalidade: 4.e) já processadas
                    // Verifica se já fora realizada alguma busca para o arquivo informado
                    for (String historico : historicoSearch) {
                        if (historico.contains(arquivoBuscado)) {
                            System.out.println("Requisição já processada para " + arquivoBuscado);
                            break;
                        }
                    }
                    
                    // Verifica se o arquivo já existe
                    if (verificaArquivo(nomeDiretorio, arquivoBuscado)) {
                        System.out.println("O peer já possui o arquivo.");
                        break;
                    }

                    // Seleciona um vizinho aleatoriamente
                    int numeroPeer = (int) Math.round(Math.random());
                    String ipDestino = getIp(peers[numeroPeer]);
                    int portaDestino = getPorta(peers[numeroPeer]);

                    // Cria mensagem
                    Mensagem mensagem = new Mensagem(serverInfos, arquivoBuscado, false, false);
                    // Envia mensagem
                    enviaMensagem(clientSocket, mensagem, ipDestino, portaDestino);

                    // Adiciona no histórico
                    historicoSearch.add(arquivoBuscado);

                    // Responde com timeout
                    // Funcionalidade 4.g)
                    verificaResposta(nomeDiretorio, arquivoBuscado);

                    break;
                }
                default:
                    System.out.println("Ação incorreta. Informe uma ação válida.");
                    break;
            }

        }
    }
}