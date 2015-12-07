package com.example.borja.obtenerpaismedianterest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private Button bt;
    private EditText dirip;
    private String resultado;
    private String pais;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        bt = (Button) findViewById(R.id.bt);
        dirip = (EditText) findViewById(R.id.dirip);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String direccionip = dirip.getText().toString();
                AsyncPost task = new AsyncPost();
                task.execute(direccionip);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class AsyncPost extends AsyncTask<String,Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpURLConnection conn;
                URL url = new URL("http://www.webservicex.net/geoipservice.asmx/GetGeoIP");
                //you need to encode ONLY the values of the parameters
                String param="IPAddress=" + URLEncoder.encode(params[0], "UTF-8");//+
                //“&param2=”+URLEncoder.encode(“value2″,”UTF-8″)+
                //“&param3 =”+URLEncoder.encode(“value3″,”UTF-8″);
                conn=(HttpURLConnection)url.openConnection();
                //set the output to true, indicating you are outputting(uploading) POST data
                conn.setDoOutput(true);
                //once you set the output to true, you don’t really need to set the request method to post, but I’m doing it anyway
                conn.setRequestMethod("POST");
                //Android documentation suggested that you set the length of the data you are sending to the server, BUT
                // do NOT specify this length in the header by using conn.setRequestProperty(“Content-Length”, length);
                //use this instead.
                conn.setFixedLengthStreamingMode(param.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //send the POST out
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.close();
                //build the string to store the response text from the server
                String result = "";
                pais="";
                resultado="";
                //start listening to the stream
                Scanner inStream = new Scanner(conn.getInputStream());
                //process the stream and store it in StringBuilder
                while(inStream.hasNextLine()) {
                    result =(inStream.nextLine());
                    resultado+=result;
                    if (result.indexOf("CountryName") > 0)
                        pais=result.replace("<CountryName>","").replace("</CountryName>","");
                }
            } catch (MalformedURLException e) {
                Log.e("A", "exception: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e("A", "exception: " + e.getMessage());
            } catch (IOException e) {
                Log.e("A", "exception: " + e.getMessage());
            } catch (Exception e) {
                Log.e("A", "exception: " + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute (Void result){
            Toast.makeText(MainActivity.this, "Pais:"+ pais +"\nResultado: "+resultado, Toast.LENGTH_LONG).show();
        }
    }
}
