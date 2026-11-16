class Posicao {
    private final int linha;
    private final int coluna;

    public Posicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public Posicao mover(Direcao direcao) {
        return new Posicao(linha + direcao.getDeltaLinha(), coluna + direcao.getDeltaColuna());
    }

    public boolean mesmaCoordenada(Posicao outra) {
        return outra != null
                && linha == outra.getLinha()
                && coluna == outra.getColuna();
    }
}
