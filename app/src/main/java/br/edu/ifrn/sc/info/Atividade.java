package br.edu.ifrn.sc.info;

import java.io.Serializable;
public class Atividade implements Serializable {
    private String palavra;
    private String silabica;
    private String imagemUrl;
    private String audioUrl;

    public Atividade() {} // Necessário para o Firebase

    public String getPalavra() { return palavra; }
    public String getSilabica() { return silabica; }
    public String getImagemUrl() { return imagemUrl; }
    public String getAudioUrl() { return audioUrl; }
}
