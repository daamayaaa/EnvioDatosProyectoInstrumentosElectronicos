package com.damaya.monitoreo.rs232.flujomasico;

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
import com.damaya.monitoreo.rs232.dto.EW32907DTO;
import com.damaya.monitoreo.rs232.excepciones.GeneracionDatosExcepcion;
import com.damaya.monitoreo.rs232.interfaces.IEnvioDatosHaciaPuertoSerial;
import com.damaya.monitoreo.rs232.interfaces.IEnvioDatosRS232FlujoMasico;
import com.damaya.monitoreo.rs232.mapeo.MapeadorParametrosConfiguracion;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * The Class EnvioDatosRS232InstrumentoEW32907.
 */
public class EnvioDatosRS232InstrumentoFlujo implements IEnvioDatosRS232FlujoMasico, IEnvioDatosHaciaPuertoSerial, Runnable, Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The id puerto serial. */
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
	 * Instantiates a new envio datos r s232 instrumento e w32907.
	 */
	public EnvioDatosRS232InstrumentoFlujo() {
		super();
		setPuertoSerialParaDatosAbierto(false);
		listaPuertos = FuncionesUtilitarias.obtenerListaPuertos();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Integer segundosRestantes = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el tiempo de ejecución de la aplicación: ", "Tiempo de ejecución", JOptionPane.INFORMATION_MESSAGE));
		listaPuertosAsVector = listaPuertos.toArray(new String[listaPuertos.size()]);
		if (listaPuertosAsVector != null || !ArrayUtils.isEmpty(listaPuertosAsVector)) {
			if (FuncionesUtilitarias.existeValor(listaPuertosAsVector, 1)) {
				nombrePuerto = (String) JOptionPane.showInputDialog(null, "Seleccione un puerto para inicar el envio de datos del instrumento de flujo: ", "Selecci�n de datos de puerto serial", JOptionPane.QUESTION_MESSAGE, null, listaPuertosAsVector, listaPuertosAsVector[1]);
				if (nombrePuerto != null) {
					if (abrirPuertoSerial(nombrePuerto) != true) {
						return;
					}
					while (segundosRestantes > 0) {
						segundosRestantes--;
						logger.info("Se inicia el envío de datos del instrumento de flujo suministrando un tiempo de : " + segundosRestantes + " ms.");
						try {
							String datoSimuladoHiperTerminal = generarRegistroParaEnvioHaciaPuertoSerial();
							enviarDatoPuertoSerial(datoSimuladoHiperTerminal);
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {
							logger.error("El hilo ha sido interrumpido mientras dorm�a: " + e.getMessage());
							Thread.currentThread().interrupt();
						}
					}
					puertoSerialCompleto();
				}
				else {
					logger.error("El puerto no puede permanecer vac�o. Por tanto termina la ejecución del programa.");
					System.exit(1);
				}
			}
			else {
				logger.error("No se encontraron puertos disponibles para lanzar la ejecución.");
				System.exit(1);
			}
		}
		else {
			logger.warn("No se encontraron puertos disponibles para lanzar la ejecución.");
		}
	}
	
	@Override
	public String generarRegistroParaEnvioHaciaPuertoSerial() {
		try {
			EW32907DTO ew32907dto = new EW32907DTO();
			Random valorAleatorio = new Random();
			ew32907dto.setPresionAbsolutaPSIA("+" + generarValorAleatorio(valorAleatorio, 0, 30, 2));
			ew32907dto.setTemperatura("+" + generarValorAleatorio(valorAleatorio, 0, 20, 2));
			ew32907dto.setVolumenFlujo("+" + generarValorAleatorio(valorAleatorio, 0, 5, 2));
			ew32907dto.setFlujoMasa("+" + generarValorAleatorio(valorAleatorio, 0, 5, 3));
			ew32907dto.setValorPuestaPunto(generarValorAleatorioTresDecimales(valorAleatorio, 0, 30, 2));
			ew32907dto.setGas("Air");
			return ew32907dto.toString();
		}
		catch (GeneracionDatosExcepcion e) {
			logger.error("Ha ocurrido un error con la generación del registro a enviar hacia el puerto serial.");
			return "NO_DATA";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232FlujoMasico# generarValorAleatorio(java.util.Random, int, int, int)
	 */
	@Override
	public String generarValorAleatorio(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion {
		if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
			throw new GeneracionDatosExcepcion("Put error message here");
		}
		return FuncionesUtilitarias.generarValorAleatorioComoString("0%.", random, lowerBound, upperBound, decimalPlaces);
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232FlujoMasico# generarValorAleatorioTresDecimales(java.util.Random, int, int, int)
	 */
	@Override
	public String generarValorAleatorioTresDecimales(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion {
		if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
			throw new GeneracionDatosExcepcion("Put error message here");
		}
		return FuncionesUtilitarias.generarValorAleatorioComoString("00%.", random, lowerBound, upperBound, decimalPlaces);
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232FlujoMasico# generarValorAleatorioDosDecimales(java.util.Random, int, int, int)
	 */
	@Override
	public String generarValorAleatorioDosDecimales(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion {
		if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
			throw new GeneracionDatosExcepcion("Put error message here");
		}
		return FuncionesUtilitarias.generarValorAleatorioComoString("000%.", random, lowerBound, upperBound, decimalPlaces);
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232FlujoMasico# abrirPuertoSerial(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean abrirPuertoSerial(String nombrePuerto) {
		Boolean puertoEncontrado = false;
		if (puertoSerialParaDatosAbierto != false) {
			logger.info("El puerto serial se encuentra abierto.");
			return false;
		}
		logger.info("Puerto serial: " + nombrePuerto + " ha sido abierto.");
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			idPuertoSerial = enumComm.nextElement();
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
			puertoSerialParaDatosAbierto = true;
			return true;
		}
		else {
			logger.warn("Unicamente se aceptan puertos seriales.");
			puertoSerialParaDatosAbierto = false;
			return false;
		}
	}
	
	@Override
	public void abrirPuerto(String nombrePuerto) {
		try {
			puertoSerial = (SerialPort) idPuertoSerial.open(ConstantesEnvioRecepcionDatosRS232.ID_ENVIO_PUERTO_SERIAL_INSTRUMENTO_FLUJO, ConstantesEnvioRecepcionDatosRS232.TIEMPO_APERTURA_PUERTO_MILISEGUNDOS);
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
	
	@Override
	public void consultarInformacionPuerto(SerialPort puertoSerial) {
		logger.info("Nombre del puerto: " + puertoSerial.getName());
		logger.info("Tasa de Baudios: " + puertoSerial.getBaudRate());
		logger.info("Bits de Datos: " + puertoSerial.getDataBits());
		logger.info("Bits de Parada: " + puertoSerial.getStopBits());
		logger.info("Paridad:  " + puertoSerial.getParity());
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232FlujoMasico# puertoSerialCompleto()
	 */
	@Override
	public void puertoSerialCompleto() {
		if (puertoSerialParaDatosAbierto == true) {
			if (puertoSerial != null) {
				logger.info("Se cierra el puerto serial.");
				puertoSerial.close();
				puertoSerialParaDatosAbierto = false;
			}
		}
		else {
			logger.warn("El puerto serial se encuentra cerrado. ");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232FlujoMasico# enviarDatoPuertoSerial(java.lang.String)
	 */
	@Override
	public void enviarDatoPuertoSerial(String mensaje) {
		if (puertoSerialParaDatosAbierto != true) {
			return;
		}
		try {
			flujoSalida.write(mensaje.getBytes());
			flujoSalida.flush();
		}
		catch (IOException e) {
			logger.error("Error al enviar el dato de la balanza. ");
		}
	}
	
	public Boolean getPuertoSerialParaDatosAbierto() {
		return puertoSerialParaDatosAbierto;
	}
	
	public void setPuertoSerialParaDatosAbierto(Boolean puertoSerialParaDatosAbierto) {
		this.puertoSerialParaDatosAbierto = puertoSerialParaDatosAbierto;
	}
}