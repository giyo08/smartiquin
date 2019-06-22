#include <SoftwareSerial.h>
#include <DHT.h>

SoftwareSerial Bt1(10, 11); // RX | TX


/*
 * Sensores y actuadores asociados
 */
 
byte switch01 = 3; 
byte switch02 = 5;
byte switch03 = 6;
byte buzzer = 4;
byte LED_Green = 7;
byte LED_Red = 8;
byte lampara = 9;
byte electroiman = 12;

byte fotoresistor = A4;

/*
 * Sensor Temperatura y Humedad
 */
 
#define DHTTYPE DHT11 // Declaramos el modelo de sensor a utilizar
byte DHTPin = 2;

DHT dht(DHTPin, DHTTYPE); // Inicializamos la variable de comunicación entre el sensor y Arduino


/*
 *  Variables
 */

char digitoLeidoBT = ' ';
float valorLuminosidadLeido;
float humedadLeida;

boolean puertaAbierta = true;

// Relacionadas con los switchs

boolean switch1SinPulsarRecientemente = true; // Variable de control de corte
boolean switch2SinPulsarRecientemente = true; // Variable de control de corte
boolean switch3SinPulsarRecientemente = true; // Variable de control de corte

// Relacionadas con la lampara

int porcentajeDeLuzAnterior = 0;

// Relacionadas con el sensor de luz

const byte LUMINOSIDAD_MAX = 300; // luminosidad máxima admitida dentro del botiquín
boolean luzNoPermitidaRecientemente = true; // Variable de control de corte

// Relacionadas con el sensor de temperatura y humedad

byte HUMEDAD_MAX = 90; // humedad máxima permitida dentro del botiquín
boolean humedadNoPermitidaRecientemente = true; // Variable de control de corte

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

boolean componenteApagado(byte componente){
  return digitalRead(componente) == 0;
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
  float t = dht.readTemperature();

  if (isnan(humedadLeida) || isnan(t)) {
    Serial.print("Falló al leer del sensor");
    return;
  }

  if ( humedadLeida > HUMEDAD_MAX ) {
    //Serial.print("Humedad: ");
    //Serial.print(h);
    //Serial.print("%\t");
    if( humedadNoPermitidaRecientemente ){
      Bt1.write('H');
      humedadNoPermitidaRecientemente = false;
      alarmaDeHumedad = true;
    }    
  } else{
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
//  Serial.print("\nLuminosidad: ");
//  Serial.print(analogRead(fotoresistor));
  
  valorLuminosidadLeido = analogRead(fotoresistor);
  
  if ( !puertaAbierta && valorLuminosidadLeido > LUMINOSIDAD_MAX ){
    if( luzNoPermitidaRecientemente){
      Bt1.write('L');
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
  int i;
  
  if( porcentajeDeLuzAnterior < porcentaje)
    for(i = (porcentajeDeLuzAnterior*128/100)+1; i<= (porcentaje*128/100); i++){
      analogWrite(lampara, i);
      delay(5);
    }
    
  if( porcentajeDeLuzAnterior > porcentaje)
    for(i = (porcentajeDeLuzAnterior*128/100)-1; i >= (porcentaje*128/100); i--){
      analogWrite(lampara, i);
      delay(5);
    }
  porcentajeDeLuzAnterior = porcentaje;
}

/*
 * 255 es el 100% del voltaje proporcionado.
 * 128 es la mitad, en este caso 2,5v
 * 
 */ 
//void prender_lampara(){
//  int i;
//  for(i = 0; i<= 128; i++){
//    analogWrite(lampara, i);
//    delay(25);
//  }
//  for(i = 128; i>=0; i--){
//    analogWrite(lampara, i);
//    delay(25);
//  }
//}

/*
 * ******************** DOOR CONTROL ZONE *******************
 */

void cerrar_botiquin(){
  /*Activar rele*/
  if( componenteApagado )
    digitalWrite(electroiman, HIGH);
  /*Encender led rojo*/
  puertaAbierta = false;
  cambiar_estado_puerta();
  hacer_sonar_melodia("ccggaagc");
}

void abrir_botiquin() {
  /*Desactivar rele*/
  if( !componenteApagado )
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
          Bt1.write('1');
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
          Bt1.write('2');
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
          Bt1.write('3');
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

void setup() {
  
  pinMode(buzzer, OUTPUT);
  pinMode(electroiman, OUTPUT);
  pinMode(lampara, OUTPUT);
  pinMode(LED_Green, OUTPUT);
  pinMode(LED_Red, OUTPUT);
  pinMode(switch01, INPUT);
  pinMode(switch02, INPUT);
  pinMode(switch03, INPUT);
  
  digitalWrite(LED_Green, HIGH);
  
  delay (500) ;              // Espera antes de encender el modulo
  
  Serial.begin(9600);
  dht.begin();
  Bt1.begin(9600); 

 
}

void loop(){
  
  tiempo = millis();
  
  if ( intervalo_cumplido() ) {
    //chequear_humedad();
    chequear_luminosidad();
    chequear_extraccion();
    chequear_buzzer();
    
    if(Bt1.available()){ /// te dice si hay algo en el buffer
      digitoLeidoBT = Bt1.read();
      
        /*
         * LOGICA DE INTERPRETACIÓN
         */
         
      if(digitoLeidoBT == 'L'){ // Parar de sonar la alarma
         alarmaApagada = true;
      }else if( digitoLeidoBT== 'A'){ // Abrir la puerta
         abrir_botiquin();
         
      }else if( digitoLeidoBT == 'C'){ //Cerrar la puerta
         cerrar_botiquin();
         
      }else if( digitoLeidoBT >= 'P' && digitoLeidoBT<= 'Z'){ //Cantidad de Luz en el celular
        /*
         * Prender la lampaprender_lamparara, en el interior del botiquín, según el valor recibido de luz del celular
         * P => 80-0% | Z => 90-100%
         * 
         * c - 80 = n => n*100 => porcentaje que busco! :D
         * 
         */
         if(puertaAbierta){
           int i = ((int) digitoLeidoBT - 80)*10; 
           prender_lampara(i);
         }
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
