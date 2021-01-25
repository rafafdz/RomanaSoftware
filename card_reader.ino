/*  sources: 
 *  https://forum.arduino.cc/index.php?topic=444745.0
 *  https://naylampmechatronics.com/blog/22_Tutorial-Lector-RFID-RC522.html
 *  https://github.com/miguelbalboa/rfid/issues/279
 *  https://stackoverflow.com/questions/10228562/try-catch-block-in-arduino
 *  https://github.com/miguelbalboa/rfid/blob/master/examples/AccessControl/AccessControl.ino
 *  @author santiago
*/
#include <require_cpp11.h>
#include <MFRC522.h>
#include <deprecated.h>
#include <MFRC522Extended.h>
#include <SPI.h>

// # include <bits/stdc++.h>
// using namespace std;
# define for_range(i,a,b) for(int i = a; i < b; ++i)
// # define rep(i,a,b) for(int i = a; i <= b; ++i)
// # define invrep(i,b,a) for(int i = b; i >= a; --i)
# define pass (void)0
// typedef pair<int, int> par;
// typedef tuple<int, int, int> triple;
// typedef vector<int> vi;
// typedef vector<par> vp;
// typedef vector<vi> matrix;


#define RST_PIN 9    
#define SS_PIN  10   
MFRC522 rfid(SS_PIN, RST_PIN); // 
MFRC522::MIFARE_Key key;

// String id_ = "";  // .c_string()
// byte id_tarjeta[4]; 

const int CARD_TIMEOUT = 20000; // 30 segundos de espera

void setup() {
  Serial.begin(9600);
  SPI.begin();
  rfid.PCD_Init();
  //Serial.println("Holiwi");
  pinMode(7, OUTPUT);
}

void loop() {
  while (Serial.available()) {
    
    // Serial.print("B");
    // Serial.print("\n");

    digitalWrite(7, HIGH);
    int i = Serial.read();
    if (i == -1) {
        break;
    }
    char c = (char) i;
    // instrucciones
    if (c == 'L') {   // Listen
      Serial.print('S');
      delay(300);
      bool listened = false;
      long initial_time = millis();
      while (not listened && millis() - initial_time < CARD_TIMEOUT) {
        listened = listen_card();
        delay(20);
      }
    }
    else if (c == 'C') {  // Check Status
      check_status();
    }
    Serial.flush();
    break;
  }
  delay(50); // 20 ps -> 10 ps
}

bool listen_card() {
   if ( rfid.PICC_IsNewCardPresent()) {
      if (rfid.PICC_ReadCardSerial()) {
        /*
        for (byte i = 0; i < rfid.uid.size; i++) {
          Serial.print(rfid.uid.uidByte[i] < 0x10 ? " 0" : " ");
          Serial.print(rfid.uid.uidByte[i], HEX);
          id_tarjeta[i]= rfid.uid.uidByte[i];
        }
        */
        dump_byte_array(rfid.uid.uidByte, rfid.uid.size);
        rfid.PICC_HaltA();
        rfid.PCD_StopCrypto1();
        return(true);
      }
    }
    return(false);
}
 
// comparamos dos vectores (sabemos que son de largo cuatro)
boolean compare_array(byte array1[], byte array2[]) { 
  for_range(i, 0, 4) {
  if (array1[i] != array2[i]) return(false);
  }
  return(true);
}


// dump_byte_array(rfid.uid.uidByte, rfid.uid.size); imprime en formato hexadecimal
void dump_byte_array(byte *buffer, byte buffer_size) {
  //String response = "";
  for (byte i = 0; i < buffer_size; i++) {
    
    Serial.print(buffer[i] < 0x10 ? "0" : "");
    //vals[i] = buffer
    Serial.print(buffer[i], HEX);
  }
  //Serial.println(response);
}


// status of MFRC522
void check_status() {
  // Get the MFRC522 software version
  byte v = rfid.PCD_ReadRegister(rfid.VersionReg);
  if ((v == 0x00) || (v == 0xFF)) {
    Serial.print('B'); // Bad - something went wrong
    // return(false);
  }
  else {
    Serial.print('G');  // Good - everything is OK.
    // return(true);
  }
}
