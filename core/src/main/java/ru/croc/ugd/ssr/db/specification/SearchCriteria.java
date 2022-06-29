package ru.croc.ugd.ssr.db.specification;

/**
 * Критерий для формирования предикато.
 */
public class SearchCriteria {
    private String key;
    private Object value;
    private SearchOperation operation;

    /**
     * Констурктор.
     * @param key        ключ
     * @param value      значение
     * @param operation  операция
     */
    public SearchCriteria(String key, Object value, SearchOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    /**
     * Геттер.
     *
     * @return ключ
     */
    public String getKey() {
        return key;
    }

    /**
     * Геттер.
     *
     * @return значение
     */
    public Object getValue() {
        return value;
    }

    /**
     * Геттер.
     *
     * @return оператор
     */
    public SearchOperation getOperation() {
        return operation;
    }
}
