package com.sonarshowcase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Base64;
import java.util.List;

/**
 * Data import controller with Insecure Deserialization vulnerability.
 *
 * SEC-11: Insecure Deserialization - Remote Code Execution
 *
 * @author SonarShowcase
 */
@RestController
@RequestMapping("/api/v1/data")
@Tag(name = "Data Import", description = "Data import API endpoints. ⚠️ Contains intentional Insecure Deserialization vulnerabilities for demonstration.")
public class DataImportController {

    /**
     * Default constructor for DataImportController.
     */
    public DataImportController() {
    }

    /**
     * SEC-11: Insecure Deserialization vulnerability - S5135
     * Deserializes untrusted data without validation
     *
     * This can lead to Remote Code Execution (RCE) if attacker provides
     * malicious serialized Java objects (e.g., using ysoserial gadget chains).
     *
     * @param data Base64-encoded serialized object
     * @return Deserialized object information
     */
    @Operation(
        summary = "Import serialized data",
        description = "Deserializes Base64-encoded serialized data with class allowlist validation."
    )
    @ApiResponse(responseCode = "200", description = "Object deserialized")
    @ApiResponse(responseCode = "400", description = "Deserialization error")
    @PostMapping("/import")
    public ResponseEntity<String> importData(
            @Parameter(description = "Base64-encoded serialized object")
            @RequestBody String data) {
        try {
            byte[] decodedData = Base64.getDecoder().decode(data);
            ByteArrayInputStream bis = new ByteArrayInputStream(decodedData);
            ObjectInputStream ois = new SafeObjectInputStream(bis);

            Object obj = ois.readObject();

            ois.close();

            return ResponseEntity.ok("Data imported: " + obj.getClass().getName());

        } catch (InvalidClassException e) {
            return ResponseEntity.badRequest()
                    .body("Deserialization error: unauthorized class");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Deserialization error");
        }
    }

    /**
     * A safe ObjectInputStream that only allows deserialization of pre-approved classes.
     */
    private static class SafeObjectInputStream extends ObjectInputStream {

        private static final List<String> APPROVED_CLASSES = List.of(
                String.class.getName()
        );

        SafeObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
            if (!APPROVED_CLASSES.contains(osc.getName())) {
                throw new InvalidClassException("Unauthorized deserialization", osc.getName());
            }
            return super.resolveClass(osc);
        }
    }

    /**
     * SEC-11: Another deserialization variant - session data
     *
     * @param sessionData Base64-encoded session object
     * @return Session restoration status
     */
    @Operation(
        summary = "Restore session (VULNERABLE)",
        description = "🔴 INSECURE DESERIALIZATION - Session data deserialized without validation."
    )
    @PostMapping("/session/restore")
    public ResponseEntity<String> restoreSession(
            @RequestBody String sessionData) {
        try {
            // SEC: Deserializing session data
            byte[] decoded = Base64.getDecoder().decode(sessionData);
            ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(decoded));
            Object session = ois.readObject();

            return ResponseEntity.ok("Session restored: " + session.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Session restore error: " + e.getMessage());
        }
    }

    /**
     * Create serialized data for testing (helper endpoint)
     *
     * @param text Text to serialize
     * @return Base64-encoded serialized object
     */
    @Operation(
        summary = "Create test serialized data",
        description = "Helper endpoint to create serialized data for testing."
    )
    @GetMapping("/create-test-data")
    public ResponseEntity<String> createTestData(
            @RequestParam(defaultValue = "test") String text) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(text);
            oos.close();

            String encoded = Base64.getEncoder().encodeToString(bos.toByteArray());
            return ResponseEntity.ok(encoded);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
