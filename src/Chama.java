class Chama {
    private final Posicao posicao;
    private int duracao;

    public Chama(Posicao posicao, int duracao) {
        this.posicao = posicao;
        this.duracao = duracao;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public int getDuracao() {
        return duracao;
    }

    public void diminuir() {
        duracao--;
    }
}
