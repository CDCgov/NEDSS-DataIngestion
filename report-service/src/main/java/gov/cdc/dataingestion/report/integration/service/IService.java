package gov.cdc.dataingestion.report.integration.service;

/**
 * Base service interface.
 * @param <T> input type.
 * @param <G> output type.
 */
public interface IService<T, G> {

    /**
     * Executes service.
     */
     G execute(T input);
}
