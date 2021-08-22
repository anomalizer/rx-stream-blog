package net.anomalizer.j2se.rxblog;

import lombok.Data;

import java.util.List;

/**
 * General template for paginated REST resultset
 * @param <T> Specific type of the resultset
 */
@Data
public final class PaginatedResponse<T> {
    /**
     * URI to the next page
     * 
     * String representation of URI to the next page in the paginated responsed.
     * NULL if this is the last page in the response
     */
    private final String next;

    /**
     * A sequence of objects of a given type
     */
    private final List<T> results;
}
