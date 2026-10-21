package com.damaya.monitoreo.rs232.dto;

import java.io.Serializable;

/**
 * The Class HumedadTemperaturaDTO.
 */
public class HumedadTemperaturaDTO implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The temperatura sensor1. */
	private float temperaturaSensor1;
	
	/** The temperatura sensor2. */
	private float temperaturaSensor2;
	
	/** The humedad sensor3. */
	private float humedadSensor3;

	/**
	 * Instantiates a new humedad temperatura dao.
	 */
	public HumedadTemperaturaDTO() {
		super();
		temperaturaSensor1 = 0F;
		temperaturaSensor2 = 0F;
		humedadSensor3 = 0F;
	}

	/**
	 * Instantiates a new humedad temperatura dao.
	 *
	 * @param temperaturaSensor1 the temperatura sensor1
	 * @param temperaturaSensor2 the temperatura sensor2
	 * @param humedadSensor3 the humedad sensor3
	 */
	public HumedadTemperaturaDTO(float temperaturaSensor1, float temperaturaSensor2, float humedadSensor3) {
		super();
		this.temperaturaSensor1 = temperaturaSensor1;
		this.temperaturaSensor2 = temperaturaSensor2;
		this.humedadSensor3 = humedadSensor3;
	}

	/**
	 * Gets the temperatura sensor1.
	 *
	 * @return the temperatura sensor1
	 */
	public float getTemperaturaSensor1() {
		return temperaturaSensor1;
	}

	/**
	 * Sets the temperatura sensor1.
	 *
	 * @param temperaturaSensor1 the new temperatura sensor1
	 */
	public void setTemperaturaSensor1(float temperaturaSensor1) {
		this.temperaturaSensor1 = temperaturaSensor1;
	}

	/**
	 * Gets the temperatura sensor2.
	 *
	 * @return the temperatura sensor2
	 */
	public float getTemperaturaSensor2() {
		return temperaturaSensor2;
	}

	/**
	 * Sets the temperatura sensor2.
	 *
	 * @param temperaturaSensor2 the new temperatura sensor2
	 */
	public void setTemperaturaSensor2(float temperaturaSensor2) {
		this.temperaturaSensor2 = temperaturaSensor2;
	}

	/**
	 * Gets the humedad sensor3.
	 *
	 * @return the humedad sensor3
	 */
	public float getHumedadSensor3() {
		return humedadSensor3;
	}

	/**
	 * Sets the humedad sensor3.
	 *
	 * @param humedadSensor3 the new humedad sensor3
	 */
	public void setHumedadSensor3(float humedadSensor3) {
		this.humedadSensor3 = humedadSensor3;
	}
	
	@Override
	public String toString() {
		return "\n" + temperaturaSensor1 + "\n" + temperaturaSensor2 + "\n" + humedadSensor3 + "\n";
	}
}
