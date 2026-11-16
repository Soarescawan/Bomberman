import java.util.ArrayList;
import java.util.List;

class Tabuleiro {
    private final Celula[][] celulas;

    public Tabuleiro(int tamanho) {
        this.celulas = new Celula[tamanho][tamanho];
        for (int linha = 0; linha < tamanho; linha++) {
            for (int coluna = 0; coluna < tamanho; coluna++) {
                celulas[linha][coluna] = new Celula(Terreno.CHAO);
            }
        }
    }

    public int getTamanho() {
        return celulas.length;
    }

    public boolean dentroDosLimites(Posicao posicao) {
        return posicao.getLinha() >= 0 && posicao.getLinha() < getTamanho()
                && posicao.getColuna() >= 0 && posicao.getColuna() < getTamanho();
    }

    public Celula celulaEm(Posicao posicao) {
        return celulas[posicao.getLinha()][posicao.getColuna()];
    }

    public boolean estaBloqueado(Posicao posicao) {
        return ehParede(posicao) || ehCaixote(posicao);
    }

    public boolean ehParede(Posicao posicao) {
        return dentroDosLimites(posicao) && celulaEm(posicao).getTerreno() == Terreno.PAREDE;
    }

    public boolean ehCaixote(Posicao posicao) {
        return dentroDosLimites(posicao) && celulaEm(posicao).getTerreno() == Terreno.CAIXOTE;
    }

    public void destruirCaixote(Posicao posicao) {
        Celula celula = celulaEm(posicao);
        if (celula.getTerreno() == Terreno.CAIXOTE) {
            celula.setTerreno(Terreno.CHAO);
            if (celula.temSaida()) {
                celula.revelarSaida();
            }
        }
    }

    public char[][] paraMatriz(boolean depuracao) {
        char[][] matriz = new char[getTamanho()][getTamanho()];
        for (int linha = 0; linha < getTamanho(); linha++) {
            for (int coluna = 0; coluna < getTamanho(); coluna++) {
                Celula celula = celulas[linha][coluna];
                if (celula.getTerreno() == Terreno.PAREDE) {
                    matriz[linha][coluna] = 'X';
                } else if (celula.getTerreno() == Terreno.CAIXOTE) {
                    if (depuracao && celula.temSaida()) {
                        matriz[linha][coluna] = 'O';
                    } else if (depuracao && celula.temPoderEscondido()) {
                        matriz[linha][coluna] = '*';
                    } else {
                        matriz[linha][coluna] = '#';
                    }
                } else if (celula.getSaidaRevelada()) {
                    matriz[linha][coluna] = 'O';
                } else if (celula.getPoderVisivel() != null) {
                    matriz[linha][coluna] = '*';
                } else {
                    matriz[linha][coluna] = '.';
                }
            }
        }
        return matriz;
    }

    public List<String> exportarMapaVisivel(Posicao inicioJogador, List<Inimigo> inimigos) {
        char[][] matriz = paraMatriz(false);
        matriz[inicioJogador.getLinha()][inicioJogador.getColuna()] = 'P';
        for (Inimigo inimigo : inimigos) {
            matriz[inimigo.getPosicao().getLinha()][inimigo.getPosicao().getColuna()] = inimigo.getSimbolo();
        }
        List<String> linhas = new ArrayList<>();
        for (int linha = 0; linha < matriz.length; linha++) {
            linhas.add(new String(matriz[linha]));
        }
        return linhas;
    }

    public Tabuleiro copia() {
        Tabuleiro copia = new Tabuleiro(getTamanho());
        for (int linha = 0; linha < getTamanho(); linha++) {
            for (int coluna = 0; coluna < getTamanho(); coluna++) {
                copia.celulas[linha][coluna] = celulas[linha][coluna].copia();
            }
        }
        return copia;
    }
}
