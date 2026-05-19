package auxiliares;

import java.io.*;

// Par (idCurso, idInscrição) para o índice B+ de inscrições por curso.

public class ParCursoIdInscricaoId implements InterfaceArvoreBMais<ParCursoIdInscricaoId>,
                                               Comparable<ParCursoIdInscricaoId> {

    private int idCurso;
    private int idInscricao;
    private short TAMANHO = 8; // 4 bytes + 4 bytes

    public ParCursoIdInscricaoId() {
        this(-1, -1);
    }

    public ParCursoIdInscricaoId(int idCurso, int idInscricao) {
        this.idCurso     = idCurso;
        this.idInscricao = idInscricao;
    }

    public int getIdCurso()     { return idCurso; }
    public int getIdInscricao() { return idInscricao; }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idCurso);
        dos.writeInt(idInscricao);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idCurso     = dis.readInt();
        idInscricao = dis.readInt();
    }

    // Ordena por idCurso; desempata por idInscricao
    @Override
    public int compareTo(ParCursoIdInscricaoId obj) {
        if (this.idCurso != obj.idCurso)
            return Integer.compare(this.idCurso, obj.idCurso);
        return Integer.compare(this.idInscricao, obj.idInscricao);
    }

    @Override
    public ParCursoIdInscricaoId clone() {
        return new ParCursoIdInscricaoId(this.idCurso, this.idInscricao);
    }

    @Override
    public String toString() {
        return "Curso: " + idCurso + " | Inscrição: " + idInscricao;
    }
}
