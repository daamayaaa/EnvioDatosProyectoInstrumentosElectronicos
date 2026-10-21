# Sistema de Monitoreo y Control de Instrumentos Electrónicos

> **Autor:** Ing. Diego Alejandro Amaya Arjona  
> **Proyecto:** Aplicación visual en Java para la adquisición, procesamiento y visualización en tiempo real de datos mediante comunicación serial.

---

## Resumen Ejecutivo & Objetivos

| Elemento | Descripción |
| :--- | :--- |
| **Problema** | Necesidad de capturar, procesar y visualizar de forma centralizada variables físicas (humedad, temperatura, flujo, volumen, peso) desde múltiples instrumentos electrónicos. |
| **Solución** | Estación centralizada de monitoreo desarrollada en Java con gráficos dinámicos en tiempo real y exportación automática de reportes. |
| **Alcance** | Monitoreo y control continuo sobre **3 instrumentos clave** conectables por puerto serial. |

---

## Instrumentos y Variables Medidas

```
  +-----------------------------------------------------------------------+
  |                      ESTACIÓN CENTRAL DE MONITOREO                    |
  +-----------------------------------+-----------------------------------+
                                      |
       +------------------------------+------------------------------+
       |                              |                              |
       v                              v                              v
+--------------+               +--------------+               +--------------+
| Termocuplas  |               |   Balanza    |               |  Sensor de   |
| (Arduino/K)  |               |   Digital    |               |    Flujo     |
+--------------+               +--------------+               +--------------+
| - Temp. T1   |               | - Peso Real  |               | - Presión    |
| - Temp. T2   |               |   (kg/g)     |               | - Temp (C)  |
| - Humedad    |               |              |               | - Flow (LPM) |
+--------------+               +--------------+               +--------------+
```

---

## Flujo de Implementación del Sistema

```
 [1. Comunicación]  -> 	  [2. Recepción]   -> 	[3. Procesamiento] -> [4. Interfaz / GUI]
  - Puerto Serial         - Captura de          - Validaciones         - Tablas dinámicas
  - RS-232 / USB            datos brutos        - Fórmulas de          - Gráficos JFreeChart
  - Drivers Java                                  calibración          - Exportación PDF/Excel
```

---

## Ajustes y Fórmulas de Calibración (Arduino)

Para garantizar la precisión de los datos analógicos leídos, se aplican las siguientes transformaciones matemáticas:

* **Temperatura 1 ($T_1$):**
  $$T_1 = \frac{15.381 \times 5.1 \times \text{lectura}_1}{1024}$$

* **Temperatura 2 ($T_2$):**
  $$T_2 = \frac{9.196 \times 5.1 \times \text{lectura}_2}{1024}$$

* **Humedad ($H_3$):**
  $$H_3 = \frac{5.0 \times \text{lectura}_3 \times 100}{1024}$$

---

## Configuración del Puerto Serial (RS-232 / COM)

```
       +----------------------------------------------------+
       |         Parámetros de Conexión Serial              |
       +-----------------------+----------------------------+
       | Tasa de Baudios       | 9600 bps                   |
       | Bits de Datos         | 8 bits                     |
       | Paridad               | Ninguna (0)                |
       | Bits de Parada        | 1 bit                      |
       | Control de Flujo      | Hardware / None            |
       +-----------------------+----------------------------+
```

---

## Arquitectura de Software

```
                         +--------------------------+
                         |    Cliente del Proceso   |
                         +------------+-------------+
                                      |
         +----------------------------+----------------------------+
         |                            |                            |
         v                            v                            v
+------------------+        +------------------+        +------------------+
| Manejador Datos  |        | Manejador Chart  |        | Manejador Reporte|
|   del Proceso    |        |   (JFreeChart)   |        |   (Apache POI)   |
+------------------+        +------------------+        +------------------+
         |                            |                            |
         +----------------------------+----------------------------+
                                      |
       +------------------------------+------------------------------+
       |                              |                              |
       v                              v                              v
+--------------+               +--------------+               +--------------+
| Driver Peso  |               | Driver Flujo |               | Driver Temp  |
| (Mettler)    |               | (EW-32907)   |               |  (Arduino)   |
+--------------+               +--------------+               +--------------+
```

---

## Tech Stack

* **Lenguaje:** Java 
* **IDE:** Eclipse + WindowBuilder
* **Librería de Gráficos:** JFreeChart
* **Comunicación Serial:** RXTXcomm / Java Communications API
* **Generación de Reportes:** Apache POI (PDF y Excel)
* **Hardware & Emulación:** Arduino IDE, VirtualSerialPort

---

## Desafíos y Restricciones Técnicas

1. **Procesamiento Paralelo Limitado:** Desafíos de eficiencia al leer múltiples puertos simultáneamente.
2. **Dependencia de Calibración:** Alto acoplamiento con la velocidad y formato de respuesta de hardware específico.
3. **Carga Computacional:** Las gráficas multivariable en tiempo real requieren recursos estables en el equipo host.

---

##  Conclusiones

* Se logró una interfaz gráfica funcional, modular e intuitiva para el control de variables físicas heterogéneas.
* El manejo de eventos seriales en Java permitió una recepción eficiente y estructurada de tramas de datos.
* La automatización de reportes (PDF/XLS) optimiza la toma de decisiones y el almacenamiento histórico de las lecturas.
