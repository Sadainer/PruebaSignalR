package com.example.admin_sena.pruebasignalr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class ServiceSignalR extends Service {


    public HubConnection connection;
    HubProxy proxy;

    public ServiceSignalR() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startConnection();
    }

    public void startConnection() {

        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        String host = "http://190.109.185.138:8013/";
        connection = new HubConnection(host);
        proxy = connection.createHubProxy("hubAlarma");

        // subscribe to received - equal to `connection.received(function (data)` from javascript
        connection.received(new MessageReceivedHandler() {

            @Override
            public void onMessageReceived(JsonElement json) {



                JsonObject json_data = json.getAsJsonObject();
               JsonElement mensaje = json_data.get("A");
//
                if (mensaje != null) {

                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(2000);

                    Log.e("onMessageReceived", mensaje.toString());
                }
            }
        });

        connection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error al conectar, verifique la conexión");
                /*connection.disconnect();*/
            }
        });

        connection.closed(new Runnable() {
            @Override
            public void run() {
                System.out.println("Conexion Cerrada");
                /*connection.start();*/
            }
        });

        connection.reconnected(new Runnable() {
            @Override
            public void run() {
                System.out.println("Conexion Reconected");
            }
        });

        connection.reconnecting(new Runnable() {
            @Override
            public void run() {
                System.out.println("Conexion Reconecting");
                /*connection.disconnect();*/
            }
        });

        connection.connectionSlow(new Runnable() {
            @Override
            public void run() {
                System.out.println("Conexión lenta, verifique");
            }
        });

        connection.connected(new Runnable() {
            @Override
            public void run() {


                proxy.invoke("registerConId", "1");
                System.out.println("Está conectado");
            }
        });

        connection.stateChanged(new StateChangedCallback() {
            @Override
            public void stateChanged(ConnectionState oldState, ConnectionState newState) {
                System.out.println("Pasó de " + oldState.toString() + " a " + newState.toString());
            }
        });
        connection.start()
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {

                        System.out.println("Iniciando");
                    }
                });
    }
}
