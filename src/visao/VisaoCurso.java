package visao;

import auxiliares.Teclado;
import entidades.Curso;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VisaoCurso {

    public Curso leCurso(int idUsuario) {
        System.out.println("\nCADASTRO DE NOVO CURSO");
        System.out.println("======================");
        
        // Nome do curso
        String nome;
        do {
            System.out.print("Nome do curso: ");
            nome = Teclado.lerLinha().trim();
            if (nome.isEmpty()) {
                System.out.println("Nome não pode ser vazio!");
            }
        } while (nome.isEmpty());
        
        // Data de início
        LocalDate dataInicio = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        do {
            System.out.print("Data de início (dd/MM/yyyy): ");
            String dataStr = Teclado.lerLinha().trim();
            try {
                dataInicio = LocalDate.parse(dataStr, formatter);
                if (dataInicio.isBefore(LocalDate.now())) {
                    System.out.println("Data de início deve ser futura!");
                    dataInicio = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Use o formato dd/MM/yyyy");
            }
        } while (dataInicio == null);
        
        // Descrição
        String descricao;
        do {
            System.out.println("Descrição do curso (programa, dias, locais, etc.):");
            descricao = Teclado.lerLinha().trim();
            if (descricao.isEmpty()) {
                System.out.println("Descrição não pode ser vazia!");
            }
        } while (descricao.isEmpty());
        
        return new Curso(idUsuario, nome, dataInicio, descricao);
    }
    
    public Curso alteraCurso(Curso curso) {
        System.out.println("\nALTERAÇÃO DE CURSO");
        System.out.println("==================");
        
        // Nome do curso
        System.out.print("Nome do curso [" + curso.getNome() + "]: ");
        String nome = Teclado.lerLinha().trim();
        if (!nome.isEmpty()) {
            curso.setNome(nome);
        }
        
        // Data de início
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.print("Data de início [" + curso.getDataInicio().format(formatter) + "]: ");
        String dataStr = Teclado.lerLinha().trim();
        if (!dataStr.isEmpty()) {
            try {
                LocalDate novaData = LocalDate.parse(dataStr, formatter);
                if (novaData.isBefore(LocalDate.now())) {
                    System.out.println("Data de início deve ser futura! Mantendo data anterior.");
                } else {
                    curso.setDataInicio(novaData);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Mantendo data anterior.");
            }
        }
        
        // Descrição
        System.out.println("Descrição do curso [" + curso.getDescricao() + "]:");
        String descricao = Teclado.lerLinha().trim();
        if (!descricao.isEmpty()) {
            curso.setDescricao(descricao);
        }
        
        return curso;
    }
    
    // Exibe os dados do curso no formato da visão do PROPRIETÁRIO (Meus Cursos)
    public void mostraCurso(Curso curso) {
        System.out.println("\n" + "=".repeat(50));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        System.out.println("CÓDIGO........: " + curso.getCodigoCompartilhavel());
        System.out.println("NOME..........: " + curso.getNome());
        System.out.println("DESCRIÇÃO.....: " + curso.getDescricao());
        System.out.println("DATA DE INÍCIO: " + curso.getDataInicio().format(formatter));
        System.out.println();
        
        switch (curso.getEstado()) {
            case Curso.ATIVO_INSCRICOES:
                System.out.println("Este curso está aberto para inscrições!");
                break;
            case Curso.ATIVO_SEM_INSCRICOES:
                System.out.println("Este curso já iniciou e não aceita mais inscrições.");
                break;
            case Curso.CONCLUIDO:
                System.out.println("Este curso foi concluído.");
                break;
            case Curso.CANCELADO:
                System.out.println("Este curso foi cancelado.");
                break;
        }
        System.out.println("=".repeat(50));
    }

    // Exibe os dados do curso no formato da visão de INSCRIÇÃO (Minhas inscrições)
    public void mostraCursoInscricao(Curso curso, String nomeAutor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("CÓDIGO........: " + curso.getCodigoCompartilhavel());
        System.out.println("CURSO.........: " + curso.getNome());
        System.out.println("AUTOR.........: " + nomeAutor);
        System.out.println("DESCRIÇÃO.....: " + curso.getDescricao());
        System.out.println("DATA DE INÍCIO: " + curso.getDataInicio().format(formatter));
    }
    
    public boolean confirmaExclusao(Curso curso) {
        System.out.println("\nTem certeza que deseja excluir o curso '" + curso.getNome() + "'? (s/N): ");
        String resp = Teclado.lerLinha().trim().toLowerCase();
        return resp.equals("s") || resp.equals("sim");
    }
    
    public void mensagemSucesso(String operacao) {
        System.out.println("\n[OK] " + operacao + " com sucesso!");
    }
    
    public void mensagemErro(String mensagem) {
        System.out.println("\n[ERRO] " + mensagem);
    }
    
    public int lerOpcaoMenu() {
        System.out.print("\nOpção: ");
        try {
            String input = Teclado.lerLinha().trim();
            if (input.isEmpty()) return -1;
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public String lerOpcaoTexto() {
        System.out.print("\nOpção: ");
        return Teclado.lerLinha().trim().toUpperCase();
    }
}