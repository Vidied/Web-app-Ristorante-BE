package davidepan.capstone.security;

import davidepan.capstone.entities.User;
import davidepan.capstone.exceptions.UnauthorizedException;
import davidepan.capstone.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authHeader.substring(7);

            jwtTools.verifyToken(accessToken);

            String id = jwtTools.extractIdFromToken(accessToken);

            User currentUser = userService.findById(Long.parseLong(id));

            var authorities = currentUser.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .toList();

            Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UnauthorizedException ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);


    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        String path = request.getServletPath();
        String method = request.getMethod();

        return new AntPathMatcher().match("/auth/**", path) || (method.equalsIgnoreCase("GET") && (
                new AntPathMatcher().match("/products/**", path) ||
                new AntPathMatcher().match("/categoriesbb/**", path)||
                new AntPathMatcher().match("/products", path) ||
                new AntPathMatcher().match("/categories", path)||
                new AntPathMatcher().match("/ingredients", path) ||
                new AntPathMatcher().match("/ingredients/**", path)
                ));
    }

}
