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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;


public class Poisk extends AppCompatActivity {
    Connection connection;
    String ConnectionResult = "";
    ImageView image_poisk;
    String Poisk;
    String Img_poisk="";
    public final int[] i = {0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisk);
        configureBackButton();
    }

    private void configureBackButton() {
        Button back_poisk = (Button) findViewById(R.id.back_poisk);
        back_poisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

        public void GetTextFromSql(View a) {
            TextView UserID_poisk = findViewById(R.id.UserID_poisk);
            TextView FirstName_poisk = findViewById(R.id.FirstName_poisk);
            TextView LastName_poisk = findViewById(R.id.LastName_poisk);
            TextView login_user_poisk = findViewById(R.id.login_user_poisk);
            TextView password_user_poisk = findViewById(R.id.password_user_poisk);
            ImageView imageView_poisk = findViewById(R.id.image_poisk);
            TextView poisk=findViewById(R.id.Stroka_Poiska);
            Poisk=poisk.getText().toString();
            try {
                ConectionHellper conectionHellper = new ConectionHellper();
                connection = conectionHellper.connectionClass();

                if (connection != null) {

                    String query3 = "select count(id) from Users where FirstName LIKE '"+Poisk+"%'";
                    Statement statement3 = connection.createStatement();
                    ResultSet resultSet3= statement3.executeQuery(query3);
                    int c = 0;
                    while (resultSet3.next()) {
                        c = resultSet3.getInt(1);
                    }
                    if (i[0] != 1) {
                        i[0] = i[0] - 1;
                    }
                    String query100 = "select id from Users where FirstName LIKE '"+Poisk+"%'";
                    Statement statement100 = connection.createStatement();
                    ResultSet resultSet100 = statement100.executeQuery(query100);
                    int[] index= new int[c];
                    int b=0;
                    while (resultSet100.next()) {
                        index[b] = resultSet100.getInt(1);
                        b++;
                    }
                    b=0;
                    String query4 = "Select * From Users where id=" + index[b] + "";
                    Statement statement4 = connection.createStatement();
                    ResultSet resultSet4 = statement4.executeQuery(query4);
                    while (resultSet4.next()) {
                        UserID_poisk.setText(resultSet4.getString(1));
                        FirstName_poisk.setText(resultSet4.getString(2));
                        LastName_poisk.setText(resultSet4.getString(3));
                        login_user_poisk.setText(resultSet4.getString(4));
                        password_user_poisk.setText(resultSet4.getString(5));
                        Img_poisk = (resultSet4.getString(6));
                        imageView_poisk.setImageBitmap(getImgBitmap(Img_poisk));
                    }
                } else {
                    ConnectionResult = "Check Connection";
                }
            } catch (Exception ex) {

            }

        }

        private Bitmap getImgBitmap(String encodedImg) {
            if (encodedImg != null && !encodedImg.equals("null")) {
                byte[] bytes = new byte[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bytes = Base64.getDecoder().decode(encodedImg);
                }
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            return BitmapFactory.decodeResource(com.example.zlobinov_data.Poisk.this.getResources(),
                    R.drawable.no_photo);
        }
        public void onClickChooseImage(View view)
        {
            getImage();

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==1 && data!= null && data.getData()!= null)
            {
                if(resultCode==RESULT_OK)
                {
                    Log.d("MyLog","Image URI : "+data.getData());
                    image_poisk.setImageURI(data.getData());
                    Bitmap bitmap = ((BitmapDrawable)image_poisk.getDrawable()).getBitmap();
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
                Img_poisk=Base64.getEncoder().encodeToString(bytes);
                return Img_poisk;
            }
            return "";
        }
}