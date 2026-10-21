package com.damaya.monitoreo.rs232.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.damaya.monitoreo.rs232.constantes.ConstantesEnvioRecepcionDatosRS232;
import gnu.io.CommPortIdentifier;

public class FuncionesUtilitarias implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(FuncionesUtilitarias.class);
	
	@SuppressWarnings("unused")
	public static boolean existeValor(String[] arreglo, int indice) {
		boolean resultado = false;
		try {
			String valor = arreglo[indice];
			resultado = true;
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		return resultado;
	}
	
	public static void cargarConfiguracionLog() {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public static String generarValorAleatorioComoString(String precision, Random random, final int lowerBound, final int upperBound, final int decimalPlaces) {
		final double valorAleatorioGenerado = ((random == null ? new Random() : random).nextDouble() * (upperBound - lowerBound)) + lowerBound;
		String format = String.format(precision + decimalPlaces + "f", valorAleatorioGenerado);
		return format.replaceAll(",", ".");
	}
	
	@SuppressWarnings("rawtypes")
	public static List<String> obtenerListaPuertos() {
		List<String> listaPuertosSeriales = new ArrayList<String>();
		List<String> listaPuertosParalelos = new ArrayList<String>();
		Enumeration listaPuertos = CommPortIdentifier.getPortIdentifiers();
		logger.info("Puertos seriales y paralelos disponiles.");
		while (listaPuertos.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) listaPuertos.nextElement();
			if (portId.getPortType() == ConstantesEnvioRecepcionDatosRS232.ID_COM_PUERTO_SERIAL) {
				listaPuertosSeriales.add(portId.getName());
			}
			else if (portId.getPortType() == ConstantesEnvioRecepcionDatosRS232.ID_COM_PUERTO_PARALELO) {
				listaPuertosParalelos.add(portId.getName());
			}
		}
		if (listaPuertosSeriales == null || listaPuertosSeriales.isEmpty()) {
			logger.warn("No se encontraron puertos seriales disponibles para lanzar la ejecución del programa.");
		}
		else {
			for (String puertoSerial : listaPuertosSeriales) {
				logger.info("Puerto Serial:" + puertoSerial);
			}
		}
		if (listaPuertosParalelos == null || listaPuertosParalelos.isEmpty()) {
			logger.warn("No se encontraron puertos paralelos disponibles para lanzar la ejecución del programa.");
		}
		else {
			for (String puertoParalelo : listaPuertosParalelos) {
				logger.info("Puerto Paralelo:" + puertoParalelo);
			}
		}
		return listaPuertosSeriales;
	}

}