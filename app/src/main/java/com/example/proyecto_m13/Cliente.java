package com.example.proyecto_m13;

import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;

public class Cliente {
    int id;
    String name;
    String surname;
    String dni;
    Date date_born;
    int tlf;
    String email;
    String tutor;
    boolean graduate;
    Date date_graduacion;
    String tipo_lentes;
    boolean Test_TVPS;
    //String date_test_TVPS;
    //String next_date_TVPS;
    String street;
    int cp;
    String ciudad;

    public Cliente(int id) {
        this.id = id;
    }

    //Constructor con todo
    public Cliente(int id, String name, String surname, String dni, Date date_born, int tlf, String email, String tutor, boolean graduate, Date date_graduacion, String tipo_lentes, boolean test_TVPS, String street, int cp, String ciudad){
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
    //constructor sin id

    public Cliente(String name, String surname, String dni, Date date_born, int tlf, String email, String tutor, String street, int cp, String ciudad, String tipo){
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.date_born = date_born;
        this.tlf = tlf;
        this.email = email;
        this.tutor = tutor;
        this.street = street;
        this.cp = cp;
        this.ciudad = ciudad;
        this.tipo_lentes = tipo;
    }


    public Cliente(int id,String name, String surname, String dni, Date date_born, int tlf, String email, String tutor, String street, int cp, String ciudad, String tipo){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.date_born = date_born;
        this.tlf = tlf;
        this.email = email;
        this.tutor = tutor;
        this.street = street;
        this.cp = cp;
        this.ciudad = ciudad;
        this.tipo_lentes = tipo;
    }



    //constructor sin graduación

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getDate_born() {
        return date_born;
    }

    public void setDate_born(Date date_born) {
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

    public boolean getGraduate() {
        return graduate;
    }

    public void setGraduate(boolean graduate) {
        this.graduate = graduate;
    }

    public Date getDate_graduacion() {
        return date_graduacion;
    }

    public void setDate_graduacion(Date date_graduacion) {
        this.date_graduacion = date_graduacion;
    }

    public String getTipo_lentes() {
        return tipo_lentes;
    }

    public void setTipo_lentes(String tipo_lentes) {
        this.tipo_lentes = tipo_lentes;
    }

    public boolean getTest_TVPS() {
        return Test_TVPS;
    }

    public void setTest_TVPS(boolean test_TVPS) {
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

    public int calcularEdad() {
        if (date_born == null) {
            return 0; // Si no tiene fecha de nacimiento, devolver 0 o el valor que desees
        }

        Calendar fechaNac = Calendar.getInstance();
        fechaNac.setTime(date_born);

        Calendar hoy = Calendar.getInstance();

        int edad = hoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);

        // Ajustar si no ha cumplido años este año
        if (hoy.get(Calendar.MONTH) < fechaNac.get(Calendar.MONTH) ||
                (hoy.get(Calendar.MONTH) == fechaNac.get(Calendar.MONTH) &&
                        hoy.get(Calendar.DAY_OF_MONTH) < fechaNac.get(Calendar.DAY_OF_MONTH))) {
            edad--;
        }

        return edad;
    }
}
