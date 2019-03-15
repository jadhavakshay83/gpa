package in.hatcube.gpa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class PvsActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    Button chooseImg,submit;
    ImageView choosenImg;
    Uri selectedImgUri;
    Bitmap selectedImg;
    private String authImage ;
    private Bundle extras;
    public static String TAG = "UploadActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvs);

        //Get user input data
        extras = getIntent().getExtras();
        try {
            JSONObject gpa = new JSONObject(extras.getString(DBHelper.USERS_COLUMN_GPA));
            authImage = gpa.getString("selectedImage");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        submit = findViewById(R.id.pvc_login);
        chooseImg = findViewById(R.id.chooseImg);
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Choose Image"), PICK_IMAGE_REQUEST);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImgUri == null) {
                    Toast.makeText(getApplicationContext(), "Choose image to proceed", Toast.LENGTH_SHORT).show();
                } else {
                    saveData();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            selectedImgUri = data.getData();

            try {
                selectedImg = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImgUri);
                // Log.d(TAG, String.valueOf(bitmap));

                choosenImg = findViewById(R.id.choosenImg);
                choosenImg.setImageBitmap(selectedImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void saveData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImg.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        if(encodedImage.equals(authImage)){
            Toast.makeText(getApplicationContext(), "User successfully authenticated!", Toast.LENGTH_SHORT).show();
            Bundle dataBundle = new Bundle();
            dataBundle.putString(DBHelper.USERS_COLUMN_EMAIL, extras.getString(DBHelper.USERS_COLUMN_EMAIL));
            Intent intent = new Intent(getApplicationContext(), LoggedinActivity.class);
            intent.putExtras(dataBundle);
            startActivity(intent);
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Graphical Password Authentication failed, Please try again!")
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

            AlertDialog d = builder.create();
            d.setTitle("Failure!");
            d.show();
        }
    }
}
