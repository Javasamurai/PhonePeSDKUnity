package com.example.capital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import com.phonepe.intent.sdk.api.B2BPGRequest;
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import com.phonepe.intent.sdk.api.UPIApplicationInfo;
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment;
import com.wrapper.phonepe.R;
import com.wrapper.phonepe.UnityPlayerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class MainActivity extends UnityPlayerActivity {
    private Button clickButton;
    String apiEndPoint = "/pg/v1/pay";
    String merchantID = "PGTESTPAYUAT";
    String packageName = "com.phonepe.simulator";
    private static int B2B_PG_REQUEST_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitPhonePe();
        setContentView(R.layout.activity_main);
        clickButton = findViewById(R.id.clickmeButton);

        clickButton.setOnClickListener(view -> {
            try {
                CreateTransaction();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private String sha256(String input) throws FileNotFoundException {
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

    private void CreateTransaction() throws JSONException, NoSuchAlgorithmException, FileNotFoundException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId", merchantID);
        jsonObject.put("merchantUserId", System.currentTimeMillis());
        jsonObject.put("merchantTransactionId", "AXDSDS");
        jsonObject.put("callbackUrl", "https://webhook.site/61639a71-bf77-46d0-88a5-5d3beb9513c7");
        jsonObject.put("amount", 200);
        jsonObject.put("mobileNumber", "7976062916");

        JSONObject paymentInstrument = new JSONObject();
        JSONObject deviceContext = new JSONObject();
        deviceContext.put("deviceOS", "ANDROID");

        paymentInstrument.put("type", "UPI_INTENT");
        paymentInstrument.put("targetApp", packageName);
        jsonObject.put("paymentInstrument", paymentInstrument);
        jsonObject.put("deviceContext", deviceContext);

        String base64 = new String(Base64.encodeToString(jsonObject.toString().getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));

        String checksum = sha256(base64 + apiEndPoint + "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399") + "###1";
        B2BPGRequest b2BPGRequest = new B2BPGRequestBuilder()
                .setData(base64).
                setChecksum(checksum)
                .setUrl(apiEndPoint)
                .build();
        try {
            List<UPIApplicationInfo> apps = GetUPIApps();
//            String signature = GetPackageSignature();

            Intent intent = PhonePe.getImplicitIntent(this, b2BPGRequest, packageName);
            startActivityForResult(intent, B2B_PG_REQUEST_CODE);
//            startActivityForResult(PhonePe.getImplicitIntent(this, b2BPGRequest, packageName), B2B_PG_REQUEST_CODE);
        } catch (PhonePeInitException e) {
            throw new RuntimeException(e);
        }
    }

    private String GetPackageSignature()
    {
        try {
            return PhonePe.getPackageSignature();
        } catch (PhonePeInitException e) {
            throw new RuntimeException(e);
        }
    }

    private List<UPIApplicationInfo> GetUPIApps() {
        PhonePe.setFlowId("12232321");
        try {
            return PhonePe.getUpiApps();
        } catch (PhonePeInitException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == B2B_PG_REQUEST_CODE) {
            Log.d("Request code", data.toString());
        }
    }


    private void InitPhonePe()
    {
        PhonePe.init(this, PhonePeEnvironment.SANDBOX, merchantID, null);
    }
}