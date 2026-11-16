abstract class Inimigo extends Entidade {
    protected Inimigo(Posicao posicao) {
        super(posicao);
    }

    public abstract char getSimbolo();

    public abstract void agir(Jogo jogo);

    public abstract Inimigo copia();

    protected void moverAleatoriamente(Jogo jogo) {
        for (Direcao direcao : jogo.direcoesEmbaralhadas()) {
            Posicao destino = getPosicao().mover(direcao);
            if (destino.mesmaCoordenada(jogo.getJogador().getPosicao()) || jogo.podeInimigoMoverPara(destino)) {
                jogo.moverInimigoPara(this, destino);
                return;
            }
        }
    }
}
