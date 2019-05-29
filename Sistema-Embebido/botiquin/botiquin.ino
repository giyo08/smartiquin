#include <SoftwareSerial.h>
#include <DHT.h>

SoftwareSerial BTserial(10,11); // RX | TX

byte switch01 = 3; //en principio son pulsadores
byte switch02 = 4;
byte switch03 = 5;
byte buzzer01 = 7;
byte LED_Blue01 = 8;
byte LED_Red01 = 9;

byte ldr01 = A4;

// Sensor Temperatura y Humedad
#define DHTTYPE DHT11 // Declaramos el modelo de sensor a utilizar
byte DHTPin = 2;
byte HUMEDAD_MIN = 20; 
byte HUMEDAD_MAX = 90;

int abierto = 0;
int cerrado = 1;
int pulsado = 1;
int encendido = 1;
int apagado = 0;
int abrir_pestillo = 0;

DHT dht(DHTPin, DHTTYPE); // Inicializamos la variable de comunicación entre el sensor y Arduino

byte pulsador01 = 2; 
 
// Sensor Fotoresistor
byte LDRPin = A4; 
byte LUMINOSIDAD_MIN = 10; 
byte LUMINOSIDAD_MAX = 70; 


//relacionadas al tiempo y las esperas
unsigned long tiempo = 0;
unsigned long tiempo_anterior = 0;
unsigned long intervalo = 200;

  

/*>>>>>>> 6ed58aed75d5017d0a6ad8935436d0cfe68a3ce9:Sistema-Embebido/botiquin/botiquin.ino*/

//Relacionadas al bluetooth 
char comando_bt;

enum Comandos { ABRIR, ENCONTRAR };

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

void setup() {
  // put your setup code here, to run once:

  pinMode(buzzer01, OUTPUT);  
  //digitalWrite(pulsador01, HIGH);
  Serial.begin(9600);
  dht.begin();

  obtener_parametros_externos();
}


/**
 * Chequea que la humedad no exeda los rangos minimos y maximos 
 * caso contrario, actua en consecuencia llamando a los actuadores
 * correspondientes.
 */
void chequear_humedad(){
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
      encender_buzzer(2000);
   }
 }


/**
   Chequea que la luminosidad no exeda los rangos minimos y maximos
   caso contrario, actua en consecuencia llamando a los actuadores
   correspondientes
*/
void chequear_luminosidad() {
  Serial.print("\nLuminosidad: ");
  Serial.print(analogRead(ldr01));
};

void encender_apagar_led(byte led, int veces) {
  unsigned long t_actual;
  unsigned long t_previo = 0;
  unsigned long interval = 750;
  int cumplidas = 0;
  int estado_proximo;
  while (cumplidas < veces + 1) {
    t_actual = millis();
    if ( intervalo_particular_cumplido(t_actual, t_previo, interval) ) {
      Serial.print("\n*************** Intervalo cumplido, PRENDER*************");
      estado_proximo = !digitalRead(led);
      digitalWrite(led, estado_proximo);
      t_previo = t_actual;
      if (estado_proximo == encendido) {
        cumplidas++;
      }
    }
  }
  digitalWrite(led, LOW);

}

/**
   Funcion que enciende el buzzer para que emita sonido por una determinada cantidad
   de tiempo
   Parametros:
   - tiempo: es la cantidad de tiempo en milisegundos que debe permanecer encendido el buzzer
*/
void encender_buzzer(int tiempo) {
  tone(buzzer01, 2000, tiempo); 
  Serial.println("Buzzer encendido por x tiempo"); //@todo: pasar por parametro el tiempo al print
}



int ledApagado(byte led) {
  return digitalRead(led) == 0;
}


void actuar_switch01() {
  byte switchPulsado = digitalRead(switch01);
  if (switchPulsado && ledApagado(LED_Blue01)) {
    encender_apagar_led(LED_Blue01, 1);
    
  }
}


void actuar_switch02() {
  byte switchPulsado = digitalRead(switch02);
  if (switchPulsado) {
    encender_apagar_led(LED_Red01, 2);
    //tambien prendo el buzzer, me gusta hacer ruido
    tone(buzzer01, 3000, 500);
  }
}

void actuar_switch03() {
  byte switchPulsado = digitalRead(switch02);
  if (switchPulsado) {
    encender_apagar_led(LED_Red01, 3);
    //tambien prendo el buzzer, me gusta hacer ruido
    tone(buzzer01, 3500, 500);
  }
}

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

/**
   Realiza la apertura de una puerta, es decir llama a actuar al servomotor
   para que mueva un pestillo
*/
/*
void chequear_apertura() {
  if (abrir_pestillo == 1) {
    digitalWrite(servo01, HIGH);
    //abrir_pestillo = 0;
  } else {
    digitalWrite(LED_Blue01, LOW);
  }
}
*/

/*
void chequear_pulsador() {
  //int v_ldr = digitalRead(pulsador01);
  delay(50);
  int v_pulsador = digitalRead(pulsador01);
  if ( v_pulsador == HIGH and digitalRead(servo01) == LOW ) {
    //abrir_pestillo = 1;
    digitalWrite(servo01, HIGH);

  } else {
    //abrir_pestillo = 0;
    digitalWrite(servo01, LOW);
  }
  Serial.print("\nvalor pulsador: ");
  Serial.print(v_pulsador);

}
*/

void loop() {
  // put your main code here, to run repeatedly:
  tiempo = millis();
  if ( intervalo_cumplido() ) {
    //chequear_humedad();
    chequear_luminosidad();
    chequear_extraccion();
    //chequear_pulsador();
    //chequear_apertura();
    //chequear_encender_buzzer();
    tiempo_anterior = tiempo;
    
    //if(BTserial.available())
    if(Serial.available())
    { 
      //comando_bt = BTserial.read();
      comando_bt = Serial.read();
      analizar_comando(comando_bt);
    }
  }
}

void analizar_comando(char comando){
  switch(comando){
    case 'a':
      abrir_botiquin(); 
      break; 
    case 'e': 
      encender_buzzer(4000);     
      break; 
  }  
}

void abrir_botiquin(){
  /*Llamar a funciones para abrir el electroiman*/
  /*Encender un led o el led indicativo de botiquin abierto*/
  encender_apagar_led(LED_Blue01, 5);  
}

void chequear_encender_buzzer(){
  byte switchPulsado = digitalRead(switch01);
  if (switchPulsado) {
    tone(buzzer01, 550, 2000); 
  }
}
