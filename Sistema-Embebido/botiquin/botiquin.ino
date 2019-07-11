#include <SoftwareSerial.h>
#include <DHT.h>

// Constantes de interpretación de comunicación BT

#define PARAR_ALARMA 'L'
#define ABRIR_PUERTA 'A'
#define CERRAR_PUERTA 'C'
#define LAMPARA_APAGADA 'P'
#define LAMPARA_PRENDIDA_A_MEDIAS 'T'
#define LAMPARA_PRENDIDA 'Z'
#define HUMEDAD_ELEVADA 'H'
#define LUZ_ELEVADA 'L'
#define SWITCH3_DESACTIVADO '3'
#define SWITCH2_DESACTIVADO '2'
#define SWITCH1_DESACTIVADO '1'
  
/*
 * Sensores y actuadores asociados
 */
 
byte switch01 = 3; 
byte switch02 = 5;
byte switch03 = 6;
byte buzzer = 4;
byte LED_Green = 7;
byte LED_Red = 12;
byte lampara = 9;
byte electroiman = 8;
byte DHTpin = 2;
byte RX = 10;
byte TX = 11; 

byte fotoresistor = A4;

SoftwareSerial Bt1(RX, TX);



/*
 * Sensor Temperatura y Humedad
 */
 
#define DHTTYPE DHT11 // Declaramos el modelo de sensor a utilizar
DHT dht(DHTpin, DHTTYPE); // Inicializamos la variable de comunicación entre el sensor y Arduino


/*
 *  Variables
 */

char digitoLeidoBT = ' ';
float valorLuminosidadLeido;

boolean puertaAbierta = true;

// Relacionadas con los switchs

boolean switch1SinPulsarRecientemente = true; // Variable de control de corte
boolean switch2SinPulsarRecientemente = true; // Variable de control de corte
boolean switch3SinPulsarRecientemente = true; // Variable de control de corte

// Relacionadas con el sensor de luz

const int LUMINOSIDAD_MAX = 800; // luminosidad máxima admitida dentro del botiquín
boolean luzNoPermitidaRecientemente = true; // Variable de control de corte

// Relacionadas con el sensor de temperatura y humedad

const byte HUMEDAD_MAX = 80; // humedad máxima permitida dentro del botiquín
boolean humedadNoPermitidaRecientemente = true; // Variable de control de corte
float humedadLeida;

unsigned long tiempo_anterior_lecturaHumedad = 0;
const unsigned long INTERVALO_LECTURA_HUMEDAD = 5000;

// Relacionadas al tiempo y las esperas

unsigned long tiempo = 0;
unsigned long tiempo_anterior = 0;
const unsigned long INTERVALO_LOOP_PRINCIPAL = 200;

// Relacionadas con la alarma

boolean alarmaApagada = false;
boolean alarmaDeHumedad = false;
boolean alarmaDeLuz = false;
boolean alarmaApagadaRecientemente = true; // Variable de control de corte
unsigned long tiempoAlarmaFueApagada;
const unsigned long INTERVALO_ALARMA_SIN_SONAR = 10000; // en milisegundos



void setup() {
  pinMode(buzzer, OUTPUT);
  pinMode(electroiman, OUTPUT);
  pinMode(lampara, OUTPUT);
  pinMode(LED_Green, OUTPUT);
  pinMode(LED_Red, OUTPUT);
  pinMode(switch01, INPUT);
  pinMode(switch02, INPUT);
  pinMode(switch03, INPUT);
  pinMode(DHTpin, INPUT);
  

  cerrar_botiquin();
  Serial.begin(9600);
  Bt1.begin(9600); 
  dht.begin();
}


/*
 * ******************** FIN DE DECLARACIONES ********************
 * ******************** COMIENZO DEL PROGRAMA *******************
 */

