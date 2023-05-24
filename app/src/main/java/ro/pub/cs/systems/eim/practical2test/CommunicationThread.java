package ro.pub.cs.systems.eim.practical2test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (pokemonName)");

            // Read the pokemonName value sent by the client
            String pokemonName = bufferedReader.readLine();
            if (pokemonName == null || pokemonName.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (pokemonName)!");
                return;
            }

            // It checks whether the serverThread has already received the pokemon information for the given pokemonName.
            HashMap<String, PokemonInformation> data = serverThread.getData();
            PokemonInformation pokemonInformation;
            if (data.containsKey(pokemonName)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                pokemonInformation = data.get(pokemonName);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";

                // make the HTTP request to the web service
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + pokemonName);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else if (pageSourceCode.equals("Not Found")) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Pokemon does not exist!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);

                // Parse the page source code into a JSONObject and extract the needed information
                pokemonInformation = new ObjectMapper().readValue(pageSourceCode, PokemonInformation.class);

                // Cache the information for the given pokemonName
                serverThread.setData(pokemonName, pokemonInformation);
            }

            if (pokemonInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }

            // Send the information back to the client
            String types = pokemonInformation.getTypes().toString();
            String abilities = pokemonInformation.getAbilities().toString();
            String image = pokemonInformation.getImage();

            Bitmap bmp = BitmapFactory.decodeStream((new URL(image)).openConnection().getInputStream());

            // Send the result back to the client
            printWriter.println("Type: " + types + '\n' + "Abilities: " + abilities + '\n' + Utilities.bitmapToString(bmp));
            printWriter.flush();
        } catch (IOException | IllegalArgumentException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}
