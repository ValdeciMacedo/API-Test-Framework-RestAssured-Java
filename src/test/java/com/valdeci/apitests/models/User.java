package com.valdeci.apitests.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Modelo de Usuário — espelha o contrato da API ServeRest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private String nome;
    private String email;
    private String password;
    private String administrador;
    private String _id;

    public User() {}

    public User(String nome, String email, String password, String administrador) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.administrador = administrador;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAdministrador() { return administrador; }
    public void setAdministrador(String administrador) { this.administrador = administrador; }

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    @Override
    public String toString() {
        return "User{nome='" + nome + "', email='" + email + "', administrador='" + administrador + "'}";
    }
}
