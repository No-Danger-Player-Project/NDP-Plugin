package top.alazeprt.ndps.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpUtil {
    public static final String url = "http://222.186.150.8:5020/";
    public static final List<NBanEntry> bans = new ArrayList<>();
    public static final String token = "ndp_pwd_114514";
    static long t = 0;
    static boolean close = false;
    public static final Thread thread = new Thread(() -> {
        while (!close) {
            try {
                Thread.sleep(1000);
                t++;
            } catch (InterruptedException ignored) {}
            if (t % 10 == 0) {
                try {
                    String data = get(url + "bans", "{}");
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(data, JsonObject.class);
                    bans.clear();
                    for (JsonElement element : json.getAsJsonArray("recent_actions")) {
                        JsonObject action = element.getAsJsonObject();
                        String username = action.get("username").getAsString();
                        String ip = action.get("ip").getAsString();
                        String reason = action.get("cause").getAsString();
                        NBanEntry entry = new NBanEntry(username, ip, reason);
                        bans.add(entry);
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public static void init() {
        thread.start();
    }

    public static void stop() {
        close = true;
    }

    public static Optional<NBanEntry> getBan(String data) {
        for (NBanEntry ban : bans) {
            if (ban.name().equals(data) || ban.ip().equals(data)) {
                return Optional.of(ban);
            }
        }
        return Optional.empty();
    }

    public static void addBan(NBanEntry banEntry) throws IOException, ParseException {
        bans.add(banEntry);
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verification", token);
        jsonObject.addProperty("action", "ban");
        jsonObject.addProperty("username", banEntry.name());
        jsonObject.addProperty("ip", banEntry.reason());
        jsonObject.addProperty("cause", banEntry.reason());
        post(url + "add_ban", gson.toJson(jsonObject));
    }

    public static void removeBan(NBanEntry banEntry, String reason) throws IOException, ParseException {
        bans.remove(banEntry);
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("verification", token);
        jsonObject.addProperty("action", "remove");
        jsonObject.addProperty("username", banEntry.name());
        jsonObject.addProperty("ip", banEntry.reason());
        jsonObject.addProperty("cause", reason);
        post(url + "add_ban", gson.toJson(jsonObject));
    }

    public static String get(String url, String data) throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder;
            try {
                uriBuilder = new URIBuilder(url);
            } catch (Exception e) {
                throw new IOException("Invalid URL: " + url, e);
            }

            // 解析查询参数并处理编码
            List<NameValuePair> params = URLEncodedUtils.parse(data, StandardCharsets.UTF_8);
            uriBuilder.addParameters(params);

            HttpGet httpGet;
            try {
                httpGet = new HttpGet(uriBuilder.build());
            } catch (Exception e) {
                throw new IOException("Failed to build URI with parameters", e);
            }

            // 执行请求并返回结果
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }

    public static String post(String url, String data) throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            // 设置JSON请求体并指定编码
            ContentType contentType = ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8);
            StringEntity entity = new StringEntity(data, contentType);
            httpPost.setEntity(entity);

            // 执行请求并返回结果
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }
}
