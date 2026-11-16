import java.nio.file.Path;
import java.util.Scanner;

public class Principal {
    public static void main(String[] argumentos) {
        Scanner leitor = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("======================================");
            System.out.println(" BOMBERMAN POO - VERSAO TERMINAL");
            System.out.println("======================================");
            System.out.println("Bem-vindo ao Bomberman!");
            System.out.println("1 - Jogar");
            System.out.println("2 - Sair");
            System.out.print("Escolha: ");

            String opcao = leitor.nextLine().trim();
            if (opcao.equals("1")) {
                iniciarJogo(leitor);
            } else if (opcao.equals("2")) {
                System.out.println("Ate a proxima!");
                break;
            } else {
                System.out.println("Opcao invalida.");
            }
        }
    }

    private static void iniciarJogo(Scanner leitor) {
        System.out.println();
        System.out.println("1 - Novo jogo");
        System.out.println("2 - Carregar jogo salvo");
        System.out.print("Escolha: ");
        String opcao = leitor.nextLine().trim();

        Jogo jogo;
        if (opcao.equals("2")) {
            jogo = SalvamentoJogo.carregar(Path.of("dados", "jogo_salvo.txt"));
            if (jogo == null) {
                System.out.println("Nenhum jogo salvo valido encontrado. Criando novo jogo.");
                jogo = criarNovoJogo(leitor);
            }
        } else {
            jogo = criarNovoJogo(leitor);
        }

        jogo.executar(leitor);
    }

    private static Jogo criarNovoJogo(Scanner leitor) {
        System.out.print("Digite seu nome: ");
        String nomeJogador = leitor.nextLine().trim();
        if (nomeJogador.isEmpty()) {
            nomeJogador = "Jogador";
        }

        System.out.println();
        System.out.println("Dificuldade:");
        System.out.println("1 - Facil (sem inimigo sabotador)");
        System.out.println("2 - Normal");
        System.out.println("3 - Dificil");
        System.out.print("Escolha: ");
        String opcaoDificuldade = leitor.nextLine().trim();

        Dificuldade dificuldade = Dificuldade.NORMAL;
        if (opcaoDificuldade.equals("1")) {
            dificuldade = Dificuldade.FACIL;
        } else if (opcaoDificuldade.equals("3")) {
            dificuldade = Dificuldade.DIFICIL;
        }

        return Jogo.novoJogo(nomeJogador, dificuldade);
    }
}

