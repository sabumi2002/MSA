package com.example.zuulservice.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class ZuulLoggingFilter extends ZuulFilter {
    @Override
    public String filterType() {    // 사전필터인지 사후필터인지 결정하는부분
        return "pre";
    }

    @Override
    public int filterOrder() {  // 여러개의 필터가 있을때 순서를 결정함
        return 1;
    }

    @Override
    public boolean shouldFilter() { // 원하는 옵션에따라 필터를 사용하겠다 안하겠다 지정
        return true;
    }

    @Override
    public Object run() throws ZuulException {  // 실제 어떻게 동작하는지 정의
        log.info("************************ printing logs: ");

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("************************ " + request.getRequestURI());
        return null;
    }
}
