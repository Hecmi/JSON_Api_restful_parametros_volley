package com.example.json_api_restful_parametros_volley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductsActivity extends AppCompatActivity {

    TextView txtProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        Bundle b = this.getIntent().getExtras();

        txtProductos = (TextView) findViewById(R.id.txtProductos);

        get_productos(b.getString("access_token"));
    }

    private void get_productos(String token){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.uealecpeterson.net/public/productos/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //TRY-CATCH para validar el error que se pueda presentar al formatear el JSON.
                        try {
                            txtProductos.setText(formatear_JSON(response));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Formateando los errores que se mostrarán basado en el status de la petición.
                        if(error.networkResponse.statusCode == 400){
                            txtProductos.setText("Error en el los parámetros enviados.");
                        }
                        if(error.networkResponse.statusCode == 401){
                            txtProductos.setText("Error en token de autorización.");
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("fuente", "1");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }


        };

        queue.add(stringRequest);
    }

    private String formatear_JSON(String response) throws JSONException {

        JSONObject jObject = new JSONObject(response);

        JSONArray jArray = jObject.getJSONArray("productos");

        String lstProductos = "Código - Descripción\n";

        for (int i = 0; i < jArray.length(); i++){
            JSONObject jProducto = jArray.getJSONObject(i);

            lstProductos += "\n" + jProducto.get("id") + " - " + jProducto.get("descripcion");
        }

        return lstProductos;
    }
}