package runners;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
    Usa el runner principal de Cucumber + Serenity y devuelve exit code 1 si falla.
 */
public final class MainRun {

    /**
      evita instanciacion.
     */
    private MainRun() {
    }

    /**
     Ejecuta ContactListTestSuite.
     Opcional: si se recibe un argumento, se aplica como filtro de tags de Cucumber.
     
      @param args args[0] opcional para tags, ejemplo: "@smoke and @api"
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
