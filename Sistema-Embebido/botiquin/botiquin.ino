#include <SoftwareSerial.h>
#include <DHT.h>
#define DETENER_ALARMA 'L'
#define DESBLOQUEAR_PUERTA 'A'
#define BLOQUEAR_PUERTA 'C'
#define LUZ_INTERIOR_CERO_PORCIENTO 'P'
#define LUZ_INTERIOR_CINCUENTA_PORCIENTO 'T'
#define LUZ_INTERIOR_CIEN_PORCIENTO 'Z'
#define SWITCH_UNO_ACTIVADO 1
#define SWITCH_DOS_ACTIVADO 2
#define SWITCH_TRES_ACTIVADO 3
#define VALOR_HUMEDAD_NO_PERMITIDO 'H'
#define VALOR_LUMINOSIDAD_NO_PERMITIDA 'L'

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

// Relacionadas con la lampara

int porcentajeDeLuzAnterior = 0;

// Relacionadas con el sensor de luz

const int LUMINOSIDAD_MAX = 800; // luminosidad máxima admitida dentro del botiquín
boolean luzNoPermitidaRecientemente = true; // Variable de control de corte

// Relacionadas con el sensor de temperatura y humedad

byte HUMEDAD_MAX = 80; // humedad máxima permitida dentro del botiquín
boolean humedadNoPermitidaRecientemente = true; // Variable de control de corte
float humedadLeida;

unsigned long tiempo_anterior_lecturaHumedad = 0;
unsigned long tiempo_anterior_chequeo_luz = 0;
const unsigned long intervaloLecturaHumedad = 5000;

// Relacionadas al tiempo y las esperas

unsigned long tiempo = 0;
unsigned long tiempo_anterior = 0;
const unsigned long intervalo = 200;

// Relacionadas con la alarma

boolean alarmaApagada = false;
boolean alarmaDeHumedad = false;
boolean alarmaDeLuz = false;
boolean alarmaApagadaRecientemente = true; // Variable de control de corte
unsigned long tiempoAlarmaFueApagada;
unsigned long intervaloTiempoSinSonarAlarma = 10000; // en milisegundos


SoftwareSerial Bt1(RX, TX);


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
        Bt1.write(VALOR_HUMEDAD_NO_PERMITIDO);
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
      Bt1.write(VALOR_LUMINOSIDAD_NO_PERMITIDA);
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
 */
void prender_lampara(int porcentaje){
  porcentajeDeLuzAnterior = porcentaje*128/100;
}

void graduar_luz(){
  int porcentaje_leido = analogRead(lampara);
  int porcentaje_escalado = porcentaje_leido*100/128; 
  if(porcentaje_escalado == porcentajeDeLuzAnterior)
    exit; 
  if( porcentaje_escalado < porcentajeDeLuzAnterior){
      int porcentaje = porcentaje_escalado + 1; 
      analogWrite(lampara, porcentaje);
  }else{
      int porcentaje = porcentaje_escalado - 1; 
      analogWrite(lampara, porcentaje);
  }
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
  hacer_sonar_melodia("ccggaagc");
}

void abrir_botiquin() {
  /*Desactivar rele*/
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
          Bt1.write(SWITCH_UNO_ACTIVADO);
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
          Bt1.write(SWITCH_DOS_ACTIVADO);
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
          Bt1.write(SWITCH_TRES_ACTIVADO);
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
    hacer_sonar_melodia("aaaffggd");
  else{ //se apago la alarma 
    if(alarmaApagadaRecientemente){
      tiempoAlarmaFueApagada = tiempo;
      alarmaApagadaRecientemente = false;
    }
    if(intervalo_particular_cumplido(tiempo, tiempoAlarmaFueApagada, intervaloTiempoSinSonarAlarma)){
      alarmaApagada = false;
      alarmaApagadaRecientemente = true;
    }
  }
}


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

void playNote(char note, int duration) {
  char names[] = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'C' };
  int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956 };
  int i;
  // play the tone corresponding to the note name
  for (i = 0; i < 8; i++) {
    if (names[i] == note) {
      noTone(buzzer);
      tone(buzzer, tones[i], duration);
    }
  }
}


/*
 * ******************** CONTROL ZONE *******************
 */


void loop(){
  
  tiempo = millis();

  if(intervalo_particular_cumplido(tiempo, tiempo_anterior_lecturaHumedad, intervaloLecturaHumedad)){
    chequear_humedad();
    tiempo_anterior_lecturaHumedad = tiempo;    
  }

  if(intervalo_particular_cumplido(tiempo, tiempo_anterior_chequeo_luz, 5)){
    graduar_luz();
    tiempo_anterior_chequeo_luz = tiempo;    
  }

  if ( intervalo_cumplido() ) {
    
    chequear_luminosidad();
    chequear_extraccion();
    chequear_buzzer();
    
    if(Bt1.available()){ /// te dice si hay algo en el buffer
      digitoLeidoBT = Bt1.read();
      
        /*
         * Protocolo de mensajes
         */
         
      if(digitoLeidoBT == DETENER_ALARMA){
         alarmaApagada = true;
      }else if( digitoLeidoBT== DESBLOQUEAR_PUERTA){
         abrir_botiquin();
      }else if( digitoLeidoBT == BLOQUEAR_PUERTA){
         cerrar_botiquin();
      }else if( digitoLeidoBT == LUZ_INTERIOR_CERO_PORCIENTO){
         prender_lampara(0);
      }else if( digitoLeidoBT == LUZ_INTERIOR_CINCUENTA_PORCIENTO){
         prender_lampara(50);
      }else if( digitoLeidoBT == LUZ_INTERIOR_CIEN_PORCIENTO){
         prender_lampara(100);
      }
        
      Serial.write(digitoLeidoBT);
    }

    
    if (Serial.available()){
      digitoLeidoBT = Serial.read();
      Bt1.write(digitoLeidoBT);
    }

    tiempo_anterior = tiempo; 
  }
}
