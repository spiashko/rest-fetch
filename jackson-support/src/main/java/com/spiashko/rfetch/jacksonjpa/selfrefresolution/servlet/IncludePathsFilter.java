package com.spiashko.rfetch.jacksonjpa.selfrefresolution.servlet;

import com.spiashko.rfetch.jacksonjpa.selfrefresolution.core.IncludePathsHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class IncludePathsFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            IncludePathsHolder.remove();
        }
    }
}
