class Bomba {
    private final Posicao posicao;
    private int temporizador;
    private final int raio;
    private boolean camuflada;

    public Bomba(Posicao posicao, int temporizador, int raio) {
        this.posicao = posicao;
        this.temporizador = temporizador;
        this.raio = raio;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public int getTemporizador() {
        return temporizador;
    }

    public void setTemporizador(int temporizador) {
        this.temporizador = temporizador;
    }

    public int getRaio() {
        return raio;
    }

    public boolean getCamuflada() {
        return camuflada;
    }

    public void setCamuflada(boolean camuflada) {
        this.camuflada = camuflada;
    }

    public void camuflar() {
        camuflada = true;
    }

    public void diminuirTemporizador() {
        temporizador--;
    }
}
