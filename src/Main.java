import auxiliares.Teclado;
import visao.MenuUsuarios;
import visao.ControleCurso;
import entidades.Usuario;
import visao.ControleInscricao;

public class Main {
    public static void main(String[] args) {
        try {
            MenuUsuarios menuUsuarios = new MenuUsuarios();
            ControleCurso controleCurso = new ControleCurso();
            ControleInscricao controleInscricao = new ControleInscricao();

            // Tela de Login
            Usuario usuarioLogado = menuUsuarios.telaInicial();
            if (usuarioLogado == null) {
                System.out.println("\nAté logo!");
                return;
            }

            // Menu principal
            String op;
            do {
                System.out.println("\n\nEntrePares 1.0");
                System.out.println("--------------");
                System.out.println("> Início");
                System.out.println("\nOlá, " + usuarioLogado.getNome() + "!");
                System.out.println("\n(A) Meus dados");
                System.out.println("(B) Meus cursos");
                System.out.println("(C) Minhas inscrições");
                System.out.println("\n(S) Sair");
                System.out.print("\nOpção: ");
                op = Teclado.lerLinha().trim().toUpperCase();

                switch (op) {
                    case "A":
                        menuUsuarios.menu(usuarioLogado);
                        break;
                    case "B":
                        controleCurso.menu(usuarioLogado);
                        break;
                    case "C":
                        controleInscricao.menu(usuarioLogado);
                        break;
                    case "S":
                        System.out.println("\nAté logo, " + usuarioLogado.getNome() + "!");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } while (!op.equals("S"));

        } catch (Exception e) {
            System.out.println("Erro do sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}