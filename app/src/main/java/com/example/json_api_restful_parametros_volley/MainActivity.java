package com.example.json_api_restful_parametros_volley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText txtUsuario;
    EditText txtClave;
    TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsuario = (EditText)findViewById(R.id.txtUsuario);
        txtClave = (EditText)findViewById(R.id.txtClave);
        txtError = (TextView)findViewById(R.id.txtResultado);
    }

    public void btn_logIn(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.uealecpeterson.net/public/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //TRY-CATCH para validar el error que se pueda presentar al formatear el JSON.
                        try {
                            txtError.setText("");
                            String token = get_token_JSON(response);

                            //Envíar el token a la segunda actividad y presentarla.
                            Intent intent = new Intent(MainActivity.this, ProductsActivity.class);

                            Bundle b = new Bundle();
                            b.putString("access_token", token);

                            intent.putExtras(b);
                            startActivity(intent);

                        } catch (JSONException e) {
                            txtError.setText(e.getMessage().toString());
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Formateando los errores que se mostrarán basado en el status de la petición.
                        if(error.networkResponse.statusCode == 400){
                            txtError.setText("Error en el correo o contraseña ingresada.");
                        }
                    }
                }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("correo", txtUsuario.getText().toString());
                params.put("clave", txtClave.getText().toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }


    private String get_token_JSON(String response) throws JSONException {
        JSONObject jObject = new JSONObject(response);

        String access_token = "";

        //Si no contiene el atributo error, entonces se obtiene el access_token
        if(!jObject.has("error")){
            access_token =  jObject.get("access_token").toString();
        }

        return access_token;
    }
}