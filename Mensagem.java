// Lib para serializar os objetos enviados e recebidos via socket
import java.io.Serializable;
// Lib para leitura de arquivos
import java.io.File;
// Lib para array de historico de peers
import java.util.ArrayList;
// Lib para utilização de Data
import java.util.Date;

public class Mensagem implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    
    private String senderInfos;
    private String nomeArquivo;
    private String response;
    private File conteudoArquivo;
    private boolean isResponse;
    private String peerResponse;
    private Date horarioDeEnvio;
    private boolean isTimeout;
    private ArrayList<String> historicoPeers =  new ArrayList<String>();

    public Mensagem(String senderInfos, String arquivo, boolean isResponse, boolean isTimeout) {
        this.senderInfos = senderInfos;
        this.nomeArquivo = arquivo;
        this.isResponse = isResponse;
        this.isTimeout = isTimeout;
        this.historicoPeers.add(senderInfos);
        horarioDeEnvio = new Date();
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

    public Date getHorarioDeEnvio() {
        return this.horarioDeEnvio;
    }

    public boolean getIsTimeout() {
        return this.isTimeout;
    }

    public void setIsTimeout(Boolean isTimeout) {
        this.isTimeout = isTimeout;
    }

    public void addHistoricoPeer(String peer) {
        this.historicoPeers.add(peer);
    }

    public ArrayList<String> getHistoricoPeer() {
        return this.historicoPeers;
    }

}
