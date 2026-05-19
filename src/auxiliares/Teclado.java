package auxiliares;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Scanner único compartilhado por toda a aplicação.
 * Só para evitar conflitos quando tiverem muitas clases usando o System.in.
 */
public class Teclado {

    private static final Scanner sc = new Scanner(System.in);

    /** Lê uma linha; retorna "" em caso de EOF ou erro de stream. */
    public static String lerLinha() {
        try {
            return sc.hasNextLine() ? sc.nextLine() : "";
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
