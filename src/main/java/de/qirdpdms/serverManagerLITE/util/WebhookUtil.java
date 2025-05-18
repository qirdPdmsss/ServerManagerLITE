package de.qirdpdms.serverManagerLITE.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebhookUtil {

    public static void sendWebhook(String webhookUrl, String title, String description, int color) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject embed = new JsonObject();
            embed.addProperty("title", title);
            embed.addProperty("description", description);
            embed.addProperty("color", color);

            JsonArray embedsArray = new JsonArray();
            embedsArray.add(embed);

            JsonObject payload = new JsonObject();
            payload.add("embeds", embedsArray);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            connection.getInputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
