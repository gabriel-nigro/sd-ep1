import java.io.Serializable;
// Lib para leitura de arquivos
import java.io.File;

public class Mensagem implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    
    private String senderInfos;
    private String arquivo;
    private String response;
    private File conteudoArquivo;

    public Mensagem(String senderInfos, String arquivo) {
        this.senderInfos = senderInfos;
        this.arquivo = arquivo;
    }

    public String getSenderInfos() {
        return this.senderInfos;
    }

    public String getArquivo() {
        return this.arquivo;
    }

    public String getResponse() {
        return this.response;
    }

    public File getConteudoArquivo() {
        return this.conteudoArquivo;
    }

    public void setConteudoArquivo(File conteudoArquivo) {
        this.conteudoArquivo = conteudoArquivo;
    }

}
