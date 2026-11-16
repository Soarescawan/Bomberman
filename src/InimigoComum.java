class InimigoComum extends Inimigo {
    public InimigoComum(Posicao posicao) {
        super(posicao);
    }

    @Override
    public char getSimbolo() {
        return 'C';
    }

    @Override
    public void agir(Jogo jogo) {
        if (jogo.getTurno() % 2 == 0) {
            moverAleatoriamente(jogo);
        }
    }

    @Override
    public Inimigo copia() {
        return new InimigoComum(getPosicao());
    }
}
