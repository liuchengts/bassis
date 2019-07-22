package com.bassis.boot.web.filter;

import com.bassis.boot.common.Declaration;

import javax.servlet.*;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {
    protected FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(Declaration.encoding);
        chain.doFilter(request, response);
    }

    public void destroy() {
        this.filterConfig = null;
    }
}
