package gbw.riot.tftfieldanalysis.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {

    private static final Log log = LogFactory.getLog(LoggingInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        String subUrl = extractSubUrl(request.getRequestURI());

        String queryString = request.getQueryString();

        if (queryString != null) {
            subUrl += "?" + queryString;
        }

        final String msg = String.format(
                "%s %s --> %s --> %d",
                request.getMethod().toUpperCase(),
                request.getRemoteAddr(),
                subUrl,
                response.getStatus()
        );

        log.info(msg);
    }

    /* package-private */ String extractSubUrl(String url) {
        final int indexOfSlashApi = url.lastIndexOf("/api");
        return url.substring(indexOfSlashApi);
    }

}
