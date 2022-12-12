package io.github.kuwa0824.linecalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity 
{
    private Button b1, b2, b3, b4;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        b1 = findViewById(R.id.main_b1);
        b2 = findViewById(R.id.main_b2);
        b3 = findViewById(R.id.main_b3);
        b4 = findViewById(R.id.main_b4);
        
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MslActivity.class);
                startActivity(i);
            }
        });
        
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DiffmslActivity.class);
                startActivity(i);
            }
        });
        
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SlActivity.class);
                startActivity(i);
            }
        });
        
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SlasymActivity.class);
                startActivity(i);
            }
        });
    }
}
