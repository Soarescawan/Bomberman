enum Direcao {
    CIMA(-1, 0),
    BAIXO(1, 0),
    ESQUERDA(0, -1),
    DIREITA(0, 1);

    private final int deltaLinha;
    private final int deltaColuna;

    Direcao(int deltaLinha, int deltaColuna) {
        this.deltaLinha = deltaLinha;
        this.deltaColuna = deltaColuna;
    }

    public int getDeltaLinha() {
        return deltaLinha;
    }

    public int getDeltaColuna() {
        return deltaColuna;
    }

    public static Direcao peloComando(String comando) {
        if (comando.equals("W")) {
            return CIMA;
        }
        if (comando.equals("S")) {
            return BAIXO;
        }
        if (comando.equals("A")) {
            return ESQUERDA;
        }
        return DIREITA;
    }
}
