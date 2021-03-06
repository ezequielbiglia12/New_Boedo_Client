package com.example.androidfood.Model;

import java.util.List;

public class Request {
    private String telefono;
    private String nombre;
    private String apellido;
    private String email;
    private String direccion;
    private String entrecalles;
    private String Pisoydepartamento;
    private String Localidad;
    private String total;
    private String estados;
    private String comment;
    private List<Order> comidas; // lista de comida ordenada

    public Request() {
    }

    public Request(String telefono, String nombre, String apellido, String email, String direccion, String entrecalles, String pisoydepartamento, String localidad, String total, String estados, String comment, List<Order> comidas) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.direccion = direccion;
        this.entrecalles = entrecalles;
        this.Pisoydepartamento = pisoydepartamento;
        this.Localidad = localidad;
        this.total = total;
        this.estados = estados;
        this.comment = comment;
        this.comidas = comidas;
    }


    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.nombre = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEstados() {
        return estados;
    }

    public void setEstados(String estados) {
        this.estados = estados;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Order> getComidas() {
        return comidas;
    }

    public void setComidas(List<Order> comidas) {
        this.comidas = comidas;
    }

    public String getEntrecalles() {
        return entrecalles;
    }//

    public void setEntrecalles(String entrecalles) {
        this.entrecalles = entrecalles;
    }

    public String getPisoydepartamento() {
        return Pisoydepartamento;
    }

    public void setPisoydepartamento(String pisoydepartamento) {
        Pisoydepartamento = pisoydepartamento;
    }

    public String getLocalidad() {
        return Localidad;
    }

    public void setLocalidad(String localidad) {
        Localidad = localidad;
    }


}
