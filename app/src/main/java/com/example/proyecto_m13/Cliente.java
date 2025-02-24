package com.example.proyecto_m13;

import java.util.Date;

public class Cliente {
    String id;
    String name;
    String surname;
    String dni;
    String date_born;
    int tlf;
    String email;
    String tutor;
    String graduate;
    String date_graduacion;
    String tipo_lentes;
    String Test_TVPS;
    //String date_test_TVPS;
    //String next_date_TVPS;
    String street;
    int cp;
    String ciudad;


    public Cliente(String id, String name, String surname, String dni, String date_born, int tlf, String email, String tutor, String graduate, String date_graduacion, String tipo_lentes, String test_TVPS, String street, int cp, String ciudad) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.date_born = date_born;
        this.tlf = tlf;
        this.email = email;
        this.tutor = tutor;
        this.graduate = graduate;
        this.date_graduacion = date_graduacion;
        this.tipo_lentes = tipo_lentes;
        Test_TVPS = test_TVPS;
        this.street = street;
        this.cp = cp;
        this.ciudad = ciudad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDate_born() {
        return date_born;
    }

    public void setDate_born(String date_born) {
        this.date_born = date_born;
    }

    public int getTlf() {
        return tlf;
    }

    public void setTlf(int tlf) {
        this.tlf = tlf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public String getGraduate() {
        return graduate;
    }

    public void setGraduate(String graduate) {
        this.graduate = graduate;
    }

    public String getDate_graduacion() {
        return date_graduacion;
    }

    public void setDate_graduacion(String date_graduacion) {
        this.date_graduacion = date_graduacion;
    }

    public String getTipo_lentes() {
        return tipo_lentes;
    }

    public void setTipo_lentes(String tipo_lentes) {
        this.tipo_lentes = tipo_lentes;
    }

    public String getTest_TVPS() {
        return Test_TVPS;
    }

    public void setTest_TVPS(String test_TVPS) {
        Test_TVPS = test_TVPS;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
