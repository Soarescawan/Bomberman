enum Dificuldade {
    FACIL("facil"),
    NORMAL("normal"),
    DIFICIL("dificil");

    private final String rotulo;

    Dificuldade(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
