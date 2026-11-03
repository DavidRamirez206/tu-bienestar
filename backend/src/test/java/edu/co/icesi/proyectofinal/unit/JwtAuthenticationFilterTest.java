package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.security.CustomUserDetailService;
import edu.co.icesi.proyectofinal.security.JwtService;
import edu.co.icesi.proyectofinal.security.filters.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.CannotCreateTransactionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @Test
    void answers503WhenTheDatabaseIsNotAvailable() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailService userDetailService = mock(CustomUserDetailService.class);
        when(jwtService.extractUsername("token")).thenReturn("juan@icesi.edu.co");
        when(userDetailService.loadUserByUsername("juan@icesi.edu.co"))
                .thenThrow(new CannotCreateTransactionException("sin conexion"));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/routines");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        new JwtAuthenticationFilter(jwtService, userDetailService).doFilter(request, response, chain);

        assertEquals(503, response.getStatus());
        assertNull(chain.getRequest());
    }
}
