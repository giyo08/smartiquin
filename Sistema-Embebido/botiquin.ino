int MINUTO = 60000;
int TIEMPO_CHEQUEO = 2*MINUTO;
byte TEMPERATURA_MIN = 5; 
byte TEMPERATURA_MAX = 20; 
byte HUMEDAD_MIN = 20; 
byte HUMEDAD_MAX = 90;
byte LUMINOSIDAD_MIN = 10; 
byte LUMINOSIDAD_MAX = 70; 
byte ventilador = 9; 

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

  obtener_parametros_externos();
}

void esperar_intervalo(){
  delay(TIEMPO_CHEQUEO);
}

byte leer_temperatura(){
  return 30; 
}

/**
 * Chequea que la temperatura no exeda los rangos minimos y maximo
 * Caso contrario, actua en consecuencia llamando a los actuadores.
 */
void chequear_temperatura(){
  byte temperatura = leer_temperatura(); 
  if(temperatura > TEMPERATURA_MAX){
    encender_ventilador();  
  }

  if(temperatura < TEMPERATURA_MIN){
    encender_actuador_temperatura_minima();
  }
    
};

/**
 * Chequea que la humedad no exeda los rangos minimos y maximos 
 * caso contrario, actua en consecuencia llamando a los actuadores
 * correspondientes.
 */
void chequear_humedad(){};

/**
 * Chequea que la luminosidad no exeda los rangos minimos y maximos
 * caso contrario, actua en consecuencia llamando a los actuadores
 * correspondientes
 */
void chequear_luminosidad(){};


void encender_ventilador(){
    digitalWrite(ventilador, HIGH); 
}

void apagar_ventilador(){
  digitalWrite(ventilador, LOW);  
}

/**
 * Definir que hara esta funcion
 */
void encender_actuador_temperatura_minima(){
  
}

void loop() {
  // put your main code here, to run repeatedly:
  chequear_temperatura(); 
  chequear_humedad(); 
  chequear_luminosidad(); 
  
  esperar_intervalo();
}
