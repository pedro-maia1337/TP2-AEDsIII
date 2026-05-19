package visao;

import java.util.Scanner;
import java.util.ArrayList;
import arquivo.ArquivoUsuario;
import arquivo.ArquivoCurso;
import arquivo.ArquivoInscricao;
import auxiliares.Teclado;
import entidades.Usuario;
import entidades.Curso;

//Tela inicial de login

public class MenuUsuarios {

    private ArquivoUsuario arqUsuarios;
    private ArquivoCurso arqCursos;
    private ArquivoInscricao arqInscricoes;

    public MenuUsuarios() throws Exception {
        arqUsuarios  = new ArquivoUsuario();
        arqCursos    = new ArquivoCurso();
        arqInscricoes = new ArquivoInscricao();
    }

    public Usuario telaInicial() {
        String opcao;
        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("\n(A) Login");
            System.out.println("(B) Novo usuário");
            System.out.println("(C) Esqueci minha senha");
            System.out.println("\n(S) Sair");
            System.out.print("\nOpção: ");
            opcao = Teclado.lerLinha().trim().toUpperCase();

            switch (opcao) {
                case "A":
                    Usuario u = login();
                    if (u != null) return u;
                    break;
                case "B":
                    incluirUsuario();
                    break;
                case "C":
                    recuperarSenha();
                    break;
                case "S":
                    return null;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (true);
    }

    //Exibido após o login

    public void menu(Usuario usuarioLogado) {
        String opcao;
        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Meus dados");
            System.out.println("\n(1) Ver meus dados");
            System.out.println("(2) Alterar meus dados");
            System.out.println("(3) Excluir minha conta");
            System.out.println("\n(0) Voltar");
            System.out.print("\nOpção: ");
            opcao = Teclado.lerLinha().trim();

            switch (opcao) {
                case "1":
                    buscarUsuario(usuarioLogado.getId());
                    break;
                case "2":
                    alterarUsuario(usuarioLogado);
                    break;
                case "3":
                    if (excluirUsuario(usuarioLogado)) return; // conta excluída, sai do menu
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (!opcao.equals("0"));
    }

    //Login

    private Usuario login() {
        System.out.println("\nLogin");
        System.out.print("E-mail: ");
        String email = Teclado.lerLinha().trim();
        if (email.isEmpty()) return null;

        System.out.print("Senha: ");
        String senha = Teclado.lerLinha();

        try {
            Usuario u = arqUsuarios.readByEmail(email);
            if (u != null && u.verificaSenha(senha)) {
                System.out.println("\nBem-vindo(a), " + u.getNome() + "!");
                return u;
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema: " + e.getMessage());
        }
        System.out.println("E-mail ou senha incorretos.");
        return null;
    }

    // CRUD

    private void buscarUsuario(int id) {
        try {
            Usuario u = arqUsuarios.read(id);
            if (u != null) mostraUsuario(u);
            else           System.out.println("Usuário não encontrado.");
        } catch (Exception e) {
            System.out.println("Erro do sistema: " + e.getMessage());
        }
    }

    private void incluirUsuario() {
        System.out.println("\nNovo usuário");

        String nome = leNome();
        if (nome == null) return;

        String email = leEmail();
        if (email == null) return;

        String senha = leSenha();
        if (senha == null) return;

        System.out.print("Pergunta secreta: ");
        String pergunta = Teclado.lerLinha().trim();
        if (pergunta.isEmpty()) { System.out.println("Cancelado."); return; }

        System.out.print("Resposta secreta: ");
        String resposta = Teclado.lerLinha().trim();
        if (resposta.isEmpty()) { System.out.println("Cancelado."); return; }

        System.out.print("\nConfirma o cadastro? (S/N) ");
        if (!Teclado.lerLinha().trim().equalsIgnoreCase("S")) {
            System.out.println("Cadastro cancelado.");
            return;
        }

        try {
            arqUsuarios.create(new Usuario(nome, email, senha, pergunta, resposta));
            System.out.println("Usuário cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void alterarUsuario(Usuario usuarioLogado) {
        try {
            Usuario u = arqUsuarios.read(usuarioLogado.getId());
            if (u == null) { System.out.println("Usuário não encontrado."); return; }

            System.out.println("\nDados atuais:");
            mostraUsuario(u);
            System.out.println("(Deixe em branco para manter o valor atual)");

            System.out.print("\nNovo nome [" + u.getNome() + "]: ");
            String novoNome = Teclado.lerLinha().trim();
            if (!novoNome.isEmpty()) {
                if (novoNome.length() < 4) { System.out.println("Nome deve ter mín. 4 caracteres. Mantido."); }
                else u.setNome(novoNome);
            }

            System.out.print("Novo e-mail [" + u.getEmail() + "]: ");
            String novoEmail = Teclado.lerLinha().trim();
            if (!novoEmail.isEmpty()) u.setEmail(novoEmail);

            System.out.print("Nova senha (vazio=manter): ");
            String novaSenha = Teclado.lerLinha();
            if (!novaSenha.isEmpty()) {
                if (novaSenha.length() < 6) System.out.println("Senha curta demais. Mantida.");
                else u.setSenha(novaSenha);
            }

            System.out.print("Nova pergunta secreta (vazio=manter): ");
            String novaPergunta = Teclado.lerLinha().trim();
            if (!novaPergunta.isEmpty()) {
                System.out.print("Nova resposta secreta: ");
                String novaResposta = Teclado.lerLinha().trim();
                if (novaResposta.isEmpty()) { System.out.println("Resposta não pode ser vazia. Cancelado."); return; }
                u.setPerguntaSecreta(novaPergunta);
                u.setRespostaSecreta(novaResposta);
            }

            System.out.print("\nConfirma as alterações? (S/N) ");
            if (!Teclado.lerLinha().trim().equalsIgnoreCase("S")) {
                System.out.println("Alteração cancelada.");
                return;
            }

            if (arqUsuarios.update(u)) {
                usuarioLogado.setNome(u.getNome());
                usuarioLogado.setEmail(u.getEmail());
                System.out.println("Dados alterados com sucesso.");
            } else {
                System.out.println("Não foi possível alterar os dados.");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private boolean excluirUsuario(Usuario usuarioLogado) {
        System.out.println("\nATENÇÃO: Esta ação é irreversível!");
        
        try {
            // Verifica se o usuário possui cursos
            ArrayList<Curso> cursos = arqCursos.readByUsuario(usuarioLogado.getId());
            ArrayList<Curso> cursosAtivos = new ArrayList<>();
            
            // Filtra cursos ativos
            for (Curso curso : cursos) {
                if (curso.getEstado() == Curso.ATIVO_INSCRICOES || curso.getEstado() == Curso.ATIVO_SEM_INSCRICOES) {
                    cursosAtivos.add(curso);
                }
            }
            
            if (!cursosAtivos.isEmpty()) {
                System.out.println("\nNão é possível excluir sua conta pois você possui cursos ativos:");
                for (Curso curso : cursosAtivos) {
                    System.out.println("- " + curso.getNome() + " (" + curso.getEstadoTexto() + ")");
                }
                System.out.println("\nCancele ou conclua estes cursos antes de excluir sua conta.");
                return false;
            }
            
            System.out.print("Confirma a exclusão da sua conta? (S/N) ");
            if (!Teclado.lerLinha().trim().equalsIgnoreCase("S")) {
                System.out.println("Exclusão cancelada.");
                return false;
            }
            
            // Remove cursos inativos (concluídos/cancelados) do usuário
            for (Curso curso : cursos) {
                if (curso.getEstado() == Curso.CONCLUIDO || curso.getEstado() == Curso.CANCELADO) {
                    arqCursos.delete(curso.getId());
                }
            }

            // Cancela todas as inscrições ativas do usuário
            arqInscricoes.cancelarInscricoesPorUsuario(usuarioLogado.getId());

            // Remove o usuário
            if (arqUsuarios.delete(usuarioLogado.getId())) {
                System.out.println("Conta excluída. Até logo!");
                return true;
            }
            System.out.println("Não foi possível excluir a conta.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        return false;
    }

    private void recuperarSenha() {
        System.out.println("\nRecuperação de senha");
        System.out.print("E-mail cadastrado: ");
        String email = Teclado.lerLinha().trim();

        try {
            Usuario u = arqUsuarios.readByEmail(email);
            if (u == null) { System.out.println("E-mail não encontrado."); return; }

            System.out.println("Pergunta: " + u.getPerguntaSecreta());
            System.out.print("Resposta: ");
            String resposta = Teclado.lerLinha();

            if (!u.verificaResposta(resposta)) { System.out.println("Resposta incorreta."); return; }

            System.out.print("Nova senha (mín. 6 caracteres): ");
            String nova = Teclado.lerLinha();
            if (nova.length() < 6) { System.out.println("Senha muito curta."); return; }

            System.out.print("Confirme a nova senha: ");
            if (!nova.equals(Teclado.lerLinha())) { System.out.println("Senhas não conferem."); return; }

            u.setSenha(nova);
            if (arqUsuarios.update(u)) System.out.println("Senha alterada com sucesso!");
            else                        System.out.println("Erro ao salvar nova senha.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // Auxiliares

    private void mostraUsuario(Usuario u) {
        System.out.println("\n--------------------------");
        System.out.printf("ID.........: %d%n",  u.getId());
        System.out.printf("Nome.......: %s%n",  u.getNome());
        System.out.printf("E-mail.....: %s%n",  u.getEmail());
        System.out.printf("Pergunta...: %s%n",  u.getPerguntaSecreta());
        System.out.println("--------------------------");
    }

    private String leNome() {
        while (true) {
            System.out.print("Nome (mín. 4 letras, vazio=cancelar): ");
            String v = Teclado.lerLinha().trim();
            if (v.isEmpty()) return null;
            if (v.length() >= 4) return v;
            System.out.println("Nome deve ter pelo menos 4 caracteres.");
        }
    }

    private String leEmail() {
        while (true) {
            System.out.print("E-mail (vazio=cancelar): ");
            String v = Teclado.lerLinha().trim();
            if (v.isEmpty()) return null;
            if (v.contains("@") && v.contains(".")) return v.toLowerCase();
            System.out.println("E-mail inválido.");
        }
    }

    private String leSenha() {
        while (true) {
            System.out.print("Senha (mín. 6 caracteres): ");
            String v = Teclado.lerLinha();
            if (v.isEmpty()) { System.out.println("Cancelado."); return null; }
            if (v.length() >= 6) return v;
            System.out.println("Senha deve ter pelo menos 6 caracteres.");
        }
    }
}
