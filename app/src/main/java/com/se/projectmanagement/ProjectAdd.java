package com.se.projectmanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProjectAdd extends AppCompatActivity {

    EditText title, description;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_add);

        title = (EditText)findViewById(R.id.projectTitle);
        description = (EditText)findViewById(R.id.projectDescription);

        add = (Button) findViewById(R.id.addProject);
        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URL url = new URL("http://10.0.2.2:7777/api/projects/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");

                    if (MainActivity.msCookieManager.getCookieStore().getCookies().size() > 0) {
                        // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                        System.out.println("COOKIES");
                        conn.setRequestProperty("Cookie",
                                TextUtils.join(";",  MainActivity.msCookieManager.getCookieStore().getCookies()));
                    }

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("title", title.getText().toString())
                            .appendQueryParameter("description", description.getText().toString());
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder(in.available());
                    String line;
                    while ((line = reader.readLine()) != null) {
                        total.append(line).append('\n');
                    }
                    System.out.println(total.toString());

                    if (conn.getResponseCode() == 200) {
                        Toast.makeText(ProjectAdd.this, "Project added successfully!", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(ProjectAdd.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                    conn.disconnect();

                } catch (MalformedURLException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                // TODO: If Settings has multiple levels, Up should navigate up
                // that hierarchy.
                setResult(Activity.RESULT_OK);
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
