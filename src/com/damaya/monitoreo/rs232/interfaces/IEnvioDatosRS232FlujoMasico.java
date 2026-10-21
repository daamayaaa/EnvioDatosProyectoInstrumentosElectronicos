package com.damaya.monitoreo.rs232.interfaces;

import java.util.Random;
import com.damaya.monitoreo.rs232.config.ParametrosConfiguracion;
import com.damaya.monitoreo.rs232.excepciones.GeneracionDatosExcepcion;
import gnu.io.SerialPort;

// TODO: Auto-generated Javadoc
/**
 * The Interface IEnvioDatosRS232FlujoMasico.
 */
public interface IEnvioDatosRS232FlujoMasico {

	/**
	 * Generar valor aleatorio.
	 *
	 * @param random the random
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @param decimalPlaces the decimal places
	 * @return the string
	 */
	public String generarValorAleatorio(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion;

	/**
	 * Generar valor aleatorio tres decimales.
	 *
	 * @param random the random
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @param decimalPlaces the decimal places
	 * @return the string
	 */
	public String generarValorAleatorioTresDecimales(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion;

	/**
	 * Generar valor aleatorio dos decimales.
	 *
	 * @param random the random
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @param decimalPlaces the decimal places
	 * @return the string
	 */
	public String generarValorAleatorioDosDecimales(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion;
	
	/**
	 * Abrir puerto serial.
	 *
	 * @param portName the port name
	 * @return true, if successful
	 */
	public boolean abrirPuertoSerial(String portName);
	
	/**
     * Si el puerto no está en uso, se intenta abrir.
	 * @param nombrePuerto Nombre del Puerto.
	 */
	public void abrirPuerto(String nombrePuerto);

	/**
	 * Puerto serial completo.
	 */
	public void puertoSerialCompleto();

	/**
	 * Enviar dato puerto serial.
	 *
	 * @param mensaje the mensaje
	 */
	public void enviarDatoPuertoSerial(String mensaje);
	
	/**
	 * Se obtiene un canal de entrada	 
	 */
	public void obtenerCanalEntrada();
	
	/**
     * Se fijan los par�metros de comunicación del puerto-
	 */
	public void fijarParametrosComunicacionPuerto(ParametrosConfiguracion parametrosConfiguracion);
	
	/**
	 * Se obtiene la informaci�n detallada del puerto serial.
	 * @param puertoSerial
	 */
	public void consultarInformacionPuerto(SerialPort puertoSerial);

}