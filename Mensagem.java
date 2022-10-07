import java.io.Serializable;

public class Mensagem implements Serializable {
    private String senderInfos;
    private String arquivo;
    private String response;

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

}
