int MINUTO = 60000;
int TIEMPO_CHEQUEO = 2*MINUTO;
byte HUMEDAD_MIN = 20; 
byte HUMEDAD_MAX = 90;
byte LUMINOSIDAD_MIN = 10; 
byte LUMINOSIDAD_MAX = 70; 
byte servo01 = 8;  
int abierto = 1; 

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





void actuar_switch_abierto(swith){
  estado = digitalRead(switch)
  if(estado == abierto){
    //contar que se saco una pastilla del switch correspondiente
    
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
    abrir_pestillo = 0;   
  }
}


void loop() {
  // put your main code here, to run repeatedly: 
  chequear_humedad(); 
  chequear_luminosidad();
  chequear_extraccion(); 
  chequear_apertura();
  esperar_intervalo();
}
