package com.spiashko.jpafetch.jacksonjpa.selfrefresolution.servlet;

import com.spiashko.jpafetch.jacksonjpa.selfrefresolution.core.IncludePathsHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class IncludePathsFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String[] includes = request.getParameterValues("include");
        IncludePathsHolder.setIncludedPaths(Arrays.asList(includes));

        try {
            chain.doFilter(request, response);
        } finally {
            IncludePathsHolder.remove();
        }
    }
}
