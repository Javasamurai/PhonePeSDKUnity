package com.wrapper.phonepe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.phonepe.intent.sdk.api.B2BPGRequest;
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import com.phonepe.intent.sdk.api.UPIApplicationInfo;
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Main extends UnityPlayerActivity {
    static String apiEndPoint = "/pg/v1/pay";
    static String packageName = "com.phonepe.simulator";
    private static int B2B_PG_REQUEST_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void InitPhonePe(Context context, int environment, String merchantID, String appID)
    {
        PhonePeEnvironment currentEnvironment = PhonePeEnvironment.SANDBOX;
        for (PhonePeEnvironment env : PhonePeEnvironment.values()) {
            if (env.ordinal() == environment) {
                currentEnvironment = env;
            }
        }

        Log.d("Zain", "Phonepe init called successfully:" + context);
        PhonePe.init(context, currentEnvironment, merchantID, appID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == B2B_PG_REQUEST_CODE) {
            UnityPlayer.UnitySendMessage("PhonePeSDK", "OnTransactionDone", resultCode == 1 ? "SUCCESS" : "FAILURE");
        }
    }

    public static void CreateTransaction(Context context, String merchantID, String salt, int saltIndex, float amount) throws JSONException, NoSuchAlgorithmException, FileNotFoundException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId", merchantID);
        jsonObject.put("merchantUserId", System.currentTimeMillis());
        jsonObject.put("merchantTransactionId", "AXDSDS");
        jsonObject.put("callbackUrl", "https://webhook.site/61639a71-bf77-46d0-88a5-5d3beb9513c7");
        jsonObject.put("amount", amount);
        jsonObject.put("mobileNumber", "7976062916");

        JSONObject paymentInstrument = new JSONObject();
        JSONObject deviceContext = new JSONObject();
        deviceContext.put("deviceOS", "ANDROID");

        paymentInstrument.put("type", "UPI_INTENT");
        paymentInstrument.put("targetApp", packageName);
        jsonObject.put("paymentInstrument", paymentInstrument);
        jsonObject.put("deviceContext", deviceContext);

        String base64 = new String(Base64.encodeToString(jsonObject.toString().getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));

        String checksum = sha256(base64 + apiEndPoint + salt) + "###" + saltIndex;
        B2BPGRequest b2BPGRequest = new B2BPGRequestBuilder()
                .setData(base64).
                setChecksum(checksum)
                .setUrl(apiEndPoint)
                .build();
        try {
            Intent intent = PhonePe.getImplicitIntent(context, b2BPGRequest, packageName);
            ((Activity)context).startActivityForResult(intent, B2B_PG_REQUEST_CODE);

        } catch (PhonePeInitException e) {
            throw new RuntimeException(e);
        }
    }

    private static String sha256(String input) throws FileNotFoundException {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = md.digest(inputBytes);

        return bytesToHex(digest);
    }
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static boolean IsPhonePeInstalled()
    {
        try {
            List<UPIApplicationInfo> upiApps = PhonePe.getUpiApps();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return upiApps.stream().filter(n -> n.getPackageName() == "com.phonepe.simulator" || n.getPackageName() == "com.phonepe.app").count() >= 0;
            } 
            return false;
        } catch (PhonePeInitException e) {
            throw new RuntimeException(e);
        }
    }
}
