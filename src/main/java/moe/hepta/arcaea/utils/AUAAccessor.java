package moe.hepta.arcaea.utils;

import moe.hepta.arcaea.Arcaea;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AUAAccessor {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1, TimeUnit.MINUTES)
            .build();

    public static Response request(String path) throws IOException {
        Request request = new Request.Builder().url(Arcaea.instance.apiAddress + path)
                .header("Authorization", "bearer " + Arcaea.instance.APIToken)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (response.code() != 200) {
            Arcaea.instance.logger.error("Response code: " + response.code());
        }
        return response;
    }

    public static @Nullable String requestString(String path) throws IOException {
        Response response = request(path);
        try {
            String result = Objects.requireNonNull(response.body()).string();
            response.close();
            return result;
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public static byte[] requestBytes(String path) throws IOException {
        Response response = request(path);
        if (response.code() != 200) return null;
        try {
            byte[] result = Objects.requireNonNull(response.body()).bytes();
            response.close();
            return result;
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    @Nullable
    public static String requestStringWithParams(String path, Map<String, String> params) throws IOException {
        return requestString(path + resolveQueryParams(params));
    }

    public static byte[] requestBytesWithParams(String path, Map<String, String> params)  throws IOException {
        return requestBytes(path + resolveQueryParams(params));
    }

    private static String resolveQueryParams(@NotNull Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder("?");
        for (String key : params.keySet()) {
            stringBuilder.append(key).append("=").append(params.get(key)).append("&");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
