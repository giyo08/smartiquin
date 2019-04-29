# Smartiquín
Proyecto para la cátedra SOA de la UNLaM

**Integrantes**

  * Alberto, Gisele Romina	   	34.154.196
  * Benítez, Gerardo Abel		    25.883.409
  * Coradini, Gonzalo Fabián		38.946.395
  * Vázquez, Lucia Maité	    	40.886.803

**Descripción**

*Smartiquín* es un botiquín de madera que te facilita la gestión de los medicamentos que estás tomando: te sugerirá qué medicamento ingerir dependiendo de si es mañana o noche -a modo de recordatorio-, te contará la cantidad de veces que ingeriste determinado medicamento a lo largo del día, te respaldará diciendote si falta algún medicamento y te informará si hay demasiada humedad dentro. Para todo esto, Smartiquín estará conectado a una aplicación móvil por donde podrás ver los datos anteriormente comentados. Además, el sistema cuenta con una apertura supervisada y segura: necesitarás la aplicación móvil para pedir que el botiquín se destrabe y puedas acceder a su contenido (necesitarás también la aplicación para hacer el bloqueo); de ésta forma, se restringe el acceso a los medicamentos al usuario de la aplicación, manteniéndolos fuera del alcance de los niños.

**Hardware a utilizar**

  Sensores: *sensor de humedad, switch, sensor de luz.*

  Actuadores: *luces led, buzzer, servo motor.*

  Otros: *placa Arduino, 2 potencias de 5v, cables, resistencias, relay, protoboard.*

**Descripción de la implementación**

El arduino y la aplicación móvil se comunicarán mediante bluetooth. Los medicamentos se colocarán arriba de los switchs, con los que se interpretará una ingesta o un faltante de medicamento cada vez que cambie de estado - dependiendo del tiempo que pase-. El servo de colocará de forma que trabe la puerta y, al mandarle una señal desde la aplicación por medio de bluetooth, el arduino moverá el servo destrabándola. En el botiquín habrá un sensor de luz y otro de humedad: el primero se colocará en el exterior, se utilizará para determinar el período del día que es (mañana, tarde o noche) y luego, notificar al usuario qué medicamento tomar (asociado al período); el segundo se colocará en el interior y se utilizará para efectivizar, cuando la humedad supere cierto valor apto, el envío de una notificación al usuario y la reproducción de un sonido de alerta con el buzzer. Los leds se dispondrán en el frente del botiquín, uno para informar si la puerta está cerrada o abierta, y los demás para ver fácilmente la disponibilidad de los medicamentos sin tener que abrir la aplicación ni el botiquín.
