package com.freemarket.api_gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.freemarket.api_gateway.DTO.ResponseDTO;

import reactor.core.publisher.Mono;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "VGhpc0lzQVNlY3VyZUtleUZvckpXVFdpdGhIbWFjU0hBMjU2IQ==";

    private static final List<String> PUBLIC_ROUTES = List.of(
    "/api-v1/auth/login",
    "/api-v1/auth/register",
    "/api-v1/auth/refresh",
    "/api-v1/auth/logout",
    "/api-v1/productos/get",
    "/api-v1/auth/password/reset-request",
    "/api-v1/auth/password/reset",
    "/api-v1/config/public",
    "/api-v1/auth/password/validate-token",
    "/api-v1/productos/get/active"
    );

    // Reemplaza el Map.ofEntries por esto:
private static final Map<String, String> ROUTE_PRIVILEGES;

static {
    ROUTE_PRIVILEGES = new java.util.LinkedHashMap<>();
    
    // auth
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/auth/update",            "UPDATE_USER");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/auth/setState/",         "SET_STATE_USER");
    ROUTE_PRIVILEGES.put("POST:/api-v1/auth/rol",                "CREATE_ROL");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/auth/rol/change",        "CHANGE_ROL_USER");
    ROUTE_PRIVILEGES.put("GET:/api-v1/auth/getall",              "READ_USER");
    ROUTE_PRIVILEGES.put("DELETE:/api-v1/auth/delete/admin/",    "DELETE_USER");
    ROUTE_PRIVILEGES.put("GET:/api-v1/auth/rol/getall",          "READ_ROL");

    // productos
    ROUTE_PRIVILEGES.put("POST:/api-v1/productos/create",        "CREATE_PRODUCT");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/productos/update/",      "UPDATE_PRODUCT");
    ROUTE_PRIVILEGES.put("DELETE:/api-v1/productos/delete/",     "DELETE_PRODUCT");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/productos/activate/{id}", "UPDATE_PRODUCT");    

    // reserva
    ROUTE_PRIVILEGES.put("POST:/api-v1/reserve/createReserve",   "CREATE_RESERVE");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/reserve/cancel/",        "DELETE_RESERVE");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/reserve/cancel",         "UPDATE_RESERVE");
    ROUTE_PRIVILEGES.put("GET:/api-v1/reserve/getallreserve",    "READ_RESERVE");
    ROUTE_PRIVILEGES.put("GET:/api-v1/reserve/user/",            "READ_RESERVE");
    ROUTE_PRIVILEGES.put("GET:/api-v1/reserve/",                 "READ_RESERVE");

    // config
    ROUTE_PRIVILEGES.put("POST:/api-v1/config/create",           "CREATE_SYSTEM_CONFIG");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/config/update/",         "UPDATE_SYSTEM_CONFIG");
    ROUTE_PRIVILEGES.put("GET:/api-v1/config/get/",              "READ_SYSTEM_CONFIG");

    // delivery — MÁS ESPECÍFICAS PRIMERO
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/delivery/reserva/status/","UPDATE_DELIVERY_STATE");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/delivery/reserva",        "UPDATE_DELIVERY_STATE");
    ROUTE_PRIVILEGES.put("PATCH:/api-v1/delivery/take",            "PATCH_DELIVERY");
    ROUTE_PRIVILEGES.put("GET:/api-v1/delivery/status/",          "READ_DELIVERY");  
    ROUTE_PRIVILEGES.put("GET:/api-v1/delivery/delivery/",        "READ_DELIVERY");
    ROUTE_PRIVILEGES.put("GET:/api-v1/delivery/reserva/",         "READ_DELIVERY");
    ROUTE_PRIVILEGES.put("GET:/api-v1/delivery/usuario/",         "READ_DELIVERY");
    ROUTE_PRIVILEGES.put("GET:/api-v1/delivery/all",              "READ_DELIVERY");
    ROUTE_PRIVILEGES.put("GET:/api-v1/delivery/",                 "READ_DELIVERY"); 
    ROUTE_PRIVILEGES.put("GET:/api-v1/delivery",                  "READ_DELIVERY");

    // location
    ROUTE_PRIVILEGES.put("POST:/api-v1/location/createLocation",  "CREATE_LOCATION");
    ROUTE_PRIVILEGES.put("PUT:/api-v1/location/updateLocation",   "UPDATE_LOCATION");
    ROUTE_PRIVILEGES.put("GET:/api-v1/location/getLocation/",     "READ_LOCATION");

    // privileges
    ROUTE_PRIVILEGES.put("POST:/api-v1/privileges/modules",       "CREATE_ROL");
    ROUTE_PRIVILEGES.put("GET:/api-v1/privileges/modules",        "READ_USER");
    ROUTE_PRIVILEGES.put("GET:/api-v1/privileges/all",            "READ_USER");
    ROUTE_PRIVILEGES.put("POST:/api-v1/privileges/create",        "CREATE_ROL");
    ROUTE_PRIVILEGES.put("POST:/api-v1/privileges/asignar",       "CHANGE_ROL_USER");
    ROUTE_PRIVILEGES.put("DELETE:/api-v1/privileges/eliminar/",   "CHANGE_ROL_USER");
    ROUTE_PRIVILEGES.put("GET:/api-v1/privileges/role/",          "READ_USER");

    // auth delete
    ROUTE_PRIVILEGES.put("DELETE:/api-v1/auth/delete",            "DELETE_USER");
    }

    private final WebClient webClient;
    private final ReactiveCircuitBreaker circuitBreaker;

    public JwtAuthenticationFilter(
            @Qualifier("loadBalancedBuilder") WebClient.Builder webClientBuilder,
            ReactiveCircuitBreakerFactory<?, ?> cbFactory) { 
        this.webClient = webClientBuilder.baseUrl("http://privileges-service").build();
        this.circuitBreaker = cbFactory.create("privileges-service"); 
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        if (PUBLIC_ROUTES.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = getClaims(authHeader.substring(7));

            if (claims.getExpiration().before(new Date())) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            Long roleId = claims.get("roleId", Long.class);
            String status = claims.get("status", String.class);
            Long userId = claims.get("userId",Long.class);

            if ("INACTIVO".equals(status)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
            }

            String requiredPrivilege = ROUTE_PRIVILEGES.entrySet().stream()
                .filter(e -> e.getKey().startsWith(method + ":") &&
                             path.startsWith(e.getKey().split(":")[1]))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r
                    .header("X-Username", username)
                    .header("X-Roles", String.join(",", roles))
                    .header("X-Role-Id", String.valueOf(roleId))
                .header("X-User-Id", String.valueOf(userId)))
                .build();

            if (requiredPrivilege == null) {
                return chain.filter(mutatedExchange);
            }

            String privilegeToCheck = requiredPrivilege;

            return circuitBreaker.run(
                webClient.get()
                    .uri("/api-v1/privileges/role/" + roleId)
                    .retrieve()
                    .bodyToFlux(ResponseDTO.class)
                    .collectList()
                    .flatMap(privileges -> {
                        boolean hasPrivilege = privileges.stream()
                            .anyMatch(p -> p.getPrivilegeName().equals(privilegeToCheck));

                        if (!hasPrivilege) {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }

                        return chain.filter(mutatedExchange);
                    }),

               //circuito abierto o timeout
                throwable -> {
                    log.error("Fallback activado: {}", throwable.getClass().getName() + " - " + throwable.getMessage());
                    return chain.filter(mutatedExchange);
                }
            );
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() { return -1; }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}