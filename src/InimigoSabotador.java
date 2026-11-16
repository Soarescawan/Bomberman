class InimigoSabotador extends Inimigo {
    public InimigoSabotador(Posicao posicao) {
        super(posicao);
    }

    @Override
    public char getSimbolo() {
        return 'S';
    }

    @Override
    public void agir(Jogo jogo) {
        Bomba bomba = jogo.encontrarBombaParaSabotar(getPosicao());
        if (bomba != null) {
            jogo.sabotar(bomba);
            return;
        }
        moverAleatoriamente(jogo);
    }

    @Override
    public Inimigo copia() {
        return new InimigoSabotador(getPosicao());
    }
}
