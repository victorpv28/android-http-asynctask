package com.example.logonrm.aula03;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.logonrm.aula03.adapter.ListaAndroidAdapter;
import com.example.logonrm.aula03.model.AndroidVersao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private RecyclerView rvLista;
    private ListaAndroidAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        rvLista = (RecyclerView) findViewById(R.id.rvLista);

        new BuscaDados().execute("http://www.mocky.io/v2/58af1fb21000001e1cc94547");

    }

    private class BuscaDados extends AsyncTask<String, Void, String>{

        ProgressDialog pdLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading = new ProgressDialog(ListaActivity.this);
            pdLoading.setMessage("Carregando os dados");
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);

                conn.setRequestMethod("GET");

                conn.setDoOutput(true);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                    InputStream is = conn.getInputStream();
                    BufferedReader buffer =
                            new BufferedReader(
                                    new InputStreamReader(is));

                    StringBuilder result =  new StringBuilder();
                    String linha;

                    while((linha = buffer.readLine()) != null){
                        result.append(linha);
                    }

                    conn.disconnect();
                    return  result.toString();

                }


            }catch (MalformedURLException e){

            }catch (IOException ioe){

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

            if (s == null){
                Toast.makeText(ListaActivity.this, "Errou", Toast.LENGTH_SHORT).show();

            }else {
                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray jsonArray = json.getJSONArray("android");

                    List<AndroidVersao> lista = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        AndroidVersao v = new AndroidVersao();
                        v.setApi(data.getString("api"));
                        v.setNome(data.getString("nome"));
                        v.setUrlImagem(data.getString("urlImagem"));
                        v.setVersao(data.getString("versao"));

                        lista.add(v);

                    }

                    setUpLista(lista);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            pdLoading.dismiss();
        }

    }

    private void setUpLista(List<AndroidVersao> lista) {
        adapter = new ListaAndroidAdapter(this, lista);
        rvLista.setLayoutManager(new LinearLayoutManager(this));
        rvLista.setAdapter(adapter);

    }

}
