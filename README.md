# Smartiquín
Proyecto para la cátedra SOA de la UNLaM

**Integrantes**

  * Alberto, Gisele Romina	   	34.154.196
  * Benítez, Gerardo Abel		    25.883.409
  * Coradini, Gonzalo Fabián		38.946.395
  * Vázquez, Lucia Maité	    	40.886.803

**Descripción**

Smartiquín es un botiquín de madera que te facilita la gestión de los medicamentos que estás tomando: ya que mantendrá un registro de los medicamentos, descontando la cantidad que se ingiere, te respalda diciendote si falta algún medicamento y te informará si hay demasiada humedad o luz dentro. Para todo esto, Smartiquín estará conectado a una aplicación móvil, en donde se podrá ver los datos de los medicamentos con los que se cuenta actualmente en el botiquín y las cantidades pertinentes. En la misma, se recibirán una serie de notificaciones para que el usuario esté informado en tiempo real del estado de sus medicamentos; las mismas notifican:
  * Cuando hay luz dentro del botiquín estando la puerta cerrada.
  * Cuando hay humedad en el mismo.
  * Cuando un medicamento está vencido.
  * Cuando de un medicamento hay pocas pastillas.
  * Cuando de un medicamento no hay más pastillas.
Además, el sistema cuenta con una apertura supervisada y segura: necesitarás la aplicación móvil para pedir que el botiquín se destrabe y puedas acceder a su contenido (necesitarás también la aplicación para hacer el bloqueo); de ésta forma, se restringe el acceso a los medicamentos al usuario de la aplicación, manteniéndolos fuera del alcance de los niños.


**Hardware a utilizar**

  * *Arduino UNO R3
  * *Protoboard de 150 puntos
  * *Sensor de humedad DHT11
  * *3 Switch fin de carrera
  * *Fotoresistor LDR
  * *Luces LED
  * *Módulo Bluetooth HC-05
  * *Buzzer activo
  * *Electroimán 12v
  * *Relay
  * *Cargador 5v
  * *Cargador 12v
  * *Cables de conexión
  * *Resistencias

**Software Utilizado**

Arduino IDE 1.8.9
Android Studio 3.4.1


**Descripción de la implementación**

El arduino y la aplicación móvil se comunicarán mediante bluetooth. En este caso utilizamos un módulo bluetooth para el arduino, de modelo HC-05.
Los medicamentos se colocarán arriba de los switchs, con los que se interpretará una ingesta de medicamento (una pastilla). 
El electroimán se mantendrá energizado de forma que trabe la puerta y, al mandarle una señal desde la aplicación, ya sea a través del respectivo botón o habilitando el shake del celular, el arduino desmagnetiza el imán destrabando la puerta. Para facilitar su implementación, utilizamos un relé para la interconexión entre arduino-electroimán.
En el botiquín habrá un sensor de luz y otro de humedad: el primero se colocará en el interior para determinar si dentro del botiquín cerrado hay presencia de luz, de modo que se pueda notificar al usuario cuando el botiquín no esté en condiciones. El segundo se colocará en el interior y se utilizará para efectivizar, cuando la humedad supere cierto valor apto, el envío de una notificación al usuario. Ambos, cuando tomen valores no deseados, activan la reproducción de un sonido de alerta con el buzzer. 
Los leds se dispondrán dos en el frente del botiquín, uno para informar si la puerta está cerrada - rojo- o abierta - verde-, y el restante, en el interior del botiquín para alumbrar el interior del mismo cuando el mismo esté abierto y en el ambiente no haya luz suficiente para ver los medicamentos.

**Manual de usuario**
 * Primeros pasos:

    * *Conexion Android-Smartiquín
    * *El usuario encenderá el Smartiquín.
    * *El usuario descargara e ingresara a la aplicación de Smartiquín en su dispositivo android (*).
    * *Debe permitir el encendido del bluetooth.
    * *A continuación deberá presionar “Conectar con Arduino”.
    * *Luego de unos segundos el Smartiquín estará conectado exitosamente con su dispositivo Android.

	(*) Versión mínima de android : android 6.0 MarshMallow

**Más información en:https://github.com/giyo08/smartiquin/blob/master/Informe-Final/Informe%20Final.pdf**
