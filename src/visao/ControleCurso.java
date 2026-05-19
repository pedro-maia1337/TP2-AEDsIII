package visao;

import arquivo.ArquivoCurso;
import arquivo.ArquivoInscricao;
import arquivo.ArquivoUsuario;
import auxiliares.Teclado;
import entidades.Curso;
import entidades.Inscricao;
import entidades.Usuario;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ControleCurso {

    private ArquivoCurso arquivoCurso;
    private ArquivoInscricao arquivoInscricao;
    private ArquivoUsuario arquivoUsuario;
    private VisaoCurso visao;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ControleCurso() throws Exception {
        arquivoCurso     = new ArquivoCurso();
        arquivoInscricao = new ArquivoInscricao();
        arquivoUsuario   = new ArquivoUsuario();
        visao            = new VisaoCurso();
    }

    public void menu(Usuario usuarioLogado) {
        String opcao;
        do {
            try {
                exibirMenuPrincipal(usuarioLogado);
                opcao = visao.lerOpcaoTexto();

                switch (opcao) {
                    case "A":
                        criarCurso(usuarioLogado);
                        break;
                    case "R":
                        return; // Retorna ao menu anterior
                    default:
                        // Tenta interpretar como número (seleção de curso)
                        try {
                            int numeroCurso = Integer.parseInt(opcao);
                            selecionarCurso(usuarioLogado, numeroCurso);
                        } catch (NumberFormatException e) {
                            System.out.println("Opção inválida!");
                        }
                        break;
                }
            } catch (Exception e) {
                visao.mensagemErro("Erro do sistema: " + e.getMessage());
            }
        } while (true);
    }

    private void exibirMenuPrincipal(Usuario usuarioLogado) throws Exception {
        System.out.println("\n\nEntrePares 1.0");
        System.out.println("--------------");
        System.out.println("> Início > Meus cursos");
        System.out.println();

        // Busca cursos do usuário ordenados por nome
        ArrayList<Curso> cursos = arquivoCurso.readByUsuario(usuarioLogado.getId());
        cursos.sort((c1, c2) -> c1.getNome().compareToIgnoreCase(c2.getNome()));

        System.out.println("CURSOS");
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
        } else {
            for (int i = 0; i < cursos.size(); i++) {
                Curso curso = cursos.get(i);
                System.out.printf("(%d) %s - %02d/%02d/%d%n", 
                    i + 1, 
                    curso.getNome(), 
                    curso.getDataInicio().getDayOfMonth(),
                    curso.getDataInicio().getMonthValue(),
                    curso.getDataInicio().getYear());
            }
        }

        System.out.println("\n(A) Novo curso");
        System.out.println("(R) Retornar ao menu anterior");
    }

    private void criarCurso(Usuario usuarioLogado) {
        try {
            Curso novoCurso = visao.leCurso(usuarioLogado.getId());
            int id = arquivoCurso.create(novoCurso);
            visao.mensagemSucesso("Curso cadastrado com ID " + id);
        } catch (Exception e) {
            visao.mensagemErro("Não foi possível cadastrar o curso: " + e.getMessage());
        }
    }

    private void selecionarCurso(Usuario usuarioLogado, int numeroCurso) throws Exception {
        ArrayList<Curso> cursos = arquivoCurso.readByUsuario(usuarioLogado.getId());
        cursos.sort((c1, c2) -> c1.getNome().compareToIgnoreCase(c2.getNome()));

        if (numeroCurso < 1 || numeroCurso > cursos.size()) {
            System.out.println("Número de curso inválido!");
            return;
        }

        Curso curso = cursos.get(numeroCurso - 1);
        menuCurso(curso);
    }

    private void menuCurso(Curso curso) {
        String opcao;
        do {
            try {
                exibirMenuCurso(curso);
                opcao = visao.lerOpcaoTexto();

                switch (opcao) {
                    case "A":
                        gerenciarInscritos(curso);
                        break;
                    case "B":
                        alterarCurso(curso);
                        break;
                    case "C":
                        encerrarInscricoes(curso);
                        break;
                    case "D":
                        concluirCurso(curso);
                        break;
                    case "E":
                        cancelarCurso(curso);
                        break;
                    case "R":
                        return; // Retorna ao menu anterior
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
            } catch (Exception e) {
                visao.mensagemErro("Erro do sistema: " + e.getMessage());
            }
        } while (true);
    }

    private void exibirMenuCurso(Curso curso) {
        System.out.println("\n\nEntrePares 1.0");
        System.out.println("--------------");
        System.out.println("> Início > Meus Cursos > " + curso.getNome());
        
        visao.mostraCurso(curso);
        
        System.out.println("\n(A) Gerenciar inscritos no curso");
        System.out.println("(B) Corrigir dados do curso");
        
        if (curso.getEstado() == Curso.ATIVO_INSCRICOES) {
            System.out.println("(C) Encerrar inscrições");
        }
        
        if (curso.getEstado() == Curso.ATIVO_INSCRICOES || curso.getEstado() == Curso.ATIVO_SEM_INSCRICOES) {
            System.out.println("(D) Concluir curso");
        }
        
        if (curso.getEstado() != Curso.CANCELADO && curso.getEstado() != Curso.CONCLUIDO) {
            System.out.println("(E) Cancelar curso");
        }
        
        System.out.println("\n(R) Retornar ao menu anterior");
    }

    private void alterarCurso(Curso curso) {
        try {
            Curso cursoAlterado = visao.alteraCurso(curso);
            if (arquivoCurso.update(cursoAlterado)) {
                visao.mensagemSucesso("Curso alterado");
            } else {
                visao.mensagemErro("Não foi possível alterar o curso");
            }
        } catch (Exception e) {
            visao.mensagemErro("Erro ao alterar curso: " + e.getMessage());
        }
    }

    private void encerrarInscricoes(Curso curso) {
        try {
            if (curso.getEstado() == Curso.ATIVO_INSCRICOES) {
                curso.setEstado(Curso.ATIVO_SEM_INSCRICOES);
                if (arquivoCurso.update(curso)) {
                    visao.mensagemSucesso("Inscrições encerradas para o curso");
                } else {
                    visao.mensagemErro("Não foi possível encerrar as inscrições");
                }
            } else {
                visao.mensagemErro("O curso não está recebendo inscrições");
            }
        } catch (Exception e) {
            visao.mensagemErro("Erro ao encerrar inscrições: " + e.getMessage());
        }
    }

    private void concluirCurso(Curso curso) {
        try {
            if (curso.getEstado() == Curso.ATIVO_INSCRICOES || curso.getEstado() == Curso.ATIVO_SEM_INSCRICOES) {
                curso.setEstado(Curso.CONCLUIDO);
                if (arquivoCurso.update(curso)) {
                    visao.mensagemSucesso("Curso marcado como concluído");
                } else {
                    visao.mensagemErro("Não foi possível concluir o curso");
                }
            } else {
                visao.mensagemErro("O curso não pode ser concluído no estado atual");
            }
        } catch (Exception e) {
            visao.mensagemErro("Erro ao concluir curso: " + e.getMessage());
        }
    }

    private void cancelarCurso(Curso curso) {
        try {
            if (visao.confirmaExclusao(curso)) {
                // Cancela todas as inscrições ativas antes de cancelar o curso
                arquivoInscricao.cancelarInscricoesPorCurso(curso.getId());
                curso.setEstado(Curso.CANCELADO);
                if (arquivoCurso.update(curso)) {
                    visao.mensagemSucesso("Curso cancelado");
                } else {
                    visao.mensagemErro("Não foi possível cancelar o curso");
                }
            }
        } catch (Exception e) {
            visao.mensagemErro("Erro ao cancelar curso: " + e.getMessage());
        }
    }

    // ──────────────────── GERENCIAR INSCRITOS ────────────────────────────────

    private void gerenciarInscritos(Curso curso) {
        boolean refresh = true;
        while (refresh) {
            refresh = false;
            try {
                ArrayList<Inscricao> inscricoes = arquivoInscricao.readByCurso(curso.getId());

                // Monta pares {Inscricao, Usuario} e ordena por nome
                ArrayList<Object[]> inscritos = new ArrayList<>();
                for (Inscricao i : inscricoes) {
                    Usuario u = arquivoUsuario.read(i.getIdUsuario());
                    if (u != null) inscritos.add(new Object[]{i, u});
                }
                inscritos.sort((a, b) ->
                    ((Usuario) a[1]).getNome().compareToIgnoreCase(((Usuario) b[1]).getNome()));

                System.out.println("\n\nEntrePares 1.0");
                System.out.println("--------------");
                System.out.println("> Início > Meus cursos > " + curso.getNome() + " > Inscrições");
                System.out.println();

                if (inscritos.isEmpty()) {
                    System.out.println("Nenhum inscrito neste curso.");
                } else {
                    for (int i = 0; i < inscritos.size(); i++) {
                        Inscricao insc = (Inscricao) inscritos.get(i)[0];
                        Usuario u      = (Usuario)    inscritos.get(i)[1];
                        System.out.printf("(%d) %s (%s)%n",
                            i + 1, u.getNome(), insc.getDataInscricao().format(FMT));
                    }
                }

                System.out.println("\n(A) Exportar lista");
                System.out.println("\n(R) Retornar ao menu anterior");
                System.out.print("\nOpção: ");
                String opcao = Teclado.lerLinha().trim().toUpperCase();

                if (opcao.equals("A")) {
                    exportarListaCSV(curso, inscritos);
                    refresh = true;
                } else if (opcao.equals("R")) {
                    return;
                } else {
                    try {
                        int num = Integer.parseInt(opcao);
                        if (!inscritos.isEmpty() && num >= 1 && num <= inscritos.size()) {
                            Inscricao insc = (Inscricao) inscritos.get(num - 1)[0];
                            Usuario u      = (Usuario)    inscritos.get(num - 1)[1];
                            telaDetalheInscrito(curso, insc, u);
                            refresh = true;
                        } else {
                            System.out.println("Opção inválida!");
                            refresh = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Opção inválida!");
                        refresh = true;
                    }
                }
            } catch (Exception e) {
                visao.mensagemErro("Erro ao listar inscritos: " + e.getMessage());
            }
        }
    }

    private void telaDetalheInscrito(Curso curso, Inscricao insc, Usuario u) {
        String opcao;
        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Meus cursos > " + curso.getNome()
                + " > Inscrições > " + u.getNome());
            System.out.println();
            System.out.println("NOME...........: " + u.getNome());
            System.out.println("E-MAIL.........: " + u.getEmail());
            System.out.println("DATA INSCRIÇÃO.: " + insc.getDataInscricao().format(FMT));
            System.out.println();
            System.out.println("(A) Cancelar inscrição deste usuário");
            System.out.println("\n(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");
            opcao = Teclado.lerLinha().trim().toUpperCase();

            if (opcao.equals("A")) {
                try {
                    insc.setEstado(Inscricao.CANCELADA);
                    if (arquivoInscricao.update(insc)) {
                        visao.mensagemSucesso("Inscrição de " + u.getNome() + " cancelada");
                    } else {
                        visao.mensagemErro("Não foi possível cancelar a inscrição");
                    }
                } catch (Exception e) {
                    visao.mensagemErro("Erro: " + e.getMessage());
                }
                return;
            } else if (opcao.equals("R")) {
                return;
            } else {
                System.out.println("Opção inválida!");
            }
        } while (true);
    }

    private void exportarListaCSV(Curso curso, ArrayList<Object[]> inscritos) {
        String nomeArquivo = "inscritos_" + curso.getCodigoCompartilhavel() + ".csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("Nome,Email,Data de Inscricao");
            for (Object[] par : inscritos) {
                Inscricao insc = (Inscricao) par[0];
                Usuario u      = (Usuario)   par[1];
                writer.printf("\"%s\",\"%s\",\"%s\"%n",
                    u.getNome(), u.getEmail(), insc.getDataInscricao().format(FMT));
            }
            visao.mensagemSucesso("Lista exportada para " + nomeArquivo);
        } catch (Exception e) {
            visao.mensagemErro("Erro ao exportar CSV: " + e.getMessage());
        }
    }
}