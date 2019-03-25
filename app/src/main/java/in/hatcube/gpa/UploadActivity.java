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

public class UploadActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    Button chooseImg,submit;
    ImageView choosenImg;
    Uri selectedImgUri;
    Bitmap selectedImg;
    private DBHelper mydb ;
    private Bundle extras;
    public static String TAG = "UploadActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //Initialize database
        mydb = new DBHelper(this);

        //Get user input data
        extras = getIntent().getExtras();

        submit = findViewById(R.id.pvc_logup);
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

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = new FileInputStream(sourceFile).getChannel();
        FileChannel destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }

    private String getRealPathFromURI(Uri contentURI)
    {
        String result = null;

        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentURI, proj, null, null, null);

        if (cursor == null)
        { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        }
        else
        {
            if(cursor.moveToFirst())
            {
                Log.d(TAG,"Look below");
                Log.d(TAG,cursor.toString());
                int idx = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }

    public void saveData() {

        //Insert data to database
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImg.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        JSONObject json = new JSONObject();
        try {
            json.put("selectedImage", encodedImage);
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
        Boolean insertSuccess = mydb.insertUser(extras.getString(DBHelper.USERS_COLUMN_NAME), extras.getString(DBHelper.USERS_COLUMN_PHONE), extras.getString(DBHelper.USERS_COLUMN_EMAIL), extras.getString(DBHelper.USERS_COLUMN_PASSWORD),Constants.PVS,gpa);
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
