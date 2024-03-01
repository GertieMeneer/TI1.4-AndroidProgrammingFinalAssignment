package com.example.androidprogrammingfinalassignment;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javax.net.ssl.SSLSocketFactory;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private MqttClient client = null;
    private TextView redText;
    private TextView blueText;
    private TextView yellowText;
    private TextView greenText;
    private TextView conStatusText;
    private static List<com.example.androidprogrammingfinalassignment.Log> logs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logs = new ArrayList<>();

        redText = findViewById(R.id.redText);
        blueText = findViewById(R.id.blueText);
        yellowText = findViewById(R.id.yellowText);
        greenText = findViewById(R.id.greenText);
        conStatusText = findViewById(R.id.conStatusText);

        try
        {
            client = new MqttClient("ssl://8f379621c2fc4eba8c8f89232ae9aab3.s2.eu.hivemq.cloud", MqttClient.generateClientId(), new MemoryPersistence());       //url not in use anymore
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("esp32GertieMeneer");
        mqttConnectOptions.setPassword("Droppie@12!".toCharArray());
        mqttConnectOptions.setSocketFactory(SSLSocketFactory.getDefault());

        try
        {
            client.connect(mqttConnectOptions);
            conStatusText.setText(R.string.connected);
            conStatusText.setTextColor(Color.GREEN);
            Log.i("CONNECTION", "Connected to MQTT Broker");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable throwable)
            {
                conStatusText.setTextColor(Color.RED);
                conStatusText.setText(R.string.disconnected);
                Log.i("CONNECTION", "Connection lost with MQTT Broker");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage)
            {
                runOnUiThread(() ->
                {
                    Log.i("CONNECTION", "Message received");
                    Log.d("CONNECTION", "" + mqttMessage);

                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                    if (String.valueOf(mqttMessage).equals("red: 0")) {
                        redText.setText(R.string.red_led_off);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.red_led_off)));
                        Log.i("LOG", "Red LED off");
                    }
                    if (String.valueOf(mqttMessage).equals("red: 1")) {
                        redText.setText(R.string.red_led_on);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.red_led_on)));
                        Log.i("LOG", "Red LED on");
                    }

                    if (String.valueOf(mqttMessage).equals("blue: 0")) {
                        blueText.setText(R.string.blue_led_off);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.blue_led_off)));
                        Log.i("LOG", "Blue LED off");
                    }
                    if (String.valueOf(mqttMessage).equals("blue: 1")) {
                        blueText.setText(R.string.blue_led_on);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.blue_led_on)));
                        Log.i("LOG", "Blue LED on");
                    }

                    if (String.valueOf(mqttMessage).equals("yellow: 0")) {
                        yellowText.setText(R.string.yellow_led_off);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.yellow_led_off)));
                        Log.i("LOG", "Yellow LED off");
                    }
                    if (String.valueOf(mqttMessage).equals("yellow: 1")) {
                        yellowText.setText(R.string.yellow_led_on);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.yellow_led_on)));
                        Log.i("LOG", "Yellow LED on");
                    }

                    if (String.valueOf(mqttMessage).equals("green: 0")) {
                        greenText.setText(R.string.green_led_off);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.green_led_off)));
                        Log.i("LOG", "Green LED off");
                    }
                    if (String.valueOf(mqttMessage).equals("green: 1")) {
                        greenText.setText(R.string.green_led_on);
                        logs.add(new com.example.androidprogrammingfinalassignment.Log(currentTime + ": " + getString(R.string.green_led_on)));
                        Log.i("LOG", "Green LED on");
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
            {
                Log.i("CONNECTION", "Message send");
            }
        });

        try
        {
            client.subscribe("#", 1);
            Log.i("CONNECTION", "Subscribed to topic");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        send("refresh");
    }

    public void onRedButton(View v)
    {
        Log.i("BUTTONS", "User pressed red button");
        send("red");
    }

    public void onBlueButton(View v)
    {
        Log.i("BUTTONS", "User pressed blue button");
        send("blue");
    }

    public void onYellowButton(View v)
    {
        Log.i("BUTTONS", "User pressed yellow button");
        send("yellow");
    }

    public void onGreenButton(View v)
    {
        Log.i("BUTTONS", "User pressed green button");
        send("green");
    }

    public void send(String message)
    {
        try
        {
            client.publish("coms", message.getBytes(UTF_8), 2, false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onReconnectButton(View v)
    {
        Log.i("BUTTONS", "User pressed reconnect button");
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        Toast toast = Toast.makeText(this, getString(R.string.reconnecting), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onDisconnectButton(View v)
    {
        Log.i("BUTTONS", "User pressed disconnect button");
        try
        {
            client.disconnect();
            conStatusText.setText(R.string.disconnected);
            conStatusText.setTextColor(Color.RED);
            Log.i("CONNECTION", "Disconnected from MQTT Broker");
            Toast toast = Toast.makeText(this, getString(R.string.disconnecting), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onClearLogButton(View v)
    {
        Log.i("BUTTONS", "User pressed clear log button");
        logs.clear();
        Toast toast = Toast.makeText(this, getString(R.string.log_deleted), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onFullLogButton(View v)
    {
        Log.i("BUTTONS", "User pressed full log button");
        Intent intent = new Intent(MainActivity.this, LogActivity.class);
        startActivity(intent);
    }

    public static List<com.example.androidprogrammingfinalassignment.Log> getLogs()
    {
        return logs;
    }
}