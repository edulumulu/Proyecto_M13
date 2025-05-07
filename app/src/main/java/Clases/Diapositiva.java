package Clases;

public class Diapositiva {


    private int id_diapositiva;
    private int id_estudio;
    private int n_diapositivas;
    private boolean timer;
    private int tiempo;
    private int n_respuestas;
    private int respuesta_correcta;
    private String foto;

    public Diapositiva(int id_diapositiva, int id_estudio, int n_diapositivas, boolean timer, int tiempo, int n_respuestas, int respuesta_correcta, String foto) {
        this.id_diapositiva = id_diapositiva;
        this.id_estudio = id_estudio;
        this.n_diapositivas = n_diapositivas;
        this.timer = timer;
        this.tiempo = tiempo;
        this.n_respuestas = n_respuestas;
        this.respuesta_correcta = respuesta_correcta;
        this.foto = foto;
    }

    public int getId_diapositiva() {
        return id_diapositiva;
    }

    public void setId_diapositiva(int id_diapositiva) {
        this.id_diapositiva = id_diapositiva;
    }

    public int getId_estudio() {
        return id_estudio;
    }

    public void setId_estudio(int id_estudio) {
        this.id_estudio = id_estudio;
    }

    public int getN_diapositivas() {
        return n_diapositivas;
    }

    public void setN_diapositivas(int n_diapositivas) {
        this.n_diapositivas = n_diapositivas;
    }

    public boolean isTimer() {
        return timer;
    }

    public void setTimer(boolean timer) {
        this.timer = timer;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getN_respuestas() {
        return n_respuestas;
    }

    public void setN_respuestas(int n_respuestas) {
        this.n_respuestas = n_respuestas;
    }

    public int getRespuesta_correcta() {
        return respuesta_correcta;
    }

    public void setRespuesta_correcta(int respuesta_correcta) {
        this.respuesta_correcta = respuesta_correcta;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
