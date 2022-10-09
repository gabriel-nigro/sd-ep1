import java.io.Serializable;
// Lib para leitura de arquivos
import java.io.File;

public class Mensagem implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    
    private String senderInfos;
    private String nomeArquivo;
    private String response;
    private File conteudoArquivo;
    private boolean isResponse;
    private String peerResponse;

    public Mensagem(String senderInfos, String arquivo, boolean isResponse) {
        this.senderInfos = senderInfos;
        this.nomeArquivo = arquivo;
        this.isResponse = isResponse;
    }

    public String getSenderInfos() {
        return this.senderInfos;
    }

    public String getNomeArquivo() {
        return this.nomeArquivo;
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

    public boolean getIsResponse() {
        return this.isResponse;
    }

    public void setIsResponse(Boolean isResponse) {
        this.isResponse = isResponse;
    }

    public String getPeerResponse() {
        return this.peerResponse;
    }

    public void setPeerResponse(String peerResponse) {
        this.peerResponse = peerResponse;
    }

}
