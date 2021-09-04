package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class donate extends AppCompatActivity {

    Button btn_btc, btn_eth, btn_rvn, btn_doge, btn_ltc, btn_xmr;
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        close = findViewById(R.id.topnav_donate_close);
        btn_btc = findViewById(R.id.btn_copy_btc);
        btn_eth = findViewById(R.id.btn_copy_eth);
        btn_rvn = findViewById(R.id.btn_copy_rvn);
        btn_doge = findViewById(R.id.btn_copy_doge);
        btn_ltc = findViewById(R.id.btn_copy_ltc);
        btn_xmr = findViewById(R.id.btn_copy_xmr);

        //Listeners
        close.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );

        btn_btc.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Address", getText(R.string.address_btc));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getText(R.string.toast_donate_success), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btn_eth.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Address", getText(R.string.address_eth));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getText(R.string.toast_donate_success), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btn_rvn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Address", getText(R.string.address_rvn));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getText(R.string.toast_donate_success), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btn_doge.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Address", getText(R.string.address_doge));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getText(R.string.toast_donate_success), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btn_ltc.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Address", getText(R.string.address_ltc));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getText(R.string.toast_donate_success), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btn_xmr.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Address", getText(R.string.address_xmr));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getText(R.string.toast_donate_success), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}