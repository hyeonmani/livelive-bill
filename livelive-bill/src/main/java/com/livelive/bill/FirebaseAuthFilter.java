package com.livelive.bill;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class FirebaseAuthFilter implements Filter {

    public static ThreadLocal<FirebaseToken> currentUser = new ThreadLocal<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = Optional.ofNullable(req.getHeader("Authorization"))
                .filter(x -> x.startsWith("Bearer "))
                .map(x -> x.substring(7)).orElse(null);

        if (token != null) {
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
                currentUser.set(decoded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }
}
