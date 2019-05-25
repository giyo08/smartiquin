#include <DHT.h>

// pines digitales
byte swith01 = 1; //en principio son pulsadores
byte swith02 = 2;
byte swith03 = 3;
byte LED_Red01 = 4;
byte LED_Blue01 = 5;
byte servo01 = 5; //en principio se trata de un led azul

// Sensor Temperatura y Humedad
#define DHTTYPE DHT11 // Declaramos el modelo de sensor a utilizar
byte DHTPin = 2;
byte HUMEDAD_MIN = 20; 
byte HUMEDAD_MAX = 90;

DHT dht(DHTPin, DHTTYPE); // Inicializamos la variable de comunicación entre el sensor y Arduino

byte pulsador01 = 2; 
 
// Sensor Fotoresistor
byte LDRPin = A4; 
byte LUMINOSIDAD_MIN = 10; 
byte LUMINOSIDAD_MAX = 70; 

//parametros
int MINUTO = 60000;
int TIEMPO_CHEQUEO = 2*MINUTO;


  
int abierto = 1; 
int abrir_pestillo = 0; 

//relacionadas al tiempo y las esperas
unsigned long tiempo = 0; 
unsigned long tiempo_anterior = 0; 
unsigned long intervalo = 2000; 

bool intervalo_cumplido(){
  return ((tiempo - tiempo_anterior) > intervalo);  
}

bool intervalo_cumplido_p(unsigned long inicial, unsigned long previo, unsigned long espera){
  return ((inicial - previo) > espera);
}

/**
 * Esta funcion obtiene los valores minimos y maximos de cada
 * parametro a comprobar, se comunica con un microservicio
 * que le sirve los valores actualizados
 */
void obtener_parametros_externos(){
  //conectarse y obtener valores
}

void setup() {
  // put your setup code here, to run once:
  pinMode(servo01, OUTPUT); 
  pinMode(pulsador01, INPUT);
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
      encender_buzzer();
   }
 }

 void encender_buzzer() {
  Serial.println("Buzzer encendido!!");
 }

/**
 * Chequea que la luminosidad no exeda los rangos minimos y maximos
 * caso contrario, actua en consecuencia llamando a los actuadores
 * correspondientes
 */
void chequear_luminosidad(){};

void encender_apagar_led(byte led, int veces){
  unsigned long t_actual = 0; 
  unsigned long t_previo = 0; 
  unsigned long interval = 50; 
  for(int x=0; x<veces; x++){
    if( intervalo_cumplido_p(t_actual, t_previo, interval) ){
      digitalWrite(led, !digitalRead(led)); 
      t_previo = t_actual; 
    }  
  }
    
}



void actuar_switch_abierto(byte pSwitch){
  byte estado = digitalRead(pSwitch);
  if(estado == abierto){
    encender_apagar_led(LED_Red01, 5); 
  }
}

/**
Este metodo se usa para identificar que alguno de los switch
han sido desactivados, es decir, se tomo la caja de medicamentos
de uno de los slots
*/
void chequear_extraccion(){
  actuar_switch_abierto(swith01);
  actuar_switch_abierto(swith02);
  actuar_switch_abierto(swith03);
}

/**
 * Realiza la apertura de una puerta, es decir llama a actuar al servomotor
 * para que mueva un pestillo
 */
void chequear_apertura(){
  if(abrir_pestillo == 1){
    digitalWrite(servo01, HIGH); 
    //abrir_pestillo = 0;   
  }else{
    digitalWrite(LED_Blue01, LOW);
  }
}

void chequear_pulsador(){
  int v_ldr = digitalRead(pulsador01); 
  if( v_ldr == HIGH ){
    //abrir_pestillo = 1;  
    digitalWrite(servo01, HIGH); 

  }else{
    //abrir_pestillo = 0;   
    digitalWrite(servo01, LOW); 

  }
  Serial.print("\nvalor pulsador: "); 
  Serial.print(v_ldr); 
  
}


void loop() {
  // put your main code here, to run repeatedly: 
  tiempo = millis();
  if( intervalo_cumplido() ){
    chequear_humedad(); 
    chequear_luminosidad();
    chequear_extraccion(); 
    chequear_pulsador(); 
    chequear_apertura();
    tiempo_anterior = tiempo; 
  }
}
