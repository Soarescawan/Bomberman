abstract class Entidade implements Destrutivel {
    private Posicao posicao;

    protected Entidade(Posicao posicao) {
        this.posicao = posicao;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public void setPosicao(Posicao posicao) {
        this.posicao = posicao;
    }

    @Override
    public void receberExplosao(Jogo jogo) {
    }
}
