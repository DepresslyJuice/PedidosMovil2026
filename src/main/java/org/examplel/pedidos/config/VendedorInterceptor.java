package org.examplel.pedidos.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.examplel.pedidos.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class VendedorInterceptor implements HandlerInterceptor {
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            VendedorOnly vendedorOnly = handlerMethod.getMethodAnnotation(VendedorOnly.class);
            
            if (vendedorOnly != null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || auth.getName() == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                
                return userRepository.findByUsername(auth.getName())
                    .map(user -> {
                        if (!"VENDEDOR".equals(user.getRole())) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            return false;
                        }
                        return true;
                    })
                    .orElse(false);
            }
        }
        return true;
    }
}
