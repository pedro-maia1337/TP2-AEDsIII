package arquivo;

import auxiliares.Arquivo;
import auxiliares.ArvoreBMais;
import auxiliares.ParUsuarioIdInscricaoId;
import auxiliares.ParCursoIdInscricaoId;
import entidades.Inscricao;
import java.util.ArrayList;

public class ArquivoInscricao extends Arquivo<Inscricao> {

    // Índice B+: idUsuario → idInscrição  (buscar "minhas inscrições")
    private ArvoreBMais<ParUsuarioIdInscricaoId> indiceUsuarioInscricao;
    // Índice B+: idCurso → idInscrição  (buscar inscritos de um curso)
    private ArvoreBMais<ParCursoIdInscricaoId>   indiceCursoInscricao;

    public ArquivoInscricao() throws Exception {
        super("inscricoes", Inscricao.class.getConstructor());
        indiceUsuarioInscricao = new ArvoreBMais<>(
            ParUsuarioIdInscricaoId.class.getConstructor(), 5,
            ".\\dados\\inscricoes\\indiceUsuarioInscricao.db"
        );
        indiceCursoInscricao = new ArvoreBMais<>(
            ParCursoIdInscricaoId.class.getConstructor(), 5,
            ".\\dados\\inscricoes\\indiceCursoInscricao.db"
        );
    }

    @Override
    public int create(Inscricao i) throws Exception {
        int id = super.create(i);
        indiceUsuarioInscricao.create(new ParUsuarioIdInscricaoId(i.getIdUsuario(), id));
        indiceCursoInscricao.create(new ParCursoIdInscricaoId(i.getIdCurso(), id));
        return id;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Inscricao i = super.read(id);
        if (i == null) return false;

        if (super.delete(id)) {
            indiceUsuarioInscricao.delete(new ParUsuarioIdInscricaoId(i.getIdUsuario(), id));
            indiceCursoInscricao.delete(new ParCursoIdInscricaoId(i.getIdCurso(), id));
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Inscricao nova) throws Exception {
        Inscricao antiga = super.read(nova.getId());
        if (antiga == null) return false;

        if (super.update(nova)) {
            if (nova.getIdUsuario() != antiga.getIdUsuario()) {
                indiceUsuarioInscricao.delete(
                    new ParUsuarioIdInscricaoId(antiga.getIdUsuario(), nova.getId()));
                indiceUsuarioInscricao.create(
                    new ParUsuarioIdInscricaoId(nova.getIdUsuario(), nova.getId()));
            }
            if (nova.getIdCurso() != antiga.getIdCurso()) {
                indiceCursoInscricao.delete(
                    new ParCursoIdInscricaoId(antiga.getIdCurso(), nova.getId()));
                indiceCursoInscricao.create(
                    new ParCursoIdInscricaoId(nova.getIdCurso(), nova.getId()));
            }
            return true;
        }
        return false;
    }

    // Busca todas as inscrições ATIVAS de um usuário
    public ArrayList<Inscricao> readByUsuario(int idUsuario) throws Exception {
        ArrayList<Inscricao> inscricoes = new ArrayList<>();
        ArrayList<ParUsuarioIdInscricaoId> todosPares = indiceUsuarioInscricao.read(null);
        for (ParUsuarioIdInscricaoId par : todosPares) {
            if (par.getIdUsuario() == idUsuario) {
                Inscricao inscricao = super.read(par.getIdInscricao());
                if (inscricao != null && inscricao.getEstado() == Inscricao.ATIVA) {
                    inscricoes.add(inscricao);
                }
            }
        }
        return inscricoes;
    }

    // Busca todas as inscrições ATIVAS de um curso
    public ArrayList<Inscricao> readByCurso(int idCurso) throws Exception {
        ArrayList<Inscricao> inscricoes = new ArrayList<>();
        ArrayList<ParCursoIdInscricaoId> todosPares = indiceCursoInscricao.read(null);
        for (ParCursoIdInscricaoId par : todosPares) {
            if (par.getIdCurso() == idCurso) {
                Inscricao inscricao = super.read(par.getIdInscricao());
                if (inscricao != null && inscricao.getEstado() == Inscricao.ATIVA) {
                    inscricoes.add(inscricao);
                }
            }
        }
        return inscricoes;
    }

    // Verifica se o usuário já está inscrito (ativo) em determinado curso
    public boolean existeInscricao(int idUsuario, int idCurso) throws Exception {
        ArrayList<Inscricao> inscricoes = readByUsuario(idUsuario);
        for (Inscricao i : inscricoes) {
            if (i.getIdCurso() == idCurso) {
                return true;
            }
        }
        return false;
    }

    // Cancela todas as inscrições ativas de um curso (integridade ao cancelar curso)
    public void cancelarInscricoesPorCurso(int idCurso) throws Exception {
        ArrayList<Inscricao> inscricoes = readByCurso(idCurso);
        for (Inscricao i : inscricoes) {
            i.setEstado(Inscricao.CANCELADA);
            super.update(i);
        }
    }

    // Cancela todas as inscrições ativas de um usuário (integridade ao excluir conta)
    public void cancelarInscricoesPorUsuario(int idUsuario) throws Exception {
        ArrayList<Inscricao> inscricoes = readByUsuario(idUsuario);
        for (Inscricao i : inscricoes) {
            i.setEstado(Inscricao.CANCELADA);
            super.update(i);
        }
    }
}
