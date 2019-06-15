#include <SoftwareSerial.h>
#include <DHT.h>

<<<<<<< HEAD
SoftwareSerial BTserial(10, 11); // RX | TX
=======
SoftwareSerial BT(10,11); // RX | TX
>>>>>>> master

/*
 * Sensores y actuadores asociados
 */
 
byte switch01 = 3; 
byte switch02 = 5;
byte switch03 = 6;
byte buzzer = 4;
byte LED_Green = 8;
byte LED_Red = 9;
byte electroiman = 13;
byte lampara = 11;

byte fotoresistor = A4;

/*
 * Sensor Temperatura y Humedad
 */
 
#define DHTTYPE DHT11 // Declaramos el modelo de sensor a utilizar
byte DHTPin = 2;
byte HUMEDAD_MIN = 20;
byte HUMEDAD_MAX = 90;

/*
 *  Variables
 */
 
boolean puertaAbierta = true;
//const int abierto = 0;
//const int cerrado = 1;
//const int pulsado = 1;
//const int encendido = 1;
//const int apagado = 0;
//const int abrir_pestillo = 0;

// Relacionadas con los switchs

unsigned long  tiempoSwitch3Libre = 0;
boolean switch3SinPulsar = false;

unsigned long intervaloAlarmaSwitch = 10000;

// Relacionadas con la lampara

int porcentajeDeLuzAnterior = 0;

// Relacionadas con el sensor de luz

const byte LUMINOSIDAD_MIN = 10;
const byte LUMINOSIDAD_MAX = 70;

// Relacionadas al tiempo y las esperas
unsigned long tiempo = 0;
unsigned long tiempo_anterior = 0;
<<<<<<< HEAD
const unsigned long intervalo = 200;
=======
unsigned long intervalo = 2000;

  
>>>>>>> master

// Relacionadas con el bluetooth
char comando_bt;
enum Comandos { ABRIR, ENCONTRAR };

DHT dht(DHTPin, DHTTYPE); // Inicializamos la variable de comunicación entre el sensor y Arduino

byte pulsador01 = 2;


/*
 * ******************** FIN DE DECLARACIONES ********************
 * ******************** COMIENZO DEL PROGRAMA *******************
 */

/**
   Determina el tiempo que se tomara entre un cheque de estados
   y otro.
*/
bool intervalo_cumplido() {
  return ((tiempo - tiempo_anterior) > intervalo);
}

/**
   Determina si se cumplio un intervalo particular de tiempo,
   compara un tiempo inicial y previo contra un tiemp de espera
   Parametros:
   - actual: es el tiempo actual
   - previo: es el tiempo tomado antes de iniciar el proceso a controlar
   - espera: es la cantidad de tiempo que debe transcurrir para indicar si se
   cumplio el intervalo
*/
bool intervalo_particular_cumplido(unsigned long actual, unsigned long previo, unsigned long espera) {
  return ((actual - previo) > espera);
}

/**
   Esta funcion obtiene los valores minimos y maximos de cada
   parametro a comprobar, se comunica con un microservicio
   que le sirve los valores actualizados
*/
void obtener_parametros_externos() {
  //conectarse y obtener valores
}

