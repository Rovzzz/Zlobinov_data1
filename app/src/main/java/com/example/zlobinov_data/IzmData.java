package com.example.zlobinov_data;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class IzmData extends AppCompatActivity {
    Connection connection;
    String ConnectionResult = "";
    private ImageView imageButton;
    TextView FirstNameIzm;
    TextView LastNameIzm;
    TextView login_userIzm;
    TextView password_userIzm;
    ImageView imageView;
    String Img="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izm_data);
        imageButton=findViewById(R.id.imageIzm);

        FirstNameIzm = findViewById(R.id.FirstNameIzm);
        LastNameIzm = findViewById(R.id.LastNameIzm);
        login_userIzm = findViewById(R.id.login_userIzm);
        password_userIzm = findViewById(R.id.password_userIzm);
        imageView=findViewById(R.id.imageIzm);
        Intent intent = getIntent();
        String FirstName = intent.getStringExtra("FirstName");
        String LastName=intent.getStringExtra("LastName");
        String login_user=intent.getStringExtra("login_user");
        String password_user=intent.getStringExtra("password_user");
        String Image=intent.getStringExtra("Image");
        FirstNameIzm.setText(FirstName);
        LastNameIzm.setText(LastName);
        login_userIzm.setText(login_user);
        password_userIzm.setText(password_user);
        imageView.setImageBitmap(getImgBitmap(Image));
    }
    public void onClickChooseImage(View view)
    {
        getImage();

    }
    private Bitmap getImgBitmap(String encodedImg) {
        if (encodedImg != null && !encodedImg.equals("null")) {
            byte[] bytes = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bytes = Base64.getDecoder().decode(encodedImg);
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return BitmapFactory.decodeResource(IzmData.this.getResources(),
                R.drawable.no_photo);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data!= null && data.getData()!= null)
        {
            if(resultCode==RESULT_OK)
            {
                Log.d("MyLog","Image URI : "+data.getData());
                imageButton.setImageURI(data.getData());
                Bitmap bitmap = ((BitmapDrawable)imageButton.getDrawable()).getBitmap();
                encodeImage(bitmap);

            }
        }
    }

    private void getImage()
    {
        Intent intentChooser= new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentChooser,1);
    }

    private String encodeImage(Bitmap bitmap) {
        int prevW = 150;
        int prevH = bitmap.getHeight() * prevW / bitmap.getWidth();
        Bitmap b = Bitmap.createScaledBitmap(bitmap, prevW, prevH, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Img= Base64.getEncoder().encodeToString(bytes);
            return Img;
        }
        return "";
    }
    private void configureBackButton() {
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void IzmTextFromSql(View v) {

        String FirstName = FirstNameIzm.getText().toString();
        String LastName = LastNameIzm.getText().toString();
        String login_user = login_userIzm.getText().toString();
        String password_user = password_userIzm.getText().toString();
        try {
            ConectionHellper conectionHellper = new ConectionHellper();
            connection = conectionHellper.connectionClass();
            Intent intent = getIntent();
            String Users = intent.getStringExtra("Users");
            if (connection != null) {
                String query11 = "select id from Users where FirstName = '" + FirstName + "'";
                Statement statement11 = connection.createStatement();
                ResultSet resultSet11 = statement11.executeQuery(query11);
                int i = 0;
                while (resultSet11.next())
                {
                    i=resultSet11.getInt(1);
                }
                String query12 = "update Users set FirstName = '" + FirstName + "', LastName ='" + LastName + "',login_user =" + login_user + "',password_user ='" + password_user + ", Image = '"+ Img +"' where id = "+i+"";
                Statement statement12 = connection.createStatement();
                statement12.execute(query12);
                finish();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Данные успешно изменены, обновите их", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                ConnectionResult = "Check Connection";
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}