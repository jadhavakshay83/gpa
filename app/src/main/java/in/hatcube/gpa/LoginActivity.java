package in.hatcube.gpa;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button login;
    TextInputEditText email,password;
    private DBHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize database
        mydb = new DBHelper(this);

        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String inputEmail = email.getText().toString();
               String inputPassword = password.getText().toString();
               if(inputEmail.isEmpty() || inputPassword.isEmpty()) {
                   Toast.makeText(getApplicationContext(), "Enter email and password to proceed", Toast.LENGTH_SHORT).show();
               } else {
                   Cursor user = mydb.getUser(inputEmail);
                   if(user.getCount() == 0) {
                       Toast.makeText(getApplicationContext(), "Invalid email provided, User does not exists", Toast.LENGTH_SHORT).show();
                   } else {
                       user.moveToFirst();
                       String dbPassword = user.getString(user.getColumnIndex(DBHelper.USERS_COLUMN_PASSWORD));
                       if(dbPassword.equals(inputPassword)) {
                           String authType = user.getString(user.getColumnIndex(DBHelper.USERS_COLUMN_AUTH));
                           String gpa = user.getString(user.getColumnIndex(DBHelper.USERS_COLUMN_GPA));
                           Bundle dataBundle = new Bundle();
                           dataBundle.putString(DBHelper.USERS_COLUMN_EMAIL, inputEmail);
                           dataBundle.putString(DBHelper.USERS_COLUMN_GPA, gpa);
                           if(authType.equals(Constants.PVS)) {
                               Intent intent = new Intent(getApplicationContext(), LoggedinActivity.class);
                               intent.putExtras(dataBundle);
                               startActivity(intent);
                           } else if(authType.equals(Constants.CPS)) {
                               Intent intent = new Intent(getApplicationContext(), CcsActivity.class);
                               intent.putExtras(dataBundle);
                               startActivity(intent);
                           }
                       } else {
                           Toast.makeText(getApplicationContext(), "Incorrect password, please try again!", Toast.LENGTH_SHORT).show();
                       }
                   }
               }
            }
        });
    }
}
