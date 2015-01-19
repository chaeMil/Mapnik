package cz.mapnik.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by chaemil on 19.1.15.
 */
public class BugReport extends Activity {
    private static String BUG_REPORT_EMAIL = "chaemil72@gmail.com";
    private static String BUG_REPORT_SUBJECT = "Mapnik Bug Report";

    private StringBuilder log;
    private TextView logcat;
    private EditText email;
    private EditText bugSpecification;
    private int reportValidity = 0;
    private Button submitButton;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bug_report);

        bugSpecification = (EditText) findViewById(R.id.bugSpecification);
        email = (EditText) findViewById(R.id.email);
        logcat = (TextView) findViewById(R.id.logcat);
        submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReport();
            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.custom_font_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());

        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }

            logcat.setText(log);

        } catch (IOException e) {
        }
    }

    public void sendReport() {
        reportValidity = 0;

        if (!email.getText().toString().equals("")) {
            reportValidity += 1;
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.put_email),
                    Toast.LENGTH_LONG).show();
        }

        if (!bugSpecification.getText().toString().equals("")) {
            reportValidity += 1;
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.put_bug_specification),
                    Toast.LENGTH_LONG).show();
        }

        if (logcat.getText() != null) {
            reportValidity += 1;
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.logcat_is_empty),
                    Toast.LENGTH_LONG).show();
        }

        if (reportValidity == 3) {

            String emailBody = bugSpecification.getText() + "\n\n\n" + logcat.getText();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{BUG_REPORT_EMAIL});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, BUG_REPORT_SUBJECT);
            emailIntent.putExtra(Intent.EXTRA_TEXT   , emailBody);

            try {
                startActivityForResult(emailIntent, 1);
                finish();
                App.log("Finished sending email...", "");
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(BugReport.this,
                        "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
