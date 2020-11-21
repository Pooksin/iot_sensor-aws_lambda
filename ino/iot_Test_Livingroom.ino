#include <AWS_IOT.h>;
#include <WiFi.h>;
#include <ArduinoJson.h>;
#include "DHT.h";

#define DHTPIN 23     // what digital pin we're connected to
#define DOORPIN 16
#define EMERGEBUTTON 4
#define DHTTYPE DHT11   // DHT 11

DHT dht(DHTPIN, DHTTYPE);
AWS_IOT hornbill;   // AWS_IOT instance

const char* WIFI_SSID = "KAU-Guest";
const char* WIFI_PASSWORD = "";
char HOST_ADDRESS[] = "afkhvrjlvs6fo-ats.iot.ap-northeast-2.amazonaws.com"; // 사물 엔드 포인트
char CLIENT_ID[] = "devicetest"; // 사물 이름
char TOPIC_NAME[] = "mytopic/DHT"; // 토픽명
char SHADOW_TOPIC_NAME[] = "$aws/things/devicetest/shadow/update"; // 사물 섀도우 토픽명
int status = WL_IDLE_STATUS;
int tick = 0, msgCount = 0, msgReceived = 0;
char payload[512];
char rcvdPayload[512];
int doorState = 0;
int buttState = 0;
int emergeState = 0;

void setup() {
  WiFi.disconnect(true);
  delay(500);
  Serial.begin(115200);
  while (WiFi.status() != WL_CONNECTED){
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(WIFI_SSID);
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:

    // wait 5 seconds for connection:
    delay(5000);
  }
  Serial.println("\nConnected to the WiFi network");
  Serial.print("IP address : ");
  Serial.println(WiFi.localIP());

  Serial.println("Connected to wifi");

  if (hornbill.connect(HOST_ADDRESS, CLIENT_ID) == 0){
    Serial.println("Connected to AWS");
    delay(1000);
  }
  else{
    Serial.println("AWS connection failed, Check the HOST Address");
    while (1);
  }

  delay(2000);
  pinMode(EMERGEBUTTON, INPUT);
  pinMode(DOORPIN, INPUT_PULLUP);
  dht.begin(); //Initialize the DHT11 sensor
}


void loop() {
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  float f = dht.readTemperature(true);

  buttState = digitalRead(EMERGEBUTTON);
  doorState = digitalRead(DOORPIN);

 if (buttState == HIGH) {
    emergeState = 1;
  }
  else {
    emergeState = 0;
  }
  
  if (isnan(h) || isnan(t) || isnan(f)) {
    Serial.println("Failed to read from DHT sensor!");
  }
  else{
    StaticJsonDocument<300> JSONDocument;
    JSONDocument["humid"] = h;
    JSONDocument["temperature"] = t;
    JSONDocument["roomnumber"] = 1;
    JSONDocument["door"] = doorState;
    JSONDocument["emergency"] = emergeState;
    char payload[100];
    serializeJson(JSONDocument, payload);
    Serial.println("Sending message to AWS..");
    Serial.println(payload);

    if (hornbill.publish(TOPIC_NAME, payload) == 0) {
      Serial.print("Publish Message:");
      Serial.println(payload);
    }
    else{
      Serial.println("Publish failed");
    }
    /*
      if(hornbill.publish(SHADOW_TOPIC_NAME,payload) == 0)   // Publish the message(Temp and humidity)
      {
        Serial.print("Shadow Register");
      }
      else
      {
        Serial.println("Shadow failed");
      }
    */
    // publish the temp and humidity every 1Minutes.
    vTaskDelay(10000 / portTICK_RATE_MS);
  }
}
