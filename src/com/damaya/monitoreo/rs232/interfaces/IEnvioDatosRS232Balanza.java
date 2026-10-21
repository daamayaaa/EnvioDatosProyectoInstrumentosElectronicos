package com.damaya.monitoreo.rs232.interfaces;

import java.util.Random;
import com.damaya.monitoreo.rs232.config.ParametrosConfiguracion;
import com.damaya.monitoreo.rs232.excepciones.GeneracionDatosExcepcion;
import gnu.io.SerialPort;

/**
 * The Interface IEnvioDatosRS232Balanza.
 */
public interface IEnvioDatosRS232Balanza {
	/**
	 * Generar valor aleatorio.
	 *
	 * @param random
	 *            the random
	 * @param lowerBound
	 *            the lower bound
	 * @param upperBound
	 *            the upper bound
	 * @param decimalPlaces
	 *            the decimal places
	 * @return the string
	 */
	public String generarValorAleatorio(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion;
	
	/**
	 * 
	 */
	public void inicializarDatosEntrada(); 
	
	/**
	 * Abrir puerto serial.
	 *
	 * @param nombrePuerto
	 *            the port name
	 * @return true, if successful
	 */
	public boolean abrirPuertoSerial(String nombrePuerto);
	
	/**
	 * Si el puerto no está en uso, se intenta abrir.
	 * 
	 * @param nombrePuerto
	 *            Nombre del Puerto.
	 */
	public void abrirPuerto(String nombrePuerto);
	
	/**
	 * Se obtiene un canal de entrada
	 */
	public void obtenerCanalEntrada();
	
	/**
	 * Se fijan los par�metros de comunicaci�n del puerto
	 */
	public void fijarParametrosComunicacionPuerto(ParametrosConfiguracion parametrosConfiguracion);
	
	/**
	 * Se obtiene la informaci�n detallada del puerto serial.
	 * 
	 * @param puertoSerial
	 */
	public void consultarInformacionPuerto(SerialPort puertoSerial);
	
	/**
	 * Puerto serial completo.
	 */
	public void puertoSerialCompleto();
	
	/**
	 * Enviar dato puerto serial.
	 *
	 * @param mensaje
	 *            the mensaje
	 */
	public void enviarDatoPuertoSerial(String mensaje);
}