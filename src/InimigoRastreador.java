class InimigoRastreador extends Inimigo {
    public InimigoRastreador(Posicao posicao) {
        super(posicao);
    }

    @Override
    public char getSimbolo() {
        return 'R';
    }

    @Override
    public void agir(Jogo jogo) {
        Direcao direcao = jogo.direcaoParaJogadorSeLivre(getPosicao());
        if (direcao != null) {
            jogo.moverInimigoPara(this, getPosicao().mover(direcao));
        } else {
            moverAleatoriamente(jogo);
        }
    }

    @Override
    public Inimigo copia() {
        return new InimigoRastreador(getPosicao());
    }
}
