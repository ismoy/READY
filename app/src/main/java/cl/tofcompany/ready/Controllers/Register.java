package cl.tofcompany.ready.Controllers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

import cl.tofcompany.ready.Model.Usuario;
import cl.tofcompany.ready.R;

public class Register extends AppCompatActivity {

    //definamos cada campon con sus nombres
    EditText musername, mEmail, mPassword, mrepetpassword;
    ImageView mRegisterBtn, mLoginBtn;
    private ProgressDialog mDialog;
    FirebaseAuth fAuth;
    DatabaseReference databaseUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //asigñamos cada campos con sus respectivos id
        musername = findViewById(R.id.username);
        mEmail = findViewById(R.id.correo);
        mPassword = findViewById(R.id.password);
        mrepetpassword = findViewById(R.id.repeatpassword);
        mRegisterBtn = findViewById(R.id.crearcuenta);
        mDialog = new ProgressDialog(this);
        mLoginBtn = findViewById(R.id.iniciar);
        databaseUsuario = FirebaseDatabase.getInstance().getReference("usuario");
        fAuth = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(v -> {
            //definimos los variables de cada campo para poder validarlos
            String name = musername.getText().toString().trim();
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String password2 = mrepetpassword.getText().toString().trim();


            //Validamos el campo nombre para que no esta vacio
            if (TextUtils.isEmpty(name)) {
                musername.setError(getString(R.string.Requiredfield));
                return;
            }
            //Validamos que solo pueden igresar letra
            if (!validarletras(name)) {
                musername.setError(getString(R.string.Onlyallowletters));
                return;
            }
            //Validamos que el campo nombre debe tener minimo 6 caracteres
            if (name.length() <= 6) {
                musername.setError(getString(R.string.Pleaseenteryourfullname));
                return;
            }

            //Validamos el campo email para que no esta vacio
            if (TextUtils.isEmpty(email)) {
                mEmail.setError(getString(R.string.Email_is_required_));
                return;
            }
            if (!validaremail(email)) {
                mEmail.setError(getString(R.string.invalid_email));
                return;
            }
            //Validamos el campo password para que no esta vacio
            if (TextUtils.isEmpty(password)) {
                mPassword.setError(getString(R.string.Password_is_required_));
                return;
            }
            //Validamos el campo confirmar password para que no esta vacio
            if (TextUtils.isEmpty(password2)) {
                mrepetpassword.setError(getString(R.string.Password_is_required_));
                return;
            }
            //Validamos el campo password para que el contraseña sea minimum 6 caracteres
            if (password.length() < 6) {
                mPassword.setError(getString(R.string.The_password_must_be_at_least_6_characters_long));
                return;
            }
            if (password.equals(password2)) {
                //despues de validar los campos enviamos los datos en la realtime database de una vez
                Usuario();
            } else {
                mrepetpassword.setError(getString(R.string.Passwordsdonotmatch));
                return;
            }


            //despues que todos sea ok al dar click en registrar mostrara el progressBar
            mDialog.setMessage(getString(R.string.Loading___));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            // progressBar.setVisibility(View.VISIBLE);

            // registrar el usuario en firebase

            fAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                private String TAG;

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //aseguramos que el processo sea exitoso y enviamos mensaje al usuario al contrario enviamos error
                    if (task.isSuccessful()) {
                        //enviamos un correo de confirmacion al usuario para verificar su email

                        FirebaseUser user = fAuth.getCurrentUser();
                        fAuth.setLanguageCode("es");
                        assert user != null;
                        user.sendEmailVerification().addOnCompleteListener(task1 -> Toast.makeText(Register.this , R.string.Youraccounthasbeencreatedsuccessfully_Checkyouremailtoactivatetheaccount , Toast.LENGTH_LONG).show()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d(TAG , R.string.onFailureWecouldnotverifyyouremail + e.getMessage());
                            }

                        });

                    } else {
                        Toast.makeText(Register.this , R.string.Error + Objects.requireNonNull(task.getException()).getMessage() , Toast.LENGTH_LONG).show();
                        //en caso que hay problema para registrar desabilitamos el progresbar para no sigue cargando
                        //progressBar.setVisibility(View.GONE);

                        mDialog.dismiss();
                    }

                }

            });
            //despues de registrar el usuario no enviara al pantalla de login para entrar y asegurar que la cuenta esta verificada
            volveralinicio(v);
        });


    }

    //metodo para guardar automaticamente en realtime database
    public void Usuario() {
        String username = musername.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String password2 = mrepetpassword.getText().toString();
        String id = databaseUsuario.push().getKey();
        Usuario usuario = new Usuario(id , username , email , password , password2);
        assert id != null;
        databaseUsuario.child(id).setValue(usuario);
    }

    //metodo para ir al pantalla de login
    public void login(View v) {
        Intent i = new Intent(this , MainActivity.class);
        startActivity(i);

    }

    public void volveralinicio(View v) {
        Intent i = new Intent(this , MainActivity.class);
        startActivity(i);
    }

    //Validacion regex para los campos
    public static boolean validarletras(String datos) {
        return datos.matches("[a-zA-Z ]*");
    }

    //Validacion Patterns para el campo email
    private boolean validaremail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}







