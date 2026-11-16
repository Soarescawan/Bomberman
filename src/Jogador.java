class Jogador extends Entidade {
    private final String nome;
    private int vidas;
    private int limiteBombas;
    private int raioExplosao;
    private int dicasUsadas;
    private int pontos;

    public Jogador(String nome, Posicao posicao) {
        super(posicao);
        this.nome = nome;
        this.vidas = 3;
        this.limiteBombas = 1;
        this.raioExplosao = 1;
    }

    public String getNome() {
        return nome;
    }

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    public void perderUmaVida() {
        vidas--;
    }

    public int getLimiteBombas() {
        return limiteBombas;
    }

    public void setLimiteBombas(int limiteBombas) {
        this.limiteBombas = limiteBombas;
    }

    public int getRaioExplosao() {
        return raioExplosao;
    }

    public void setRaioExplosao(int raioExplosao) {
        this.raioExplosao = raioExplosao;
    }

    public int getDicasUsadas() {
        return dicasUsadas;
    }

    public void setDicasUsadas(int dicasUsadas) {
        this.dicasUsadas = dicasUsadas;
    }

    public void usarDica() {
        dicasUsadas++;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public void adicionarPontos(int valor) {
        pontos += valor;
    }

    public void aplicar(Poder poder) {
        if (poder == Poder.BOMBA_EXTRA) {
            limiteBombas++;
        } else if (poder == Poder.RAIO_EXTRA) {
            raioExplosao++;
        }
        adicionarPontos(50);
    }
}
