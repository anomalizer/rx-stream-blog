package net.anomalizer.j2se.rxblog;

import java.util.Map;

/* Direct import of some arbitrary code */
public class UrlUtils {
    /* Legacy validator that throws checked exception
     *
     * The logic here is irrelevant, what matters here is that this code throws a
     * checked exception
     */
    public static String getUrlWithQueryParams(String urlPath, Map<String, String> params)
            throws Exception {
        return urlPath;
    }
}

