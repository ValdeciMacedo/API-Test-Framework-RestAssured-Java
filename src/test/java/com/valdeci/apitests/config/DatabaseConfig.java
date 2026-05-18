package com.valdeci.apitests.config;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gerencia conexão com PostgreSQL para validações diretas no banco.
 * Usado para verificar persistência de dados após chamadas à API.
 */
public class DatabaseConfig {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    EnvConfig.getDbUrl(),
                    EnvConfig.getDbUser(),
                    EnvConfig.getDbPassword()
            );
            System.out.println("✅ Conexão com PostgreSQL estabelecida.");
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Conexão com PostgreSQL encerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    /**
     * Executa uma query e retorna lista de resultados como Map.
     */
    public static List<Map<String, Object>> executeQuery(String sql) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
    }

    /**
     * Executa query que retorna um único valor (ex: COUNT, SELECT campo).
     */
    public static Object executeScalar(String sql) throws SQLException {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getObject(1);
            }
        }
        return null;
    }

    /**
     * Executa INSERT, UPDATE ou DELETE.
     */
    public static int executeUpdate(String sql) throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }
}
