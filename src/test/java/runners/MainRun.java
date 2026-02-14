package runners;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Punto de entrada para correr la suite desde el IDE con un click.
 * Si algo falla, sale con código 1.
 */
public final class MainRun {

    /**
     * Clase utilitaria, no se instancia.
     */
    private MainRun() {
    }

    /**
     * Ejecuta `ContactListTestSuite`.
     * Si llega un argumento, se usa como filtro de tags de Cucumber.
     *
     * @param args args[0] opcional, por ejemplo: "@smoke and @api"
     */
    public static void main(String[] args) {
        if (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) {
            System.setProperty("cucumber.filter.tags", args[0]);
        }

        JUnitCore junitCore = new JUnitCore();
        junitCore.addListener(new TextListener(System.out));

        Result result = junitCore.run(ContactListTestSuite.class);
        if (!result.wasSuccessful()) {
            System.exit(1);
        }
    }
}
