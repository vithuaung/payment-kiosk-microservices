package com.conversion.pmk.gateway;

import com.conversion.pmk.common.exception.PmkException;
import com.conversion.pmk.gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject values that would normally come from @Value bindings
        ReflectionTestUtils.setField(jwtUtil, "secret", "pmk-test-secret-key-must-be-at-least-32-chars");
        ReflectionTestUtils.setField(jwtUtil, "expiryMs", 3_600_000L);
    }

    @Test
    void generateToken_returnsNonNullString() {
        String token = jwtUtil.generateToken("TERM-001", "TERMINAL");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void validateToken_validToken_returnsCorrectClaims() {
        String token = jwtUtil.generateToken("TERM-001", "TERMINAL");

        Claims claims = jwtUtil.validateToken(token);

        assertEquals("TERM-001", claims.getSubject());
        assertEquals("TERMINAL", claims.get("role", String.class));
    }

    @Test
    void validateToken_tamperedToken_throwsPmkException() {
        String token = jwtUtil.generateToken("TERM-001", "TERMINAL");
        // Corrupt the signature by appending garbage
        String tampered = token + "corrupted";

        PmkException ex = assertThrows(PmkException.class, () -> jwtUtil.validateToken(tampered));
        assertEquals("AUTH_INVALID_TOKEN", ex.getErrorCode());
    }
}
