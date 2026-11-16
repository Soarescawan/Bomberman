import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class FabricaMapa {
    private static final int TAMANHO = 15;
    private final Random aleatorio = new Random();

    public Jogo criar(String nomeJogador, Dificuldade dificuldade) {
        Tabuleiro tabuleiro = new Tabuleiro(TAMANHO);
        montarBase(tabuleiro);

        Posicao inicio = new Posicao(1, 1);
        List<Posicao> posicoesCaixote = posicionarCaixotes(tabuleiro, inicio);
        esconderSaidaEPoderes(tabuleiro, posicoesCaixote);

        List<Inimigo> inimigos = posicionarInimigos(tabuleiro, dificuldade);

        Jogo jogo = Jogo.vazio();
        jogo.setDificuldade(dificuldade);
        jogo.setTabuleiro(tabuleiro);
        jogo.setJogador(new Jogador(nomeJogador, inicio));
        jogo.setInimigos(inimigos);
        jogo.prepararEstadoInicial();
        return jogo;
    }

    private void montarBase(Tabuleiro tabuleiro) {
        for (int linha = 0; linha < tabuleiro.getTamanho(); linha++) {
            for (int coluna = 0; coluna < tabuleiro.getTamanho(); coluna++) {
                Posicao posicao = new Posicao(linha, coluna);
                if (linha == 0 || coluna == 0 || linha == tabuleiro.getTamanho() - 1 || coluna == tabuleiro.getTamanho() - 1) {
                    tabuleiro.celulaEm(posicao).setTerreno(Terreno.PAREDE);
                } else if (linha % 2 == 0 && coluna % 2 == 0) {
                    tabuleiro.celulaEm(posicao).setTerreno(Terreno.PAREDE);
                } else {
                    tabuleiro.celulaEm(posicao).setTerreno(Terreno.CHAO);
                }
            }
        }
    }

    private List<Posicao> posicionarCaixotes(Tabuleiro tabuleiro, Posicao inicio) {
        List<Posicao> posicoesCaixote = new ArrayList<>();
        for (int linha = 1; linha < tabuleiro.getTamanho() - 1; linha++) {
            for (int coluna = 1; coluna < tabuleiro.getTamanho() - 1; coluna++) {
                Posicao posicao = new Posicao(linha, coluna);
                if (tabuleiro.ehParede(posicao) || ehAreaInicialSegura(posicao, inicio)) {
                    continue;
                }
                if (aleatorio.nextDouble() < 0.36) {
                    tabuleiro.celulaEm(posicao).setTerreno(Terreno.CAIXOTE);
                    posicoesCaixote.add(posicao);
                }
            }
        }
        return posicoesCaixote;
    }

    private boolean ehAreaInicialSegura(Posicao posicao, Posicao inicio) {
        int distancia = Math.abs(posicao.getLinha() - inicio.getLinha()) + Math.abs(posicao.getColuna() - inicio.getColuna());
        return distancia <= 2;
    }

    private void esconderSaidaEPoderes(Tabuleiro tabuleiro, List<Posicao> posicoesCaixote) {
        Collections.shuffle(posicoesCaixote, aleatorio);
        if (!posicoesCaixote.isEmpty()) {
            tabuleiro.celulaEm(posicoesCaixote.get(0)).setSaida(true);
        }
        int poderes = Math.min(6, Math.max(1, posicoesCaixote.size() / 7));
        for (int indice = 1; indice <= poderes && indice < posicoesCaixote.size(); indice++) {
            Poder poder = aleatorio.nextBoolean() ? Poder.BOMBA_EXTRA : Poder.RAIO_EXTRA;
            tabuleiro.celulaEm(posicoesCaixote.get(indice)).setPoder(poder);
        }
    }

    private List<Inimigo> posicionarInimigos(Tabuleiro tabuleiro, Dificuldade dificuldade) {
        List<Inimigo> inimigos = new ArrayList<>();
        int comuns = dificuldade == Dificuldade.DIFICIL ? 4 : 3;
        int rastreadores = dificuldade == Dificuldade.FACIL ? 1 : 2;
        int sabotadores = dificuldade == Dificuldade.FACIL ? 0 : dificuldade == Dificuldade.NORMAL ? 1 : 2;

        for (int i = 0; i < comuns; i++) {
            inimigos.add(new InimigoComum(posicaoLivreAleatoria(tabuleiro, inimigos)));
        }
        for (int i = 0; i < rastreadores; i++) {
            inimigos.add(new InimigoRastreador(posicaoLivreAleatoria(tabuleiro, inimigos)));
        }
        for (int i = 0; i < sabotadores; i++) {
            inimigos.add(new InimigoSabotador(posicaoLivreAleatoria(tabuleiro, inimigos)));
        }
        return inimigos;
    }

    private Posicao posicaoLivreAleatoria(Tabuleiro tabuleiro, List<Inimigo> inimigos) {
        while (true) {
            int linha = 3 + aleatorio.nextInt(tabuleiro.getTamanho() - 4);
            int coluna = 3 + aleatorio.nextInt(tabuleiro.getTamanho() - 4);
            Posicao posicao = new Posicao(linha, coluna);
            boolean ocupado = false;
            for (Inimigo inimigo : inimigos) {
                if (inimigo.getPosicao().mesmaCoordenada(posicao)) {
                    ocupado = true;
                    break;
                }
            }
            if (!ocupado && !tabuleiro.estaBloqueado(posicao)) {
                return posicao;
            }
        }
    }
}
