class Celula {
    private Terreno terreno;
    private Poder poder;
    private boolean saida;
    private boolean saidaRevelada;

    public Celula(Terreno terreno) {
        this.terreno = terreno;
    }

    public Terreno getTerreno() {
        return terreno;
    }

    public void setTerreno(Terreno terreno) {
        this.terreno = terreno;
    }

    public void setPoder(Poder poder) {
        this.poder = poder;
    }

    public boolean temPoderEscondido() {
        return terreno == Terreno.CAIXOTE && poder != null;
    }

    public Poder getPoderVisivel() {
        if (terreno == Terreno.CHAO) {
            return poder;
        }
        return null;
    }

    public void removerPoder() {
        poder = null;
    }

    public boolean temSaida() {
        return saida;
    }

    public void setSaida(boolean saida) {
        this.saida = saida;
    }

    public boolean getSaidaRevelada() {
        return saidaRevelada;
    }

    public void revelarSaida() {
        saidaRevelada = true;
    }

    public Celula copia() {
        Celula copia = new Celula(terreno);
        copia.poder = poder;
        copia.saida = saida;
        copia.saidaRevelada = saidaRevelada;
        return copia;
    }
}
