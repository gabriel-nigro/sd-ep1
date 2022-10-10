import java.io.File;

public class Fio {
    public static void main(String[] args) {
        String diretorio = "peer1";
        String arquivo = "abobora.js";
        File tempFile = new File(diretorio + "/" + arquivo);

        boolean exists = tempFile.exists();

        System.out.println("Existe? " + exists);
    }
}
