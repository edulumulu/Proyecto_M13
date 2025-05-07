package Clases;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Test_realizado {

    private int id_test_realizado ;
    private  int id_test;
    private Date fecha;
    private  Date fecha_proxima_revision;
    private int id_cliente;
    private int id_empleado ;
    private String resultado;

    public Test_realizado(){

    }

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

    public String fecha_proxima_buen_formato() {

        if (fecha_proxima_revision == null) {
            return "Fecha no disponible"; // O lo que prefieras mostrar
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fecha_formato = sdf.format(fecha_proxima_revision);

        return fecha_formato;
    }

    /**
     * Dice si el test puede ser pasado o no comparando la fecha actul con la fecha de proxima revisión, en caso de retornar nullo permite hacer el test
     * @return
     */
    public boolean es_posible_realizar_tvps() {
        if (fecha_proxima_revision == null) {
            return true;
        }

        // Si la fecha es anterior al año 2000, por ejemplo, la consideramos inválida
        Calendar fechaMinValida = Calendar.getInstance();
        fechaMinValida.set(2000, Calendar.JANUARY, 1);

        if (fecha_proxima_revision.before(fechaMinValida.getTime())) {
            return true;
        }

        Log.d("Fecha", "fecha_proxima_revision: " + fecha_proxima_revision);

        Date fechaActual = new Date();
        return fechaActual.after(fecha_proxima_revision);
    }

}
