enum Poder {
    BOMBA_EXTRA("bomba extra"),
    RAIO_EXTRA("raio extra");

    private final String rotulo;

    Poder(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
