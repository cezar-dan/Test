package ro.pub.cs.systems.eim.practical2test;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String pokemonName;
    private final TextView pokemonInfoTextView;
    private final ImageView pokemonImageView;

    private Socket socket;

    public ClientThread(String address, int port, String pokemonName, TextView pokemonInfoTextView, ImageView pokemonImageView) {
        this.address = address;
        this.port = port;
        this.pokemonName = pokemonName;
        this.pokemonInfoTextView = pokemonInfoTextView;
        this.pokemonImageView = pokemonImageView;
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the city and information type to the server
            printWriter.println(pokemonName);
            printWriter.flush();
            String pokemonInformation;

            // reads the weather information from the server
            StringBuilder receivedOutput = new StringBuilder();
            if ((pokemonInformation = bufferedReader.readLine()) != null) {
                receivedOutput.append(pokemonInformation).append("\n");
                receivedOutput.append(bufferedReader.readLine()).append("\n");
            } else {
                pokemonInfoTextView.post(() -> pokemonInfoTextView.setText(new String("Specified pokemon does not exist!")));
                pokemonImageView.post(() -> pokemonImageView.setImageBitmap(null));
                return;
            }
            while ((pokemonInformation = bufferedReader.readLine()) != null) {
                receivedOutput.append(pokemonInformation);
            }

            String[] finalizedPokemonInfo = receivedOutput.toString().split("\n");
            Bitmap bmp = Utilities.stringToBitmap(finalizedPokemonInfo[2]);
            finalizedPokemonInfo[2] = "";

            // updates the UI with the weather information. This is done using post() method to ensure it is executed on UI thread
            pokemonInfoTextView.post(() -> pokemonInfoTextView.setText(String.join("\n", finalizedPokemonInfo)));
            pokemonImageView.post(() -> pokemonImageView.setImageBitmap(bmp));

        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred:  " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
