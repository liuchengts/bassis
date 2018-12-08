package bassis.bassis_web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharacterEncodingFilter implements Filter {
    protected String encoding = "UTF-8";
    protected FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String encoding = selectEncoding(request);
        if (encoding != null) {
            request.setCharacterEncoding(encoding);
        }
        chain.doFilter(request, response);
    }

    protected String selectEncoding(ServletRequest request) {
        return (this.encoding);
    }

    public void destroy() {
        this.encoding = null;
        this.filterConfig = null;
    }

}
