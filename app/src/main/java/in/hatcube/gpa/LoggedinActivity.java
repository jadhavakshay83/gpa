package in.hatcube.gpa;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoggedinActivity extends AppCompatActivity {

    Button logout;
    TextView textName,textEmail,textPhone;
    private DBHelper mydb ;
    private Bundle extras;
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
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
