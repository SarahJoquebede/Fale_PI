package br.edu.ifrn.sc.info;

public class AudioItem {
    private String id;
    private String arquivoUrl;
    private String autorEmail;

    public AudioItem() {


    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }

    public String getAutorEmail() { return autorEmail; }
    public void setAutorEmail(String autorEmail) { this.autorEmail = autorEmail; }
}
