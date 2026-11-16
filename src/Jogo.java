import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class Jogo {
    private static final Path CAMINHO_ULTIMO_MAPA = Path.of("dados", "ultimo_mapa.txt");

    private Tabuleiro tabuleiro;
    private Tabuleiro tabuleiroInicial;
    private Jogador jogador;
    private Dificuldade dificuldade;
    private Posicao inicioJogador;
    private List<Inimigo> inimigos;
    private List<Inimigo> inimigosIniciais;
    private List<Bomba> bombas;
    private List<Chama> chamas;
    private Random aleatorio;
    private boolean depuracao;
    private boolean finalizado;
    private boolean venceu;
    private boolean faseReiniciada;
    private int turno;
    private String mensagem;

    private Jogo() {
        this.inimigos = new ArrayList<>();
        this.inimigosIniciais = new ArrayList<>();
        this.bombas = new ArrayList<>();
        this.chamas = new ArrayList<>();
        this.aleatorio = new Random();
        this.mensagem = "";
    }

    public static Jogo novoJogo(String nomeJogador, Dificuldade dificuldade) {
        FabricaMapa fabrica = new FabricaMapa();
        Jogo jogo = fabrica.criar(nomeJogador, dificuldade);
        jogo.salvarMapaAtual();
        return jogo;
    }

    public void executar(Scanner leitor) {
        finalizado = false;
        venceu = false;

        while (!finalizado) {
            imprimirTela();
            System.out.print("Comando: ");
            String comando = leitor.nextLine().trim().toUpperCase();

            boolean turnoPassou = tratarComando(comando, leitor);
            if (finalizado) {
                break;
            }

            if (turnoPassou) {
                proximoTurno();
                verificarVitoria();
            }
        }

        if (venceu) {
            jogador.adicionarPontos(500);
            System.out.println();
            System.out.println("Voce venceu! Pontuacao final: " + (jogador.getPontos() + jogador.getVidas() * 100));
        } else {
            System.out.println();
            System.out.println("Fim de jogo.");
        }

        menuFinal(leitor);
    }

    private boolean tratarComando(String comando, Scanner leitor) {
        try {
            if (comando.equals("W") || comando.equals("A") || comando.equals("S") || comando.equals("D")) {
                moverJogador(Direcao.peloComando(comando));
                return true;
            }
            if (comando.equals("B")) {
                plantarBomba();
                return true;
            }
            if (comando.equals("E")) {
                mensagem = "Voce esperou um turno.";
                return true;
            }
            if (comando.equals("H")) {
                mostrarDica();
                return false;
            }
            if (comando.equals("DEPURACAO") || comando.equals("DEPURACAO")) {
                depuracao = !depuracao;
                mensagem = "DEPURACAO " + (depuracao ? "ativado." : "desativado.");
                return false;
            }
            if (comando.equals("Q")) {
                perguntarSaida(leitor);
                return false;
            }
            mensagem = "Comando invalido.";
            return false;
        } catch (ExcecaoJogo excecao) {
            mensagem = excecao.getMessage();
            return false;
        }
    }

    private void proximoTurno() {
        turno++;
        faseReiniciada = false;
        atualizarBombas();
        if (faseReiniciada || finalizado) {
            return;
        }
        moverInimigos();
        if (faseReiniciada || finalizado) {
            return;
        }
        causarDanoEmEntidadesNasChamas();
        if (faseReiniciada || finalizado) {
            return;
        }
        atualizarChamas();
    }

    private void imprimirTela() {
        System.out.println();
        System.out.println("--------------------------------------");
        System.out.println("Jogador: " + jogador.getNome() + " | Vidas: " + jogador.getVidas()
                + " | Bombas: " + bombasAtivasDoJogador() + "/" + jogador.getLimiteBombas()
                + " | Raio: " + jogador.getRaioExplosao()
                + " | Dicas: " + jogador.getDicasUsadas() + "/3"
                + " | Pontos: " + jogador.getPontos()
                + " | Turno: " + turno);
        System.out.println("Inimigos vivos: " + inimigos.size() + " | Dificuldade: " + dificuldade.getRotulo());
        System.out.println("--------------------------------------");

        char[][] matriz = tabuleiro.paraMatriz(depuracao);

        for (Bomba bomba : bombas) {
            if (bomba.getCamuflada()) {
                matriz[bomba.getPosicao().getLinha()][bomba.getPosicao().getColuna()] = '#';
            } else {
                matriz[bomba.getPosicao().getLinha()][bomba.getPosicao().getColuna()] = 'B';
            }
        }
        for (Chama chama : chamas) {
            matriz[chama.getPosicao().getLinha()][chama.getPosicao().getColuna()] = '+';
        }
        for (Inimigo inimigo : inimigos) {
            matriz[inimigo.getPosicao().getLinha()][inimigo.getPosicao().getColuna()] = inimigo.getSimbolo();
        }
        matriz[jogador.getPosicao().getLinha()][jogador.getPosicao().getColuna()] = 'P';

        for (int linha = 0; linha < matriz.length; linha++) {
            for (int coluna = 0; coluna < matriz[linha].length; coluna++) {
                System.out.print(matriz[linha][coluna] + " ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Legenda: P jogador | X parede | # caixote | C comum | R rastreador | S sabotador");
        System.out.println("         B bomba | + fogo | * poder | O saida");
        System.out.println("Comandos: W/A/S/D mover | B bomba | E esperar | H dica | DEPURACAO revelar | Q sair");
        if (!mensagem.isEmpty()) {
            System.out.println("Mensagem: " + mensagem);
        }
    }

    private void moverJogador(Direcao direcao) throws ExcecaoMovimentoInvalido {
        Posicao destino = jogador.getPosicao().mover(direcao);
        Bomba bomba = bombaEm(destino);
        if (bomba != null && bomba.getCamuflada()) {
            mensagem = "Voce encostou em uma armadilha camuflada!";
            detonar(bomba);
            return;
        }
        if (!tabuleiro.dentroDosLimites(destino)) {
            throw new ExcecaoMovimentoInvalido("Movimento invalido: fora do mapa.");
        }
        if (tabuleiro.estaBloqueado(destino)) {
            throw new ExcecaoMovimentoInvalido("Movimento invalido: existe uma parede ou caixote no caminho.");
        }
        if (bomba != null) {
            throw new ExcecaoMovimentoInvalido("Movimento invalido: existe uma bomba nessa posicao.");
        }

        jogador.setPosicao(destino);

        Inimigo inimigo = inimigoEm(destino);
        if (inimigo != null) {
            perderUmaVida("Voce encostou em um inimigo.");
            return;
        }

        Celula celula = tabuleiro.celulaEm(destino);
        if (celula.getPoderVisivel() != null) {
            Poder poder = celula.getPoderVisivel();
            jogador.aplicar(poder);
            celula.removerPoder();
            mensagem = "Poder coletado: " + poder.getRotulo() + ".";
        } else if (celula.getSaidaRevelada()) {
            mensagem = "Saida encontrada. Elimine todos os inimigos para vencer.";
        } else {
            mensagem = "Movimento realizado.";
        }
    }

    private void plantarBomba() throws ExcecaoLimiteBombas, ExcecaoMovimentoInvalido {
        if (bombasAtivasDoJogador() >= jogador.getLimiteBombas()) {
            throw new ExcecaoLimiteBombas("Limite de bombas simultaneas atingido.");
        }
        if (bombaEm(jogador.getPosicao()) != null) {
            throw new ExcecaoMovimentoInvalido("Ja existe uma bomba nesta posicao.");
        }
        bombas.add(new Bomba(jogador.getPosicao(), 5, jogador.getRaioExplosao()));
        mensagem = "Bomba plantada. Ela detona em 5 turnos.";
    }

    private void mostrarDica() {
        if (jogador.getDicasUsadas() >= 3) {
            mensagem = "Voce ja usou as 3 dicas disponiveis.";
            return;
        }
        jogador.usarDica();

        int raio = 3;
        for (int linha = jogador.getPosicao().getLinha() - raio; linha <= jogador.getPosicao().getLinha() + raio; linha++) {
            for (int coluna = jogador.getPosicao().getColuna() - raio; coluna <= jogador.getPosicao().getColuna() + raio; coluna++) {
                Posicao posicao = new Posicao(linha, coluna);
                if (tabuleiro.dentroDosLimites(posicao) && tabuleiro.celulaEm(posicao).temPoderEscondido()) {
                    mensagem = "Dica: existe um poder escondido nas proximidades.";
                    return;
                }
            }
        }
        mensagem = "Dica: nao ha poder escondido por perto.";
    }

    private void perguntarSaida(Scanner leitor) {
        System.out.println("1 - Salvar e sair");
        System.out.println("2 - Sair sem salvar");
        System.out.println("3 - Continuar");
        System.out.print("Escolha: ");
        String opcao = leitor.nextLine().trim();
        if (opcao.equals("1")) {
            SalvamentoJogo.salvar(this, Path.of("dados", "jogo_salvo.txt"));
            finalizado = true;
            mensagem = "Jogo salvo.";
        } else if (opcao.equals("2")) {
            finalizado = true;
        } else {
            mensagem = "Jogo continuado.";
        }
    }

    private void atualizarBombas() {
        List<Bomba> copia = new ArrayList<>(bombas);
        for (Bomba bomba : copia) {
            if (!bombas.contains(bomba)) {
                continue;
            }
            bomba.diminuirTemporizador();
            if (bomba.getTemporizador() <= 0) {
                detonar(bomba);
                if (faseReiniciada || finalizado) {
                    return;
                }
            }
        }
    }

    private void detonar(Bomba bomba) {
        bombas.remove(bomba);
        List<Posicao> explosao = calcularExplosao(bomba);

        for (Posicao posicao : explosao) {
            if (!tabuleiro.ehParede(posicao)) {
                chamas.add(new Chama(posicao, 2));
            }
        }

        List<Bomba> bombasEmCadeia = new ArrayList<>();
        for (Bomba outra : bombas) {
            if (posicoesContem(explosao, outra.getPosicao())) {
                bombasEmCadeia.add(outra);
            }
        }
        for (Bomba outra : bombasEmCadeia) {
            if (bombas.contains(outra)) {
                detonar(outra);
                if (faseReiniciada || finalizado) {
                    return;
                }
            }
        }

        List<Inimigo> derrotados = new ArrayList<>();
        for (Inimigo inimigo : inimigos) {
            if (posicoesContem(explosao, inimigo.getPosicao())) {
                derrotados.add(inimigo);
            }
        }
        inimigos.removeAll(derrotados);
        if (!derrotados.isEmpty()) {
            jogador.adicionarPontos(derrotados.size() * 100);
        }

        if (posicoesContem(explosao, jogador.getPosicao())) {
            perderUmaVida("Voce foi atingido pela explosao.");
        } else {
            mensagem = "Bomba detonou.";
        }
    }

    private List<Posicao> calcularExplosao(Bomba bomba) {
        List<Posicao> posicoes = new ArrayList<>();
        posicoes.add(bomba.getPosicao());

        for (Direcao direcao : Direcao.values()) {
            Posicao atual = bomba.getPosicao();
            for (int distancia = 0; distancia < bomba.getRaio(); distancia++) {
                atual = atual.mover(direcao);
                if (!tabuleiro.dentroDosLimites(atual)) {
                    break;
                }
                if (tabuleiro.ehParede(atual)) {
                    break;
                }
                posicoes.add(atual);

                Bomba camuflada = bombaEm(atual);
                if (camuflada != null && camuflada.getCamuflada()) {
                    break;
                }

                if (tabuleiro.ehCaixote(atual)) {
                    tabuleiro.destruirCaixote(atual);
                    break;
                }
            }
        }

        return posicoes;
    }

    private boolean posicoesContem(List<Posicao> posicoes, Posicao procurada) {
        for (Posicao posicao : posicoes) {
            if (posicao.mesmaCoordenada(procurada)) {
                return true;
            }
        }
        return false;
    }

    private void moverInimigos() {
        List<Inimigo> copia = new ArrayList<>(inimigos);
        for (Inimigo inimigo : copia) {
            if (inimigos.contains(inimigo)) {
                inimigo.agir(this);
                if (faseReiniciada || finalizado) {
                    return;
                }
            }
        }
    }

    private void causarDanoEmEntidadesNasChamas() {
        List<Inimigo> derrotados = new ArrayList<>();
        for (Inimigo inimigo : inimigos) {
            if (chamaEm(inimigo.getPosicao())) {
                derrotados.add(inimigo);
            }
        }
        inimigos.removeAll(derrotados);
        if (!derrotados.isEmpty()) {
            jogador.adicionarPontos(derrotados.size() * 100);
        }
        if (chamaEm(jogador.getPosicao())) {
            perderUmaVida("Voce pisou no fogo.");
        }
    }

    private void atualizarChamas() {
        List<Chama> expiradas = new ArrayList<>();
        for (Chama chama : chamas) {
            chama.diminuir();
            if (chama.getDuracao() <= 0) {
                expiradas.add(chama);
            }
        }
        chamas.removeAll(expiradas);
    }

    private void perderUmaVida(String motivo) {
        jogador.perderUmaVida();
        if (jogador.getVidas() <= 0) {
            finalizado = true;
            venceu = false;
            mensagem = motivo + " Voce perdeu todas as vidas.";
            return;
        }
        reiniciarFaseMantendoVidas();
        faseReiniciada = true;
        mensagem = motivo + " Voce perdeu uma vida e a fase foi reiniciada.";
    }

    private void verificarVitoria() {
        if (inimigos.isEmpty() && tabuleiro.celulaEm(jogador.getPosicao()).getSaidaRevelada()) {
            finalizado = true;
            venceu = true;
        }
    }

    private void menuFinal(Scanner leitor) {
        while (true) {
            System.out.println();
            System.out.println("1 - Reiniciar jogo");
            System.out.println("2 - Novo jogo");
            System.out.println("3 - Voltar ao menu inicial");
            System.out.print("Escolha: ");
            String opcao = leitor.nextLine().trim();
            if (opcao.equals("1")) {
                reiniciarJogoInteiroMesmoMapa();
                executar(leitor);
                return;
            }
            if (opcao.equals("2")) {
                Jogo novoJogo = AuxiliarMenuPrincipal.criarPeloFinal(leitor);
                novoJogo.executar(leitor);
                return;
            }
            if (opcao.equals("3")) {
                return;
            }
            System.out.println("Opcao invalida.");
        }
    }

    public boolean podeInimigoMoverPara(Posicao posicao) {
        return tabuleiro.dentroDosLimites(posicao)
                && !tabuleiro.estaBloqueado(posicao)
                && bombaEm(posicao) == null
                && inimigoEm(posicao) == null;
    }

    public void moverInimigoPara(Inimigo inimigo, Posicao destino) {
        if (destino.mesmaCoordenada(jogador.getPosicao())) {
            inimigo.setPosicao(destino);
            perderUmaVida("Um inimigo encostou em voce.");
            return;
        }
        if (podeInimigoMoverPara(destino)) {
            inimigo.setPosicao(destino);
        }
    }

    public List<Direcao> direcoesEmbaralhadas() {
        List<Direcao> direcoes = new ArrayList<>(List.of(Direcao.values()));
        Collections.shuffle(direcoes, aleatorio);
        return direcoes;
    }

    public boolean temLinhaLivreAteJogador(Posicao origem) {
        return direcaoParaJogadorSeLivre(origem) != null;
    }

    public Direcao direcaoParaJogadorSeLivre(Posicao origem) {
        Posicao posicaoJogador = jogador.getPosicao();
        if (origem.getLinha() == posicaoJogador.getLinha()) {
            int passo = origem.getColuna() < posicaoJogador.getColuna() ? 1 : -1;
            for (int coluna = origem.getColuna() + passo; coluna != posicaoJogador.getColuna(); coluna += passo) {
                if (tabuleiro.estaBloqueado(new Posicao(origem.getLinha(), coluna)) || bombaEm(new Posicao(origem.getLinha(), coluna)) != null) {
                    return null;
                }
            }
            return passo > 0 ? Direcao.DIREITA : Direcao.ESQUERDA;
        }
        if (origem.getColuna() == posicaoJogador.getColuna()) {
            int passo = origem.getLinha() < posicaoJogador.getLinha() ? 1 : -1;
            for (int linha = origem.getLinha() + passo; linha != posicaoJogador.getLinha(); linha += passo) {
                if (tabuleiro.estaBloqueado(new Posicao(linha, origem.getColuna())) || bombaEm(new Posicao(linha, origem.getColuna())) != null) {
                    return null;
                }
            }
            return passo > 0 ? Direcao.BAIXO : Direcao.CIMA;
        }
        return null;
    }

    public Bomba encontrarBombaParaSabotar(Posicao origem) {
        for (Bomba bomba : bombas) {
            int distancia = Math.abs(bomba.getPosicao().getLinha() - origem.getLinha())
                    + Math.abs(bomba.getPosicao().getColuna() - origem.getColuna());
            if (!bomba.getCamuflada() && bomba.getTemporizador() >= 3 && distancia <= 1) {
                return bomba;
            }
        }
        return null;
    }

    public void sabotar(Bomba bomba) {
        bomba.camuflar();
        mensagem = "Um sabotador camuflou uma bomba como caixote.";
    }

    public int getTurno() {
        return turno;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    public List<Inimigo> getInimigos() {
        return inimigos;
    }

    public List<Bomba> getBombas() {
        return bombas;
    }

    public List<Chama> getChamas() {
        return chamas;
    }

    public boolean getDepuracao() {
        return depuracao;
    }

    public void setDepuracao(boolean depuracao) {
        this.depuracao = depuracao;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public void setDificuldade(Dificuldade dificuldade) {
        this.dificuldade = dificuldade;
    }

    public void setTabuleiro(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
        this.tabuleiroInicial = tabuleiro.copia();
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        this.inicioJogador = jogador.getPosicao();
    }

    public void setInimigos(List<Inimigo> inimigos) {
        this.inimigos = inimigos;
        this.inimigosIniciais = copiarInimigos(inimigos);
    }

    public void setBombas(List<Bomba> bombas) {
        this.bombas = bombas;
    }

    public void setChamas(List<Chama> chamas) {
        this.chamas = chamas;
    }

    public void prepararEstadoInicial() {
        this.tabuleiroInicial = tabuleiro.copia();
        this.inimigosIniciais = copiarInimigos(inimigos);
        this.inicioJogador = jogador.getPosicao();
    }

    private void reiniciarFaseMantendoVidas() {
        int vidas = jogador.getVidas();
        int pontos = jogador.getPontos();
        int dicas = jogador.getDicasUsadas();
        jogador = new Jogador(jogador.getNome(), inicioJogador);
        jogador.setVidas(vidas);
        jogador.setPontos(pontos);
        jogador.setDicasUsadas(dicas);
        tabuleiro = tabuleiroInicial.copia();
        inimigos = copiarInimigos(inimigosIniciais);
        bombas.clear();
        chamas.clear();
    }

    private void reiniciarJogoInteiroMesmoMapa() {
        jogador = new Jogador(jogador.getNome(), inicioJogador);
        tabuleiro = tabuleiroInicial.copia();
        inimigos = copiarInimigos(inimigosIniciais);
        bombas.clear();
        chamas.clear();
        finalizado = false;
        venceu = false;
        depuracao = false;
        turno = 0;
        mensagem = "Jogo reiniciado com o mesmo mapa.";
    }

    private List<Inimigo> copiarInimigos(List<Inimigo> origem) {
        List<Inimigo> copia = new ArrayList<>();
        for (Inimigo inimigo : origem) {
            copia.add(inimigo.copia());
        }
        return copia;
    }

    private int bombasAtivasDoJogador() {
        return bombas.size();
    }

    private Inimigo inimigoEm(Posicao posicao) {
        for (Inimigo inimigo : inimigos) {
            if (inimigo.getPosicao().mesmaCoordenada(posicao)) {
                return inimigo;
            }
        }
        return null;
    }

    private Bomba bombaEm(Posicao posicao) {
        for (Bomba bomba : bombas) {
            if (bomba.getPosicao().mesmaCoordenada(posicao)) {
                return bomba;
            }
        }
        return null;
    }

    private boolean chamaEm(Posicao posicao) {
        for (Chama chama : chamas) {
            if (chama.getPosicao().mesmaCoordenada(posicao)) {
                return true;
            }
        }
        return false;
    }

    private void salvarMapaAtual() {
        try {
            Files.createDirectories(CAMINHO_ULTIMO_MAPA.getParent());
            try (BufferedWriter escritor = Files.newBufferedWriter(CAMINHO_ULTIMO_MAPA)) {
                escritor.write("# Ultimo mapa gerado pelo jogo");
                escritor.newLine();
                for (String linhaTexto : tabuleiro.exportarMapaVisivel(inicioJogador, inimigos)) {
                    escritor.write(linhaTexto);
                    escritor.newLine();
                }
            }
        } catch (IOException excecao) {
            mensagem = "Nao foi possivel salvar o mapa gerado em arquivo.";
        }
    }

    public static Jogo vazio() {
        return new Jogo();
    }
}
