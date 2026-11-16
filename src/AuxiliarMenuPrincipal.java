import java.util.Scanner;

class AuxiliarMenuPrincipal {
    public static Jogo criarPeloFinal(Scanner leitor) {
        System.out.print("Digite seu nome: ");
        String nome = leitor.nextLine().trim();
        if (nome.isEmpty()) {
            nome = "Jogador";
        }
        System.out.println("1 - Facil | 2 - Normal | 3 - Dificil");
        System.out.print("Dificuldade: ");
        String opcao = leitor.nextLine().trim();
        Dificuldade dificuldade = Dificuldade.NORMAL;
        if (opcao.equals("1")) {
            dificuldade = Dificuldade.FACIL;
        } else if (opcao.equals("3")) {
            dificuldade = Dificuldade.DIFICIL;
        }
        return Jogo.novoJogo(nome, dificuldade);
    }
}