<<<<<<< HEAD
boolean componenteApagado(byte componente){
  return digitalRead(componente) == 0;
=======
void setup() {
  // put your setup code here, to run once:
  BT.begin(9600);
  Serial.begin(9600);
  dht.begin();
  
  pinMode(buzzer01, OUTPUT);  

  obtener_parametros_externos();
>>>>>>> master
}

/*
 * ******************** HUMIDITY CONTROL ZONE *******************
 */

/**
   Chequea que la humedad no exeda los rangos minimos y maximos
   caso contrario, actua en consecuencia llamando a los actuadores
   correspondientes.
*/
void chequear_humedad() {
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  if (isnan(h) || isnan(t)) {
    Serial.print("Falló al leer del sensor");
    return;
  }

  if ( h < HUMEDAD_MIN || h > HUMEDAD_MAX ) {
    Serial.print("Humedad: ");
    Serial.print(h);
    Serial.print("%\t");
  hacer_sonar_melodia("ccggaagc");
  }
}


/*
 * ******************** LIGHT CONTROL ZONE *******************
 */

/**
   Chequea que la luminosidad no exeda los rangos minimos y maximos
   caso contrario, actua en consecuencia llamando a los actuadores
   correspondientes
*/
void chequear_luminosidad() {
  Serial.print("\nLuminosidad: ");
  Serial.print(analogRead(fotoresistor));
};
/*
 * En la aplicación de android tendríamos que mandar por msj el nivel de luz que querémos, y acá directamente llamar a esta función
 * con el porcentaje requerido de luz.
 * Como no tiene resistencia, el 100% tendria que ser 128.
 */
void prender_lampara(int porcentaje){
  int i;
  if( porcentajeDeLuzAnterior < porcentaje)
    for(i = (porcentajeDeLuzAnterior*128/100)+1; i<= (porcentaje*128/100); i++){
      analogWrite(lampara, i);
      delay(35);
    }
  if( porcentajeDeLuzAnterior > porcentaje)
    for(i = (porcentajeDeLuzAnterior*128/100)-1; i >= (porcentaje*128/100); i--){
      analogWrite(lampara, i);
      delay(35);
    }
  porcentajeDeLuzAnterior = porcentaje;
}
/*
 * 255 es el 100% del voltaje proporcionado.
 * 128 es la mitad, en este caso 2,5v
 * 
 */ 
void prender_lampara(){
  int i;
  for(i = 0; i<= 128; i++){
    analogWrite(lampara, i);
    delay(25);
  }
  for(i = 128; i>=0; i--){
    analogWrite(lampara, i);
    delay(25);
  }
}

/*
 * ******************** DOOR CONTROL ZONE *******************
 */

void cerrar_botiquin(){
  /*Activar rele*/
  if( componenteApagado(electroiman))
    digitalWrite(electroiman, HIGH);
  /*Encender led rojo*/
  puertaAbierta = false;
  cambiar_estado_puerta();
  hacer_sonar_melodia("ccggaagc");
}

void abrir_botiquin() {
  /*Desactivar rele*/
  if( !componenteApagado(electroiman))
   digitalWrite(electroiman, LOW);
  /*Encender led verder*/
  puertaAbierta = true;
  cambiar_estado_puerta();
  
  hacer_sonar_melodia("defaafaa");
}

void cambiar_estado_puerta(){
   if( !puertaAbierta ){
        digitalWrite( LED_Red, HIGH);   
        digitalWrite( LED_Green, LOW);
   }
   else{
        digitalWrite( LED_Green, HIGH);
        digitalWrite( LED_Red, LOW);
   }
}


/*
 * ******************** SWITCH CONTROL ZONE *******************
 */

 
/**
  Este metodo se usa para identificar que alguno de los switch
  han sido desactivados, es decir, se tomo la caja de medicamentos
  de uno de los slots
*/
void chequear_extraccion() {
  actuar_switch01();
  actuar_switch02();
  actuar_switch03();
}

void actuar_switch01() {
  byte switchPulsado = digitalRead(switch01);
  if (switchPulsado) {
    cerrar_botiquin();
  }
}

void actuar_switch02() {
  byte switchPulsado = digitalRead(switch02);
  if (switchPulsado) {
    abrir_botiquin();
    //tambien prendo el buzzer, me gusta hacer ruido
    //hacer_sonar_melodia();
    //tone(buzzer01, 3000, 500);
  }
}

void actuar_switch03() {
    byte switchPulsado = digitalRead(switch03); //1 => pulsado | 0 => no pulsado
    if(!switchPulsado){ //se saco el medicamento 
      if( !switch3SinPulsar ){ //primera vez que entra al loop de switch sin pulsar
        switch3SinPulsar = true;
        tiempoSwitch3Libre = tiempo;
        /*
         * supongo que aca se manda la info de que quito un medicamento al celular, tipo "s3/1"
         */
      }
      /*
       * Condicional inferior: [poner corte por parte del cliente en la condicion] here
       * Se puede crear otra variable global, la que sea un booleano. Si el cliente quiere dejar de hacer sonar la alarma
       * boolean pararAlarma = false; => el usuario la pondria como pararAlarma = true;
       * 
       * El problema que encuentro es que si los 3 chips están sonando al mismo tiempo para a sonar un ruido horrible.
       * Habria que buscar que buscar la forma de que suene una al mismo tiempo
       * 
       * Se puede poner otra variable global que sea: boolean alarmaSonando = false;
       * 
       * Asi cuando una alarma empieza a sonar, imposibilita a las otras a hacerlo.
       */
      if( /*!pararAlarma && !alarmaSonando && */intervalo_particular_cumplido(tiempo, tiempoSwitch3Libre, intervaloAlarmaSwitch)) {
        
        hacer_sonar_melodia("aaaffggd");
        //alarmaSonando = true;
      }
     
    }
    else{ // el switch3 esta pulsado
      switch3SinPulsar = false;
      //pararAlarma = false;
      //alarmaSonando = false;
    }
}


/*
 * ******************** BUZZER ZONE *******************
 */
 
/**
 * La idea de esta función ahora fue hacerla generica, despues solo tendría que ponerse
 * las llamadas a la función directamente, sin tanto calculo.
 */
 void hacer_sonar_melodia(char notes[]) {
    int length = 8; // the number of notes
    //char notes[] = "ccggaagc";//"ccggaagffeeddc"; // a space represents a rest ccggaagffeeddc
    int beats[] = {1,2,1,1,4,2,1,1};//{ 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 4 };
    int tempo = 100;
  
    for (int i = 0; i < length; i++) {
    if (notes[i] == ' ') {
      delay(beats[i] * tempo); // rest
    } else {
      playNote(notes[i], beats[i] * tempo);
    }

    // pause between notes
    delay(tempo / 2);
  }
}

<<<<<<< HEAD
void playNote(char note, int duration) {
  char names[] = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'C' };
  int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956 };
  int i;
  // play the tone corresponding to the note name
  for (i = 0; i < 8; i++) {
    if (names[i] == note) {
      noTone(buzzer);
      tone(buzzer, tones[i], duration);
=======
void loop() {
  // put your main code here, to run repeatedly:
  tiempo = millis();
  if ( intervalo_cumplido() ) {
    //chequear_humedad();
    //chequear_luminosidad();
    //chequear_extraccion();
    tiempo_anterior = tiempo;

    // Probando envio de mensajes entre arduino y app generica
    if(BT.available()) { 
      leer_bluetooth();
>>>>>>> master
    }

    if(Serial.available()) {
      escribir_bluetooth();
    }
    
  }
}

<<<<<<< HEAD
/*
 * ******************** BLUETOOTH ZONE *******************
 */

void leer_bluetooth() {
  //comando_bt = BTserial.read();
  comando_bt = Serial.read();
  analizar_comando(comando_bt);
}

void analizar_comando(char comando) {
  switch (comando) {
    case 'a':
      abrir_botiquin();
      break;
    case 'e': //Accion para encontrar al botiquin, si, se puede perder...
      hacer_sonar_melodia("ccggaagc");
      break;
  }
=======
void leer_bluetooth(){
  // Lee comando enviado desde terminal bluetooth
  comando_bt = BT.read();
  
  analizar_comando(comando_bt);
}

void escribir_bluetooth() {
  // Lee comando enviado desde terminal serial
  comando_bt = Serial.read(); 

  analizar_comando(comando_bt);
}

void analizar_comando(char comando){
  switch(comando){
    case 'a': 
      //abrir_botiquin(); 
      // Temporal para probar la comunicacion BT a Arduino
      Serial.println("Abre botiquin");
      break; 
    case 'e': //Accion para encontrar al botiquin, si, se puede perder...
      //encender_buzzer(2000, 600);  
      // Temporal para probar la comunicacion BT a Arduino
      Serial.println("Apagar buzzer");   
      break; 
    case 's':
      // Temporal para probar la comunicacion Arduino a BT
      BT.write("Mandamos estado de la puerta/sensores \n");
      break;
  }  
>>>>>>> master
}

/*
 * ******************** CONTROL ZONE *******************
 */

void setup() {
  
  pinMode(buzzer, OUTPUT);
  pinMode(electroiman, OUTPUT);
  pinMode(lampara, OUTPUT);
  
  Serial.begin(9600);
  dht.begin();
  
  obtener_parametros_externos();
  
}

void loop() {
  
  tiempo = millis();
  
  if ( intervalo_cumplido() ) {
    //chequear_humedad();
    chequear_luminosidad();
    chequear_extraccion();

   analogWrite(lampara,128);
    
    
    tiempo_anterior = tiempo; 

    //if(BTserial.available())
    if (Serial.available())
    {
      leer_bluetooth();
    }
  }
}
