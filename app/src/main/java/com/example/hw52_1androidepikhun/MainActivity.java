package com.example.hw52_1androidepikhun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "user.txt";

    public static final int REQUEST_CODE_PERMISSION_WRITE_STORAGE = 11;

    private EditText editPersonName;
    private EditText editPassword;

    private HashMap<String, User> users = new HashMap<>();
    private HashSet<String> loginedUsers = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadUsers();
        InitViews();
    }


    private void loadUsers() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(openFileInput(FILE_NAME)));
            try {
                String readString;
                while ((readString = reader.readLine()) != null) {
                    String[] parts = readString.split(";");
                    users.put(parts[0], new User(parts[0], parts[1]));
                }
                Toast.makeText(MainActivity.this, getString(R.string.users_db_loaded), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loginUser(String userName, String password) {
        if (userName.length() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.enter_username), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!users.containsKey(userName)) {
            Toast.makeText(MainActivity.this, getString(R.string.user_is_not_exist), Toast.LENGTH_SHORT).show();
            return;
        }
        if (loginedUsers.contains(userName)) {
            Toast.makeText(MainActivity.this, getString(R.string.user_is_logined_already), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(Objects.requireNonNull(users.get(userName)).getPassword())) {
            Toast.makeText(MainActivity.this, getString(R.string.password_is_wrong), Toast.LENGTH_SHORT).show();
            return;
        }
        loginedUsers.add(userName);
        Toast.makeText(MainActivity.this, getString(R.string.user_is_logined), Toast.LENGTH_SHORT).show();
    }


    private void registerUser(String userName, String password) {
        if (userName.length() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.enter_register_usernamer), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if (users.containsKey(userName)) {
            Toast.makeText(MainActivity.this, getString(R.string.user_is_exist), Toast.LENGTH_SHORT).show();
            return;
        }
        users.put(userName, new User(userName, password));
        //сохраняем данные пользователей
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILE_NAME, MODE_PRIVATE)));
            try {
                for (HashMap.Entry<String, User> user : users.entrySet()) {
                    writer.write(String.format("%s;%s\n", user.getValue().getUserName(), user.getValue().getPassword()));
                }
                Toast.makeText(MainActivity.this, getString(R.string.new_user_is_registered), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void InitViews() {
        Button loginButton = findViewById(R.id.buttonLogin);
        Button registerButton = findViewById(R.id.buttonRegister);
        editPersonName = findViewById(R.id.editPersonName);
        editPassword = findViewById(R.id.editPassword);

        Button.OnClickListener onClickListener = new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                String userName = editPersonName.getText().toString();
                String password = editPassword.getText().toString();
                if (view.getId() == R.id.buttonLogin) loginUser(userName, password);
                if (view.getId() == R.id.buttonRegister) registerUser(userName, password);
            }
        };
        loginButton.setOnClickListener(onClickListener);
        registerButton.setOnClickListener(onClickListener);

    }
}