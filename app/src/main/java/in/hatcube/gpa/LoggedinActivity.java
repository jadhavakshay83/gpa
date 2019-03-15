package in.hatcube.gpa;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoggedinActivity extends AppCompatActivity {

    Button logout;
    TextView textName,textEmail,textPhone;
    ImageView icon1,icon2,icon3,icon4;
    private DBHelper mydb ;
    private Bundle extras;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        //Initialize database
        mydb = new DBHelper(this);

        //Get user input data
        extras = getIntent().getExtras();

        textName    = findViewById(R.id.textName);
        textEmail   = findViewById(R.id.textEmail);
        textPhone   = findViewById(R.id.textPhone);
        logout      = findViewById(R.id.logout);
        icon1       = findViewById(R.id.icon1);
        icon2       = findViewById(R.id.icon2);
        icon3       = findViewById(R.id.icon3);
        icon4       = findViewById(R.id.icon4);

        Cursor user = mydb.getUser(extras.getString(DBHelper.USERS_COLUMN_EMAIL));
        if(user.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Invalid email provided, User does not exists", Toast.LENGTH_SHORT).show();
        } else {
            user.moveToFirst();
            String name     = user.getString(user.getColumnIndex(DBHelper.USERS_COLUMN_NAME));
            String email    = user.getString(user.getColumnIndex(DBHelper.USERS_COLUMN_EMAIL));
            String phone    = user.getString(user.getColumnIndex(DBHelper.USERS_COLUMN_PHONE));

            textName.setText(name);
            textEmail.setText(email);
            textPhone.setText(phone);

            try {
                Drawable icon_1 = getPackageManager().getApplicationIcon("com.whatsapp");
                icon1.setImageDrawable(icon_1);

                Drawable icon_2 = getPackageManager().getApplicationIcon("com.facebook.katana");
                icon2.setImageDrawable(icon_2);

                Drawable icon_3 = getPackageManager().getApplicationIcon("com.google.android.gm");
                icon3.setImageDrawable(icon_3);

                Drawable icon_4 = getPackageManager().getApplicationIcon("com.google.android.youtube");
                icon4.setImageDrawable(icon_4);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            icon1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   openExternalApp("com.whatsapp");
                }
            });

            icon2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openExternalApp("com.facebook.katana");
                }
            });

            icon3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openExternalApp("com.google.android.gm");
                }
            });

            icon4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openExternalApp("com.google.android.youtube");
                }
            });
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to LOGOUT", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void openExternalApp(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(intent);
    }
}
