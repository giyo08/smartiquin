# Smartiquin: Diseño Incremental
Vamos a describir los diferentes pasos que recorrimos para llegar a la version final de nuestro prototipo

## Version 01: conexión basica, leds y pulsadores
Para la versión inicial vamos a usar como sensores algunos pulsadores, y variables preconfiguradas y como actuadores vamos a usar leds. 
En nuestra configuracion inicial usaremos

### Componentes: 
Servo = led rojo
Buzzer = Led azul
SensorHumedad = Variable
SensorLuz = SensorLuz
Switch01 = pulsador01
Switch02 = pulsador02
Switch03 = pulsador03

### Procedimiento 
Conexiones componente a componente
* LedRojo a PinDigital03
* LedAzul a PinDigital04
* Sensor de Luz a PinAnalogico01
* Pulsador01 a PinDigital07
* Pulsador02 a PinDigital08
* Pulsador03 a PinDigital09