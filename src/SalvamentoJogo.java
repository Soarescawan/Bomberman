import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class SalvamentoJogo {
    public static void salvar(Jogo jogo, Path caminho) {
        try {
            Files.createDirectories(caminho.getParent());
            try (BufferedWriter escritor = Files.newBufferedWriter(caminho)) {
                Jogador jogador = jogo.getJogador();
                escritor.write("JOGADOR;" + jogador.getNome() + ";" + jogador.getVidas() + ";" + jogador.getLimiteBombas() + ";" + jogador.getRaioExplosao() + ";" + jogador.getDicasUsadas() + ";" + jogador.getPontos() + ";" + jogador.getPosicao().getLinha() + ";" + jogador.getPosicao().getColuna());
                escritor.newLine();
                escritor.write("DIFICULDADE;" + jogo.getDificuldade().name());
                escritor.newLine();
                escritor.write("TURNO;" + jogo.getTurno());
                escritor.newLine();
                escritor.write("DEPURACAO;" + jogo.getDepuracao());
                escritor.newLine();
                escritor.write("TABULEIRO;" + jogo.getTabuleiro().getTamanho());
                escritor.newLine();
                salvarTabuleiro(escritor, jogo.getTabuleiro());
                escritor.write("INIMIGOS;" + jogo.getInimigos().size());
                escritor.newLine();
                for (Inimigo inimigo : jogo.getInimigos()) {
                    escritor.write("INIMIGO;" + inimigo.getSimbolo() + ";" + inimigo.getPosicao().getLinha()
                            + ";" + inimigo.getPosicao().getColuna());
                    escritor.newLine();
                }
                escritor.write("BOMBAS;" + jogo.getBombas().size());
                escritor.newLine();
                for (Bomba bomba : jogo.getBombas()) {
                    escritor.write("BOMBA;" + bomba.getPosicao().getLinha() + ";" + bomba.getPosicao().getColuna() + ";" + bomba.getTemporizador() + ";" + bomba.getRaio() + ";" + bomba.getCamuflada());
                    escritor.newLine();
                }
            }
            System.out.println("Jogo salvo em " + caminho + ".");
        } catch (IOException excecao) {
            System.out.println("Nao foi possivel salvar o jogo.");
        }
    }

    public static Jogo carregar(Path caminho) {
        if (!Files.exists(caminho)) {
            return null;
        }
        try {
            List<String> linhas = Files.readAllLines(caminho);
            Jogo jogo = Jogo.vazio();
            Jogador jogador = null;
            Dificuldade dificuldade = Dificuldade.NORMAL;
            int turno = 0;
            boolean depuracao = false;
            Tabuleiro tabuleiro = null;
            List<Inimigo> inimigos = new ArrayList<>();
            List<Bomba> bombas = new ArrayList<>();

            int indice = 0;
            while (indice < linhas.size()) {
                String linhaTexto = linhas.get(indice);
                String[] partes = linhaTexto.split(";");
                if (partes[0].equals("JOGADOR")) {
                    jogador = new Jogador(partes[1], new Posicao(Integer.parseInt(partes[7]), Integer.parseInt(partes[8])));
                    jogador.setVidas(Integer.parseInt(partes[2]));
                    jogador.setLimiteBombas(Integer.parseInt(partes[3]));
                    jogador.setRaioExplosao(Integer.parseInt(partes[4]));
                    jogador.setDicasUsadas(Integer.parseInt(partes[5]));
                    jogador.setPontos(Integer.parseInt(partes[6]));
                } else if (partes[0].equals("DIFICULDADE")) {
                    dificuldade = Dificuldade.valueOf(partes[1]);
                } else if (partes[0].equals("TURNO")) {
                    turno = Integer.parseInt(partes[1]);
                } else if (partes[0].equals("DEPURACAO")) {
                    depuracao = Boolean.parseBoolean(partes[1]);
                } else if (partes[0].equals("TABULEIRO")) {
                    int tamanho = Integer.parseInt(partes[1]);
                    tabuleiro = new Tabuleiro(tamanho);
                    for (int linha = 0; linha < tamanho; linha++) {
                        carregarLinhaTabuleiro(tabuleiro, linha, linhas.get(indice + 1 + linha));
                    }
                    indice += tamanho;
                } else if (partes[0].equals("INIMIGO")) {
                    inimigos.add(criarInimigo(partes[1].charAt(0),
                            new Posicao(Integer.parseInt(partes[2]), Integer.parseInt(partes[3]))));
                } else if (partes[0].equals("BOMBA")) {
                    Bomba bomba = new Bomba(new Posicao(Integer.parseInt(partes[1]), Integer.parseInt(partes[2])),
                            Integer.parseInt(partes[3]), Integer.parseInt(partes[4]));
                    bomba.setCamuflada(Boolean.parseBoolean(partes[5]));
                    bombas.add(bomba);
                }
                indice++;
            }

            if (jogador == null || tabuleiro == null) {
                return null;
            }
            jogo.setDificuldade(dificuldade);
            jogo.setTabuleiro(tabuleiro);
            jogo.setJogador(jogador);
            jogo.setInimigos(inimigos);
            jogo.setBombas(bombas);
            jogo.setChamas(new ArrayList<>());
            jogo.setTurno(turno);
            jogo.setDepuracao(depuracao);
            jogo.prepararEstadoInicial();
            return jogo;
        } catch (RuntimeException | IOException excecao) {
            return null;
        }
    }

    private static void salvarTabuleiro(BufferedWriter escritor, Tabuleiro tabuleiro) throws IOException {
        for (int linha = 0; linha < tabuleiro.getTamanho(); linha++) {
            StringBuilder linhaTexto = new StringBuilder();
            for (int coluna = 0; coluna < tabuleiro.getTamanho(); coluna++) {
                Celula celula = tabuleiro.celulaEm(new Posicao(linha, coluna));
                char terreno = '.';
                if (celula.getTerreno() == Terreno.PAREDE) {
                    terreno = 'X';
                } else if (celula.getTerreno() == Terreno.CAIXOTE) {
                    terreno = '#';
                }
                char codigoDoPoder = celula.temPoderEscondido() || celula.getPoderVisivel() != null
                        ? (celula.getPoderVisivel() == Poder.BOMBA_EXTRA || celula.temPoderEscondido() ? 'B' : 'R')
                        : '-';
                linhaTexto.append(terreno).append(',')
                        .append(celula.temSaida()).append(',')
                        .append(celula.getSaidaRevelada()).append(',')
                        .append(codigoDoPoder).append('|');
            }
            escritor.write("CELULA;" + linhaTexto);
            escritor.newLine();
        }
    }

    private static void carregarLinhaTabuleiro(Tabuleiro tabuleiro, int linha, String linhaTexto) {
        String dados = linhaTexto.substring("CELULA;".length());
        String[] celulas = dados.split("\\|");
        for (int coluna = 0; coluna < tabuleiro.getTamanho(); coluna++) {
            String[] partes = celulas[coluna].split(",");
            Celula celula = tabuleiro.celulaEm(new Posicao(linha, coluna));
            if (partes[0].equals("X")) {
                celula.setTerreno(Terreno.PAREDE);
            } else if (partes[0].equals("#")) {
                celula.setTerreno(Terreno.CAIXOTE);
            } else {
                celula.setTerreno(Terreno.CHAO);
            }
            celula.setSaida(Boolean.parseBoolean(partes[1]));
            if (Boolean.parseBoolean(partes[2])) {
                celula.revelarSaida();
            }
            if (partes[3].equals("B")) {
                celula.setPoder(Poder.BOMBA_EXTRA);
            } else if (partes[3].equals("R")) {
                celula.setPoder(Poder.RAIO_EXTRA);
            }
        }
    }

    private static Inimigo criarInimigo(char simbolo, Posicao posicao) {
        if (simbolo == 'R') {
            return new InimigoRastreador(posicao);
        }
        if (simbolo == 'S') {
            return new InimigoSabotador(posicao);
        }
        return new InimigoComum(posicao);
    }
}
