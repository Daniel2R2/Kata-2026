package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Almacén en memoria por hilo para compartir datos entre steps de un escenario.
 * Evita acoplar step definitions con estado global mutable.
 */
public final class ScenarioContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    /**
     * Utilidad estática, no instanciable.
     */
    private ScenarioContext() {
    }

    /**
     * Guarda un valor asociado a una clave en el contexto actual.
     *
     * @param key identificador lógico del dato.
     * @param value objeto a compartir en el escenario.
     */
    public static void set(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    /**
     * Recupera un valor tipado del contexto actual.
     *
     * @param key clave almacenada.
     * @param type tipo esperado del valor.
     * @param <T> tipo genérico de retorno.
     * @return valor convertido o `null` si no existe.
     */
    public static <T> T get(String key, Class<T> type) {
        Object value = CONTEXT.get().get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    /**
     * Limpia el contexto del hilo actual al inicio/fin de cada escenario.
     */
    public static void clear() {
        CONTEXT.get().clear();
    }
}
