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
import java.util.Arrays;
import java.util.Random;

public class CcsActivity extends Activity {

    RelativeLayout interactiveImg;
    LinearLayout clickableArea1,clickableArea2,clickableArea3,clickableArea4;
    int firstImgClick,secondImgClick,thirdImgClick;
    ArrayList<Integer> clicks = new ArrayList<Integer>();
    private Bundle extras;
    private JSONArray selectedImages,selectedClicks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ccs);

        //Get user input data
        extras = getIntent().getExtras();
        try {
            JSONObject gpa = new JSONObject(extras.getString(DBHelper.USERS_COLUMN_GPA));
            selectedImages = gpa.getJSONArray("selectedImages");
            selectedClicks = gpa.getJSONArray("selectedClicks");

            //Set any random image as background
            interactiveImg = findViewById(R.id.interactive_img);
            interactiveImg.setBackground(GetImage(getApplicationContext(),"ccs_"+selectedImages.getInt(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Set areas as clickable
        clickableArea1 = findViewById(R.id.clickable_area1);
        clickableArea2 = findViewById(R.id.clickable_area2);
        clickableArea3 = findViewById(R.id.clickable_area3);
        clickableArea4 = findViewById(R.id.clickable_area4);

        clickableArea1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processClick(1);
            }
        });
        clickableArea2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processClick(2);
            }
        });
        clickableArea3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processClick(3);
            }
        });
        clickableArea4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processClick(4);
            }
        });
    }

    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }

    public void processClick(int clickArea) {
        try {
            clicks.add(clickArea);
            if (clicks.size() == 3) {
                if (clicks.get(0) == selectedClicks.getInt(0) && clicks.get(1) == selectedClicks.getInt(1) && clicks.get(2) == selectedClicks.getInt(2)) {
                    Toast.makeText(getApplicationContext(), "User successfully authenticated!", Toast.LENGTH_SHORT).show();
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString(DBHelper.USERS_COLUMN_EMAIL, extras.getString(DBHelper.USERS_COLUMN_EMAIL));
                    Intent intent = new Intent(getApplicationContext(), LoggedinActivity.class);
                    intent.putExtras(dataBundle);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Graphical Password Authentication failed, Please try again!")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                }
                            });

                    AlertDialog d = builder.create();
                    d.setTitle("Failure!");
                    d.show();
                }
            } else {
                if (clicks.size() == 1) {
                    interactiveImg.setBackground(GetImage(getApplicationContext(), "ccs_" + selectedImages.getInt(1)));
                } else if (clicks.size() == 2) {
                    interactiveImg.setBackground(GetImage(getApplicationContext(), "ccs_" + selectedImages.getInt(2)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
