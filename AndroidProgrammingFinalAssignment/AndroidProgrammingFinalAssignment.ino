#include <PubSubClient.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>
#include <WiFiClientSecure.h>

const char* ssid = "joost";
const char* password = "patrick123";
const char* mqttServer = "8f379621c2fc4eba8c8f89232ae9aab3.s2.eu.hivemq.cloud";
const int mqttPort = 8883;
const char* mqttUser = "esp32GertieMeneer";
const char* mqttPassword = "Droppie@12!";

WiFiClientSecure wifiClient;
PubSubClient mqttClient(wifiClient);

int redswitch = 33;
int blueswitch = 32;
int yellowswitch = 13;
int greenswitch = 14;

int redled = 22;
int blueled = 25;
int yellowled = 26;
int greenled = 27;

int redlaststate = 0;
int bluelaststate = 0;
int yellowlaststate = 0;
int greenlaststate = 0;

int redcurrentstate = 0;
int bluecurrentstate = 0;
int yellowcurrentstate = 0;
int greencurrentstate = 0;

int redledstate = LOW;
int blueledstate = LOW;
int yellowledstate = LOW;
int greenledstate = LOW;

int redmessage = 0;
int bluemessage = 0;
int yellowmessage = 0;
int greenmessage = 0;

void setup() {
  // put your setup code here, to run once:
  pinMode(2, OUTPUT);
  pinMode(redled, OUTPUT);
  pinMode(blueled, OUTPUT);
  pinMode(yellowled, OUTPUT);
  pinMode(greenled, OUTPUT);

  pinMode(redswitch, INPUT_PULLUP);
  pinMode(blueswitch, INPUT_PULLUP);
  pinMode(yellowswitch, INPUT_PULLUP);
  pinMode(greenswitch, INPUT_PULLUP);

  digitalWrite(2, HIGH);

  Serial.begin(115200);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
  }
  Serial.println("Connected");

  wifiClient.setInsecure();

  // Set up MQTT client
  mqttClient.setServer(mqttServer, mqttPort);
  mqttClient.setCallback(callback);
  mqttClient.setKeepAlive(60);

  while (!mqttClient.connected()) {
    if (mqttClient.connect("esp", mqttUser, mqttPassword)) {
      Serial.println("mqtt broker connected");
    } else {
      Serial.print("failed with state ");
      Serial.print(mqttClient.state());
      delay(2000);
    }
  }
  mqttClient.subscribe("coms");
  digitalWrite(2, LOW);
}

void loop() {
  // put your main code here, to run repeatedly:

  mqttClient.loop();
  checkButtons();
  updateLED();
  delay(1);
}

void checkButtons()
{
  if (redmessage == 1)
  {
    redcurrentstate = !redlaststate;
  } else
  {
    redcurrentstate = digitalRead(redswitch);
  }

  if (bluemessage == 1)
  {
    bluecurrentstate = !bluelaststate;
  } else
  {
    bluecurrentstate = digitalRead(blueswitch);
  }

  if (yellowmessage == 1)
  {
    yellowcurrentstate = !yellowlaststate;
  } else
  {
    yellowcurrentstate = digitalRead(yellowswitch);
  }

  if (greenmessage == 1)
  {
    greencurrentstate = !greenlaststate;
  } else
  {
    greencurrentstate = digitalRead(greenswitch);
  }
}

void updateLED() {
  if (redcurrentstate != redlaststate) {
    if (redcurrentstate == LOW || redmessage == 1) {  // Knop is losgelaten
      redledstate = !redledstate;  // Status van de LED omkeren
      sendLED(1);
    }
    delay(50);
  }
  digitalWrite(redled, redledstate);
  redlaststate = redcurrentstate;
  redmessage = 0;

  if (bluecurrentstate != bluelaststate) {
    if (bluecurrentstate == LOW || bluemessage == 1) {  // Knop is losgelaten
      blueledstate = !blueledstate;// Status van de LED omkeren
      sendLED(2);
    }
    delay(50);
  }
  digitalWrite(blueled, blueledstate);
  bluelaststate = bluecurrentstate;
  bluemessage = 0;

  if (yellowcurrentstate != yellowlaststate) {
    if (yellowcurrentstate == LOW || yellowmessage == 1) { // Knop is losgelaten
      yellowledstate = !yellowledstate; // Status van de LED omkeren
      sendLED(3);
    }
    delay(50);
  }
  digitalWrite(yellowled, yellowledstate);
  yellowlaststate = yellowcurrentstate;
  yellowmessage = 0;

  if (greencurrentstate != greenlaststate) {
    if (greencurrentstate == LOW || greenmessage == 1) { // Knop is losgelaten
      greenledstate = !greenledstate; // Status van de LED omkeren
      sendLED(4);
    }
    delay(50);
  }
  digitalWrite(greenled, greenledstate);
  greenlaststate = greencurrentstate;
  greenmessage = 0;
}

void callback(char* topic, byte* payload, unsigned int length) {
  // Handle incoming message
  String message = "";

  for (int i = 0; i < length; i++) {
    message += (char)payload[i];
  }

//  Serial.println("Message received on topic " + String(topic) + ": " + message);
  readMsg(message);
}

void sendLED(int led)
{
  String msg;
  switch (led)
  {
    case 1:
      msg = "red: " + String(redledstate);
      mqttClient.publish("coms", msg.c_str());
      break;

    case 2:
      msg = "blue: " + String(blueledstate);
      mqttClient.publish("coms", msg.c_str());
      break;

    case 3:
      msg = "yellow: " + String(yellowledstate);
      mqttClient.publish("coms", msg.c_str());
      break;

    case 4:
      msg = "green: " + String(greenledstate);
      mqttClient.publish("coms", msg.c_str());
      break;

    default:
//    Serial.println(msg);
      break;
  }
}

void readMsg(String message)
{
  if (message.equals("refresh"))
  {
//    Serial.println("refreshing data");
    sendLED(1);
    sendLED(2);
    sendLED(3);
    sendLED(4);
  }
  if (message.equals("red"))
  {
//    Serial.println("red button pressed");
    redmessage = 1;
  }
  else if (message.equals("blue"))
  {
//    Serial.println("blue button pressed");
    bluemessage = 1;
  }
  else if (message.equals("yellow"))
  {
//    Serial.println("yellow button pressed");
    yellowmessage = 1;
  }
  else if (message.equals("green"))
  {
//    Serial.println("green button pressed");
    greenmessage = 1;
  }
}