/**
   Determina el tiempo que se tomara entre un cheque de estados
   y otro.
*/
bool intervalo_cumplido() {
  return ((tiempo - tiempo_anterior) > INTERVALO_LOOP_PRINCIPAL);
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

/*
 * ******************** HUMIDITY CONTROL ZONE *******************
 */

/**
   Chequea que la humedad no exeda los rangos minimos y maximos. 
   En caso contrario, notifica al disposivo movil y suena una alarma.
*/
void chequear_humedad() {
  humedadLeida = dht.readHumidity();
  
  if (isnan(humedadLeida) ) {
    Serial.println("Falló al leer del sensor DHT");
  } else if ( humedadLeida > HUMEDAD_MAX ) {
      
      if( humedadNoPermitidaRecientemente ){
        Bt1.write(HUMEDAD_ELEVADA);
        humedadNoPermitidaRecientemente = false;
        alarmaDeHumedad = true;
      }

  }else{
    alarmaDeHumedad = false;
    humedadNoPermitidaRecientemente = true;
  }
 
}


/*
 * ******************** LIGHT CONTROL ZONE *******************
 */

/*
    Chequea que no haya luz en el interior del botiquín cuando éste esté
    cerrado. Si hay luz dentro, manda un mensaje, notificando al celular, y hace sonar la alarma.
*/
void chequear_luminosidad() {
  valorLuminosidadLeido = analogRead(fotoresistor);

   Serial.println(valorLuminosidadLeido);
   Serial.println("");
  
  if ( !puertaAbierta && valorLuminosidadLeido > LUMINOSIDAD_MAX ){
    if( luzNoPermitidaRecientemente){
      Bt1.write(LUZ_ELEVADA);
      luzNoPermitidaRecientemente = false;
      alarmaDeLuz = true;
    }
   
  } else{
    alarmaDeLuz = false;
    luzNoPermitidaRecientemente = true;
  }
}


/*
 * En la aplicación de android tendríamos que mandar por msj el nivel de luz que querémos, y acá directamente llamar a esta función
 * con el porcentaje requerido de luz.
 * Como no tiene resistencia, el 100% tendria que ser 128.
 * 255 es el 100% del voltaje proporcionado.
 * 128 es la mitad, en este caso 2,5v
 * 
 */
void prender_lampara(int porcentaje){
  int i = (porcentaje*128/100);
  analogWrite(lampara, i);
}


/*
 * ******************** DOOR CONTROL ZONE *******************
 */

void cerrar_botiquin(){
  /*Activar rele*/
  digitalWrite(electroiman, HIGH);
  /*Encender led rojo*/
  puertaAbierta = false;
  cambiar_estado_puerta();
  hacer_sonar_melodia();
}

void abrir_botiquin() {
  /*Desactivar rele*/
  digitalWrite(electroiman, LOW);
  /*Encender led verder*/
  puertaAbierta = true;
  cambiar_estado_puerta();  
  hacer_sonar_melodia();
}

void cambiar_estado_puerta(){
   if( !puertaAbierta ){
        digitalWrite( LED_Red, HIGH);   
        digitalWrite( LED_Green, LOW);
        digitalWrite(lampara, LOW);
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
    byte switchPulsado = digitalRead(switch01); //1 => pulsado | 0 => no pulsado
    if(!switchPulsado){ //se sacó el medicamento 
      if( switch1SinPulsarRecientemente ){ //primera vez que entra al loop de switch sin pulsar
        switch1SinPulsarRecientemente = false;
        /*
         * informo a la aplicación que se sacó algo
         */
          Bt1.write(SWITCH1_DESACTIVADO);
      }   
    }
    else{ // el switch3 esta pulsado
      switch1SinPulsarRecientemente = true;
    }
}



void actuar_switch02() {
    byte switchPulsado = digitalRead(switch02); //1 => pulsado | 0 => no pulsado
    if(!switchPulsado){ //se sacó el medicamento 
      if( switch2SinPulsarRecientemente ){ //primera vez que entra al loop de switch sin pulsar
        switch2SinPulsarRecientemente = false;
        /*
         * informo a la aplicación que se sacó algo
         */
          Bt1.write(SWITCH2_DESACTIVADO);
      }   
    }
    else{ // el switch3 esta pulsado
      switch2SinPulsarRecientemente = true;
    }
}

void actuar_switch03() {
    byte switchPulsado = digitalRead(switch03); //1 => pulsado | 0 => no pulsado
    if(!switchPulsado){ //se sacó el medicamento 
      if( switch3SinPulsarRecientemente ){ //primera vez que entra al loop de switch sin pulsar
        switch3SinPulsarRecientemente = false;
        /*
         * informo a la aplicación que se sacó algo
         */
          Bt1.write(SWITCH3_DESACTIVADO);
      }   
    }
    else{ // el switch3 esta pulsado
      switch3SinPulsarRecientemente = true;
    }
}


/*
 * ******************** BUZZER ZONE *******************
 */

void chequear_buzzer(){
  if(!alarmaApagada && (alarmaDeHumedad || alarmaDeLuz))
    hacer_sonar_melodia();
  else{ //se apago la alarma 
    if(alarmaApagadaRecientemente){
      tiempoAlarmaFueApagada = tiempo;
      alarmaApagadaRecientemente = false;
    }
    if(intervalo_particular_cumplido(tiempo, tiempoAlarmaFueApagada, INTERVALO_ALARMA_SIN_SONAR)){
      alarmaApagada = false;
      alarmaApagadaRecientemente = true;
    }
  }
}

/**
 * La idea de esta función ahora fue hacerla generica, despues solo tendría que ponerse
 * las llamadas a la función directamente.
  */
  
 void hacer_sonar_melodia() {
    noTone(buzzer);
    tone(buzzer, 1915);
}

/*
 * ******************** CONTROL ZONE *******************
 */

void loop(){
  
  tiempo = millis();

  if(intervalo_particular_cumplido(tiempo, tiempo_anterior_lecturaHumedad, INTERVALO_LECTURA_HUMEDAD)){
    chequear_humedad();
    tiempo_anterior_lecturaHumedad = tiempo;    
  }

  if ( intervalo_cumplido() ) {
    
    chequear_luminosidad();
    chequear_extraccion();
    chequear_buzzer();
    
    if(Bt1.available()){ /// te dice si hay algo en el buffer
      digitoLeidoBT = Bt1.read();
      
        /*
         * LOGICA DE INTERPRETACIÓN
         */
         
      if(digitoLeidoBT == PARAR_ALARMA){ 
         alarmaApagada = true;
         
      }else if( digitoLeidoBT== ABRIR_PUERTA){ 
         abrir_botiquin();
         
      }else if( digitoLeidoBT == CERRAR_PUERTA){ 
         cerrar_botiquin();
         
      }else if( digitoLeidoBT == LAMPARA_APAGADA){ 
         prender_lampara(0);
         
      }else if( digitoLeidoBT == LAMPARA_PRENDIDA_A_MEDIAS){ 
         prender_lampara(50);
         
      }else if( digitoLeidoBT == LAMPARA_PRENDIDA){ 
         prender_lampara(100);
      }
    }
    
    tiempo_anterior = tiempo; 
  }
}
