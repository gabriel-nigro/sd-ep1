import java.util.Scanner;
import java.io.File;

public class Main {
    private static Scanner entrada;

    public static void main(String[] args) {
        entrada = new Scanner(System.in);
        int acao;
        boolean isInitialized = false;

        while (true) {
            System.out.println("\n");
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
                    String peerInfosX = entrada.nextLine();
                    String[] peerInfosXSplited = peerInfosX.split(":");
                    String ipX = peerInfosXSplited[0];
                    int portaX = Integer.parseInt(peerInfosXSplited[1]);

                    System.out.println("\nInforme o IP:PORTA do segundo vizinho");
                    String peerInfosY = entrada.nextLine();
                    String[] peerInfosYSplited = peerInfosY.split(":");
                    String ipY = peerInfosYSplited[0];
                    int portaY = Integer.parseInt(peerInfosYSplited[1]);

                    System.out.println("\nDigite o diretório onde se encontram os arquivos:");
                    String nomeDiretorio = entrada.nextLine();
                    File diretorio = new File(nomeDiretorio);
                    File arquivos[] = diretorio.listFiles();

                    System.out.println("\nArquivos da pasta:");
                    for (File arquivo : arquivos) {
                        System.out.println(arquivo.getName());
                    }

                    // Seta estado de inicializado como "true"
                    isInitialized = true;
                        

                    break;
                }
                case 2: {
                    break;
                }
            }

        }
    }
}