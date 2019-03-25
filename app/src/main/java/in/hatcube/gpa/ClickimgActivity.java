package in.hatcube.gpa;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class ClickimgActivity extends Activity {

    RelativeLayout interactiveImg;
    LinearLayout clickableArea;
    int firstImg = 0,secondImg = 0,thirdImg = 0,firstImgClick,secondImgClick,thirdImgClick;
    boolean clickOne, clickTwo;
    private DBHelper mydb ;
    private Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clickimg);

        //Initialize database
        mydb = new DBHelper(this);

        //Get user input data
        extras = getIntent().getExtras();

        //Set any random image as background
        interactiveImg = findViewById(R.id.interactive_img);
        firstImg = getRandomNumber(10,0, 0);
        interactiveImg.setBackground(GetImage(getApplicationContext(),"ccs_"+firstImg));

        //Set any random area as clickable
        clickableArea = findViewById(R.id.clickable_area);
        firstImgClick = getRandomNumber(4, 0, 0);
        setClickableArea(firstImgClick);

        clickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(clickOne && clickTwo) {
                    saveData();
                }

                if(secondImg == 0) {
                    secondImg = getRandomNumber(10,firstImg,0);
                    interactiveImg.setBackground(GetImage(getApplicationContext(),"ccs_"+secondImg));

                    secondImgClick = getRandomNumber(4, firstImgClick, 0);
                    setClickableArea(secondImgClick);
                    clickOne = true;
                } else if(thirdImg == 0) {
                    thirdImg = getRandomNumber(10, firstImg, secondImg);
                    interactiveImg.setBackground(GetImage(getApplicationContext(),"ccs_"+thirdImg));

                    thirdImgClick = getRandomNumber(4, firstImgClick, secondImgClick);
                    setClickableArea(thirdImgClick);
                    clickTwo = true;
                }
            }
        });
    }

    public static int getRandomNumber(int bound,int except, int except1) {
        Random rand = new Random();
        // Obtain a number between [0 - 9].
        int number = rand.nextInt(bound);
        // Add 1 to the result to get a number from the required range
        // (i.e., [1 - 10]).
        number += 1;
        if(number == except){
            getRandomNumber(bound,except,except1);
        } else if(number == except1){
            getRandomNumber(bound,except,except1);
        }
        return number;
    }

    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }

    public void setClickableArea(int position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500,700);
        switch (position) {
            case 1:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case 2:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                clickableArea.setLayoutParams(params);
                break;
            case 3:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                clickableArea.setLayoutParams(params);
                break;
            case 4:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                clickableArea.setLayoutParams(params);
                break;
            default:
                break;
        }
        clickableArea.setLayoutParams(params);
    }

    public void saveData() {
        JSONObject json = new JSONObject();
        JSONArray imagesArr = new JSONArray();
        JSONArray clicksArr = new JSONArray();
        try {
            imagesArr.put(firstImg);
            imagesArr.put(secondImg);
            imagesArr.put(thirdImg);
            clicksArr.put(firstImgClick);
            clicksArr.put(secondImgClick);
            clicksArr.put(thirdImgClick);
            json.put("selectedImages", imagesArr);
            json.put("selectedClicks", clicksArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String gpa = json.toString();

        if(extras.containsKey(DBHelper.USERS_COLUMN_NAME) && extras.containsKey(DBHelper.USERS_COLUMN_PHONE)) {
            insertUser(gpa);
        } else {
            updateUser(gpa);
        }
    }

    public void insertUser(String gpa) {
        Boolean insertSuccess = mydb.insertUser(extras.getString(DBHelper.USERS_COLUMN_NAME), extras.getString(DBHelper.USERS_COLUMN_PHONE), extras.getString(DBHelper.USERS_COLUMN_EMAIL), extras.getString(DBHelper.USERS_COLUMN_PASSWORD),Constants.CPS,gpa);
        if(insertSuccess){
            Toast.makeText(getApplicationContext(), "New User created!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sign Up Successful, Please login to continue")
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

            AlertDialog d = builder.create();
            d.setTitle("Success!");
            d.show();
        } else{
            Toast.makeText(getApplicationContext(), "New User creation failure!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUser(String gpa) {
        Boolean insertSuccess = mydb.updateGPA(extras.getString(DBHelper.USERS_COLUMN_EMAIL), gpa);
        if(insertSuccess){
            Toast.makeText(getApplicationContext(), "Password changed!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPA Changed successfully, Please login to continue")
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

            AlertDialog d = builder.create();
            d.setTitle("Success!");
            d.show();
        } else{
            Toast.makeText(getApplicationContext(), "GPA Chang failure!", Toast.LENGTH_SHORT).show();
        }
    }

}
