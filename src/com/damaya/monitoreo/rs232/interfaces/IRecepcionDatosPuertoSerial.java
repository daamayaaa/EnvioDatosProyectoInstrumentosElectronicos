package com.damaya.monitoreo.rs232.interfaces;

public interface IRecepcionDatosPuertoSerial {

	/**
	 * Inicializar los datos de entrada.
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
	 */
	public void abrirPuerto(String nombrePuerto);
	
	/**
	 * Se obtiene un canal de entrada
	 */
	public void obtenerCanalEntrada();
	
	/**
	 * Se fijan los par�metros de comunicaci�n del puerto
	 */
	public void fijarParametrosComunicacionPuerto();
	
	/**
	 * A�adimos un receptor de eventos para estar informados de lo que suceda en el puerto.
	 */
	public void adicionarReceptorEventos();

	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza#puertoSerialCompleto()
	 */
	public void puertoSerialCompleto();
}
