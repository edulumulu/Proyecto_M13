package com.example.proyecto_m13;

import java.util.Date;

public class Test_realizado {

    int id_test_realizado ;
    int id_test;
    Date fecha;
    Date fecha_proxima_revision;
    int id_cliente;
    int id_empleado ;
    String resultado;

    public Test_realizado(int id_test_realizado, int id_test, Date fecha, Date fecha_proxima_revision, int id_cliente, int id_empleado, String resultado) {
        this.id_test_realizado = id_test_realizado;
        this.id_test = id_test;
        this.fecha = fecha;
        this.fecha_proxima_revision = fecha_proxima_revision;
        this.id_cliente = id_cliente;
        this.id_empleado = id_empleado;
        this.resultado = resultado;
    }

    public Test_realizado(int id_test, int id_cliente, int id_empleado, String resultado) {
        this.id_test = id_test;
        this.id_cliente = id_cliente;
        this.id_empleado = id_empleado;
        this.resultado = resultado;
    }

    public int getId_test_realizado() {
        return id_test_realizado;
    }

    public void setId_test_realizado(int id_test_realizado) {
        this.id_test_realizado = id_test_realizado;
    }

    public int getId_test() {
        return id_test;
    }

    public void setId_test(int id_test) {
        this.id_test = id_test;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFecha_proxima_revision() {
        return fecha_proxima_revision;
    }

    public void setFecha_proxima_revision(Date fecha_proxima_revision) {
        this.fecha_proxima_revision = fecha_proxima_revision;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
