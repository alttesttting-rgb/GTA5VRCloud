package com.example.gta5vrcloud;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    // This is the LAN address of the device currently hosting PinkPy Runner.
    // Change it if that device receives a different local IP address.
    private static final String DEFAULT_HEALTH_URL =
            "http://192.168.1.48:8080/health";

    private final ExecutorService networkExecutor =
            Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextView status;
    private EditText endpointInput;
    private Button connectButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        enterImmersiveMode();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48, 36, 48, 36);
        root.setBackgroundColor(Color.rgb(15, 8, 30));

        TextView title = new TextView(this);
        title.setText("GTA5 VR");
        title.setTextColor(Color.WHITE);
        title.setTextSize(38);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("THE BEANS CLOUD  •  QUEST PROTOTYPE");
        subtitle.setTextColor(Color.rgb(190, 150, 255));
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.bottomMargin = 28;
        root.addView(subtitle, subtitleParams);

        TextView endpointLabel = new TextView(this);
        endpointLabel.setText("Cloud health-check address");
        endpointLabel.setTextColor(Color.WHITE);
        endpointLabel.setTextSize(15);
        root.addView(endpointLabel);

        endpointInput = new EditText(this);
        endpointInput.setSingleLine(true);
        endpointInput.setText(DEFAULT_HEALTH_URL);
        endpointInput.setTextColor(Color.WHITE);
        endpointInput.setHintTextColor(Color.LTGRAY);
        endpointInput.setSelectAllOnFocus(true);
        endpointInput.setTextSize(14);
        endpointInput.setInputType( android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(-1, -2);
        inputParams.bottomMargin = 20;
        root.addView(endpointInput, inputParams);

        status = new TextView(this);
        status.setText("NOT CONNECTED — no request has been made yet.");
        status.setTextColor(Color.rgb(235, 225, 255));
        status.setTextSize(17);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(-1, -2);
        statusParams.bottomMargin = 24;
        root.addView(status, statusParams);

        connectButton = new Button(this);
        connectButton.setText("TEST CLOUD CONNECTION");
        connectButton.setOnClickListener(v -> testCloudConnection());
        root.addView(connectButton, new LinearLayout.LayoutParams(-1, -2));

        TextView note = new TextView(this);
        note.setText("Connection test only. GTA V streaming and OpenXR gameplay are not implemented yet.");
        note.setTextColor(Color.LTGRAY);
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams noteParams =
                new LinearLayout.LayoutParams(-1, -2);
        noteParams.topMargin = 24;
        root.addView(note, noteParams);

        setContentView(root);
    }

    private void testCloudConnection() {
        final String healthUrl = endpointInput.getText().toString().trim();

        if (!healthUrl.startsWith("http://") && !healthUrl.startsWith("https://")) {
            status.setText("CONNECTION FAILED — enter a valid http:// or https:// address.");
            return;
        }

        status.setText("CONNECTING… waiting for a real server response.");
        connectButton.setEnabled(false);

        networkExecutor.execute(() -> {
            HttpURLConnection connection = null;
            String resultText;
            boolean connected = false;

            try {
                URL url = new URL(healthUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setUseCaches(false);
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    java.io.InputStream input = connection.getInputStream();
                    java.io.ByteArrayOutputStream output =
                            new java.io.ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                    input.close();

                    JSONObject json = new JSONObject(
                            output.toString("UTF-8"));
                    connected = "The Beans Cloud".equals(
                            json.optString("service"))
                            && "online".equals(json.optString("status"));

                    if (connected) {
                        resultText = "CONNECTED — The Beans Cloud returned a real ONLINE response.";
                    } else {
                        resultText = "SERVER REPLIED, BUT THE RESPONSE DID NOT MATCH The Beans Cloud health check.";
                    }
                } else {
                    resultText = "CONNECTION FAILED — server returned HTTP "
                            + responseCode + ".";
                }
            } catch (java.net.SocketTimeoutException e) {
                resultText = "CONNECTION FAILED — server timed out.";
            } catch (Exception e) {
                String detail = e.getMessage();
                resultText = "CONNECTION FAILED — "
                        + (detail == null ? e.getClass().getSimpleName() : detail);
            } finally {
                if (connection != null) connection.disconnect();
            }

            final String finalText = resultText;
            final boolean finalConnected = connected;
            mainHandler.post(() -> {
                status.setText(finalText);
                status.setTextColor(finalConnected
                        ? Color.rgb(100, 235, 150)
                        : Color.rgb(255, 145, 145));
                connectButton.setEnabled(true);
            });
        });
    }

    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enterImmersiveMode();
    }

    @Override
    protected void onDestroy() {
        networkExecutor.shutdownNow();
        super.onDestroy();
    }
}
