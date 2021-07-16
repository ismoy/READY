package cl.tofcompany.ready.Controllers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

import cl.tofcompany.ready.R;

public class MainActivity extends AppCompatActivity {

    //definamos los campos de textos
    EditText mEmail, mPassword;
    TextView mtxtforgot, name1, email;
    NavigationView nav_view;
    private ProgressDialog mDialog;
    ImageView mbtnlogi, mnuevacuenta, picture;
    //variable signIn on firebase
    FirebaseAuth fAuth;
    FirebaseUser mUser;
    //variable signIn on google
    SignInButton btngoogle;
    GoogleSignInClient googleSignInClient;

    public static final int RC_SIGN_IN = 0;
    //Variable singIn with facebook
    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //capturamos cada campos con sus respectivo id
        mnuevacuenta = findViewById(R.id.nuevacuenta);
        mEmail = findViewById(R.id.correo);
        mPassword = findViewById(R.id.password);
        mDialog = new ProgressDialog(this);
        mbtnlogi = findViewById(R.id.login);
        btngoogle = findViewById(R.id.btngoogle);
        mtxtforgot = findViewById(R.id.txtforgot);
        //Initialise Firebase
        fAuth = FirebaseAuth.getInstance();
        fAuth.getCurrentUser();
        //iniitialize fb sdk
        FacebookSdk.sdkInitialize(MainActivity.this);
        fAuth = FirebaseAuth.getInstance();

        mbtnlogi.setOnClickListener(v -> {
            String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError(getString(R.string.Email_is_required_));
                return;
            }
            if (!validaremail(email)) {
                mEmail.setError(getString(R.string.invalid_email));
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError(getString(R.string.Password_is_required_));
                return;
            }
            //Validamos el campo password para que el contraseña sea minimum 6 caracteres
            if (password.length() < 6) {
                mPassword.setError(getString(R.string.The_password_must_be_at_least_6_characters_long));
                return;
            }
            mDialog.setMessage(getString(R.string.Loading___));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

            //Iniciar session con FireBase
            fAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(task -> {
                //aseguramos que el processo sea exitoso y enviamos mensaje al usuario al contrario enviamos error
                if (task.isSuccessful()) {

                    //obligamos que el sistema requiere verificacion email para poder entrar
                    if (fAuth.getCurrentUser().isEmailVerified()) {

                        Toast.makeText(MainActivity.this , R.string.User_was_successfully_logged_in , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext() , HomeActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this , R.string.Account_not_verified_Please_check_youre_mail , Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }


                } else {
                    Toast.makeText(MainActivity.this , R.string.Problemwhenloggingincheckyouremailandpassword , Toast.LENGTH_LONG).show();
                    //en caso que hay problema para registrar desabilitamos el progresbar para no sigue cargando
                    mDialog.dismiss();
                }
            });
        });

        //Google SignIn
        btngoogle.setOnClickListener(v -> signInWithGoogle());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this , gso);

        //metodo para ir en la pagina de olvidar contraseña
        mtxtforgot.setOnClickListener(v -> startActivity(new Intent(getApplicationContext() , ForgotPassword.class)));

        mnuevacuenta.setOnClickListener(v -> startActivity(new Intent(getApplicationContext() , Register.class)));

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email" , "public_profile");
        loginButton.registerCallback(mCallbackManager , new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }


    //metodo para el acceso al token de facebook
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this , task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = fAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this , R.string.Authenticationfailed_ ,
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(MainActivity.this , HomeActivity.class);
            startActivity(intent);
        } else {

            Toast.makeText(this , R.string.Singintocontinue , Toast.LENGTH_SHORT).show();
        }
    }


    //Metodo para hacer accion del boton de google
    private void signInWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent , RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode , @Nullable Intent data) {
        //solo mcallback eso es para facebook
        mCallbackManager.onActivityResult(requestCode , resultCode , data);
        super.onActivityResult(requestCode , resultCode , data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this , R.string.Failedtoconnectwithgoogle , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken , null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this , task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(MainActivity.this , HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this , R.string.Loginfailed , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Validacion Patterns para el campo email
    private boolean validaremail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


}