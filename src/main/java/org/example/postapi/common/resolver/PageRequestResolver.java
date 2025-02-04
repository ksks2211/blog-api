package org.example.postapi.common.resolver;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author rival
 * @since 2025-02-04
 */
public class PageRequestResolver implements HandlerMethodArgumentResolver {


    public static final int FIXED_PAGE_SIZE = 10;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Pageable.class) && parameter.hasParameterAnnotation(PageRequestDefault.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String page = webRequest.getParameter("page");
//        String size = webRequest.getParameter("size");
        try{
            int p = page == null ? 1 : Integer.parseInt(page);
//            int s = size == null ? FIXED_PAGE_SIZE : Integer.parseInt(size);
            if(p < 1){
                p = 1;
            }
//            if(s < 5 || s > 20){
//                s = FIXED_PAGE_SIZE;
//            }
            // one base => zero base
            return PageRequest.of(p-1, FIXED_PAGE_SIZE);
        }catch (Exception ignored){}

        return PageRequest.of(0, FIXED_PAGE_SIZE);
    }
}
