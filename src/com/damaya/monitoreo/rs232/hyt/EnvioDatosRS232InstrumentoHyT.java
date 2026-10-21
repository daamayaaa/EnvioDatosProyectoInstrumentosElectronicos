package com.damaya.monitoreo.rs232.hyt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import com.damaya.monitoreo.rs232.config.ParametrosConfiguracion;
import com.damaya.monitoreo.rs232.constantes.ConstantesEnvioRecepcionDatosRS232;
import com.damaya.monitoreo.rs232.dto.HumedadTemperaturaDTO;
import com.damaya.monitoreo.rs232.excepciones.GeneracionDatosExcepcion;
import com.damaya.monitoreo.rs232.flujomasico.EnvioDatosRS232InstrumentoFlujo;
import com.damaya.monitoreo.rs232.interfaces.IEnvioDatosHaciaPuertoSerial;
import com.damaya.monitoreo.rs232.interfaces.IEnvioDatosRS232HyT;
import com.damaya.monitoreo.rs232.mapeo.MapeadorParametrosConfiguracion;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class EnvioDatosRS232InstrumentoHyT implements IEnvioDatosRS232HyT, IEnvioDatosHaciaPuertoSerial, Runnable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**	 The id puerto serial.	 */
	private CommPortIdentifier idPuertoSerial;
	/** The enum comm. */
	private Enumeration<CommPortIdentifier> enumComm;
	/** The puerto serial. */
	private SerialPort puertoSerial;
	/** The flujo salida. */
	private OutputStream flujoSalida;
	/** The puerto serial para datos abierto. */
	private Boolean puertoSerialParaDatosAbierto;
	/** The lista puertos. */
	private List<String> listaPuertos;
	/** The lista puertos as vector. */
	private String[] listaPuertosAsVector;
	/** The nombre puerto. */
	private String nombrePuerto;
	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(EnvioDatosRS232InstrumentoFlujo.class);
	
	/**
	 * Instantiates a new envio datos r s232 balanza.
	 */
	public EnvioDatosRS232InstrumentoHyT() {
		super();
		setPuertoSerialParaDatosAbierto(false);
		listaPuertos = FuncionesUtilitarias.obtenerListaPuertos();
		listaPuertosAsVector = listaPuertos.toArray(new String[listaPuertos.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Integer segundosRestantes = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el tiempo de ejecución de la aplicación: ", "Tiempo de ejecución", JOptionPane.INFORMATION_MESSAGE));
		if (listaPuertosAsVector != null || !ArrayUtils.isEmpty(listaPuertosAsVector)) {
			if (FuncionesUtilitarias.existeValor(listaPuertosAsVector, 1)) {
				nombrePuerto = (String) JOptionPane.showInputDialog(null, "Seleccione un puerto para inicar el envio de datos - Humedad y Temperaura: ", "Selecci�n de datos de puerto serial", JOptionPane.QUESTION_MESSAGE, null, listaPuertosAsVector, listaPuertosAsVector[1]);
				if (nombrePuerto != null) {
					CommPortIdentifier portIdentifier;
					try {
						portIdentifier = CommPortIdentifier.getPortIdentifier(nombrePuerto);
						if (portIdentifier.isCurrentlyOwned()) {
							logger.error("El puerto: " + nombrePuerto + " se encuentra en uso.");
						}
						else {
							if (abrirPuertoSerial(nombrePuerto) != true) {
								return;
							}
							while (segundosRestantes > 0) {
								try {
									segundosRestantes--;
									logger.info("Se inicia el envío de datos de los sensores de humedad y temperatura suministrando un tiempo de : " + segundosRestantes + " ms.");
									String datoSimuladoHiperTerminal = generarRegistroParaEnvioHaciaPuertoSerial();
									enviarDatoPuertoSerial(datoSimuladoHiperTerminal);
									Thread.sleep(ConstantesEnvioRecepcionDatosRS232.FRECUENCIA);
								}
								catch (InterruptedException e) {
									logger.error("El hilo ha sido interrumpido mientras dorm�a: " + e.getMessage());
									Thread.currentThread().interrupt();
								}
							}
							puertoSerialCompleto();
						}
					}
					catch (NoSuchPortException e) {
						logger.error("El puerto no puede establecer una conexi�n. Por tanto termina la ejecución del programa.");
						System.exit(1);
					}
				}
				else {
					logger.error("El puerto no puede permanecer vac�o. Por tanto termina la ejecución del programa.");
					System.exit(1);
				}
			}
			else {
				logger.error("No se encontraron puertos disponibles para lanzar la ejecución.");
			}
		}
		else {
			logger.warn("No se encontraron puertos disponibles para lanzar la ejecución.");
		}
	}
	
	@Override
	public String generarRegistroParaEnvioHaciaPuertoSerial() {
		HumedadTemperaturaDTO humedadTemperaturaDTO = new HumedadTemperaturaDTO();
		Random valorAleatorio = new Random();
		float temperaturaMinima = 10.0f;
		float temperaturaMaxima = 20.0f;
		float humedadMinima = 20.0f;
		float humedadMaxima = 90.0f;
		float obtenerTemperaturaAleatoria = (float) (Math.random() * (temperaturaMinima - temperaturaMaxima + 1) + temperaturaMaxima);
		humedadTemperaturaDTO.setTemperaturaSensor1((float) (15.381 * 5.1 * obtenerTemperaturaAleatoria / 1024));
		humedadTemperaturaDTO.setTemperaturaSensor2((float) (9196 * 5.1 * obtenerTemperaturaAleatoria / 1024));
		humedadTemperaturaDTO.setHumedadSensor3((float) (5.0 * humedadMinima + valorAleatorio.nextFloat() * (humedadMaxima - humedadMinima)) / 1024);
		return humedadTemperaturaDTO.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza#generarValorAleatorio(java.util.Random, int, int, int)
	 */
	@Override
	public String generarValorAleatorio(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion {
		if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
			throw new GeneracionDatosExcepcion("Put error message here");
		}
		return String.format("%." + decimalPlaces + "f", ((random == null ? new Random() : random).nextDouble() * (upperBound - lowerBound)) + lowerBound);
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza#abrirPuertoSerial(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean abrirPuertoSerial(String nombrePuerto) {
		Boolean puertoEncontrado = false;
		if (puertoSerialParaDatosAbierto != false) {
			logger.warn("El puerto serial se encuentra abierto.");
			return false;
		}
		logger.info("Puerto serial abierto.");
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			idPuertoSerial = (CommPortIdentifier) enumComm.nextElement();
			if (nombrePuerto.contentEquals(idPuertoSerial.getName())) {
				puertoEncontrado = true;
				break;
			}
		}
		if (puertoEncontrado != true) {
			logger.warn("No se encontro el puerto serial: " + nombrePuerto);
			return false;
		}
		abrirPuerto(nombrePuerto);
		if (puertoSerial instanceof SerialPort) {
			ParametrosConfiguracion parametrosConfiguracion = MapeadorParametrosConfiguracion.mapearParametrosConfiguracion(nombrePuerto);
			logger.warn("Se conectó el puerto serial: " + nombrePuerto);
			obtenerCanalEntrada();
			fijarParametrosComunicacionPuerto(parametrosConfiguracion);
			consultarInformacionPuerto(puertoSerial);
			JOptionPane.showMessageDialog(null, "Se ha establecido la conexión con el puerto: " + nombrePuerto, "Conexión exitosa.", JOptionPane.INFORMATION_MESSAGE);
			setPuertoSerialParaDatosAbierto(true);
			return true;
		}
		else {
			logger.warn("ónicamente se aceptan puertos seriales.");
			setPuertoSerialParaDatosAbierto(false);
			return false;
		}
	}

	@Override
	public void abrirPuerto(String nombrePuerto) {
		try {
			puertoSerial = (SerialPort) idPuertoSerial.open(ConstantesEnvioRecepcionDatosRS232.ID_ENVIO_PUERTO_SERIAL_H_Y_T, 500);
		}
		catch (PortInUseException e) {
			logger.error("El puerto: " + nombrePuerto + " se encuentra ocupado.");
		}
	}
	
	@Override
	public void obtenerCanalEntrada() {
		try {
			flujoSalida = puertoSerial.getOutputStream();
		}
		catch (IOException e) {
			logger.error("Ha ocurrido un error con el flujo de salida.");
		}
	}

	@Override
	public void fijarParametrosComunicacionPuerto(ParametrosConfiguracion parametrosConfiguracion) {
		try {
			puertoSerial.setSerialPortParams(parametrosConfiguracion.getTasaBaudios(), parametrosConfiguracion.getBitDatos(), parametrosConfiguracion.getBitParada(), parametrosConfiguracion.getParidad());
			puertoSerial.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			puertoSerial.setDTR(true);
			puertoSerial.disableReceiveTimeout();
			puertoSerial.enableReceiveThreshold(1);

		}
		catch (UnsupportedCommOperationException e) {
			logger.error("La interfaz no soporta la configuracion.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza#puertoSerialCompleto()
	 */
	@Override
	public void puertoSerialCompleto() {
		if (getPuertoSerialParaDatosAbierto() == true) {
			if (puertoSerial != null) {
				puertoSerial.close();
				puertoSerial = null;
				setPuertoSerialParaDatosAbierto(false);;
				logger.info("La conexión con el puerto serial: " + nombrePuerto + " ha sido cerrada.");
				JOptionPane.showMessageDialog(null, "Termina la ejecución del envío de datos desde el puerto: " + nombrePuerto + ".", "Terminación del programa.", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		}
		else {
			logger.info("El puerto serial se encuentra cerrado. ");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza#enviarDatoPuertoSerial(java.lang.String)
	 */
	@Override
	public void enviarDatoPuertoSerial(String mensaje) {
		if (puertoSerialParaDatosAbierto != true)
			return;
		try {
			flujoSalida.write(mensaje.getBytes());
			flujoSalida.flush();
		}
		catch (IOException e) {
			logger.error("Se ha presentado un error al enviar el dato de los sensores de humedad y temperatura. ");
			System.exit(1);
		}
	}
	
	@Override
	public void consultarInformacionPuerto(SerialPort puertoSerial) {
		logger.info("Nombre del puerto: " + puertoSerial.getName());
		logger.info("Tasa de Baudios: " + puertoSerial.getBaudRate());
		logger.info("Bits de Datos: " + puertoSerial.getDataBits());
		logger.info("Bits de Parada: " + puertoSerial.getStopBits());
		logger.info("Paridad:  " + puertoSerial.getParity());
	}
	
	public Boolean getPuertoSerialParaDatosAbierto() {
		return puertoSerialParaDatosAbierto;
	}
	
	public void setPuertoSerialParaDatosAbierto(Boolean puertoSerialParaDatosAbierto) {
		this.puertoSerialParaDatosAbierto = puertoSerialParaDatosAbierto;
	}
}