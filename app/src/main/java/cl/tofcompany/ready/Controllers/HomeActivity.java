package cl.tofcompany.ready.Controllers;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cl.tofcompany.ready.Adapter.ToDoAdapter;
import cl.tofcompany.ready.Model.ToDoModel;
import cl.tofcompany.ready.DialogCloseListner.OnDialogCloseListner;
import cl.tofcompany.ready.R;
import cl.tofcompany.ready.RecyclerTouchHelper.RecyclerViewTouchHelper;
import cl.tofcompany.ready.Utils.DataBaseHelper;

public class HomeActivity extends AppCompatActivity implements OnDialogCloseListner {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView nav_view;
    private ImageView picture;
    private TextView username, email;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mreference;
    private RecyclerView mrecyclerView;
    private DataBaseHelper mydb;
    private List<ToDoModel> mList;
    private ToDoAdapter adapter;
    private AppBarConfiguration mAppBarConfiguration;

    //todos esos es para el boton flotante animado
    FloatingActionButton fab, fab1, fab2;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        setTitle(R.string.tasks);
        //Initialise Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mreference = FirebaseDatabase.getInstance().getReference().child("usuario");
        //fab buton flotante
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        //para las animaciones
        rotateForward = AnimationUtils.loadAnimation(this , R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this , R.anim.rotate_backward);
        fabOpen = AnimationUtils.loadAnimation(this , R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this , R.anim.fab_close);
        nav_view = findViewById(R.id.navigation);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open , R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View view = nav_view.inflateHeaderView(R.layout.main_header);
        picture = view.findViewById(R.id.picture);
        username = view.findViewById(R.id.name1);
        email = view.findViewById(R.id.email);
        updateUi();




        nav_view.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (item.getItemId()) {
                case R.id.mhome:
                    setTitle(R.string.tasks);
                    break;

                case R.id.profile:
                 Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                 startActivity(intent);
                    setTitle(R.string.profile);
                 break;
                case R.id.share:
                    shareapp();
                    setTitle(R.string.share);
                    break;

                case R.id.feedback:
                   startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));
                    setTitle(R.string.feedback);
                    break;

                case R.id.logout:
                    Alertsignout();
                    setTitle(R.string.logout);
                    break;
            }
            return false;
        });


        //Acciones de las task
        mrecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mydb = (DataBaseHelper) new DataBaseHelper(HomeActivity.this);
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(mydb , HomeActivity.this);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerView.setAdapter(adapter);
        mList = mydb.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);

        //si el usuario apreta 1 sola vez en el boton se desplegara el teclado para escribir task
        fab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(),AddNewTask.TAG));



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(mrecyclerView);
        //si el usuario deja presionado el boton se dsplegara las opciones disponibles
        fab.setOnLongClickListener(v -> {
            animateFab();
            return true;
        });
        fab1.setOnClickListener(v -> {
            animateFab();
            Toast.makeText(HomeActivity.this , R.string.Soon , Toast.LENGTH_SHORT).show();
        });

        fab2.setOnClickListener(v -> {
            animateFab();
           startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));
        });

        picture.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this,ProfileActivity.class)));

    }


    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList = mydb.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);
        adapter.notifyDataSetChanged();

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

        }

    }

    //metodo para cerrar session con alert
    public void Alertsignout() {
        AlertDialog.Builder alertDialog2 = new
                AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog2.setTitle(R.string.confirm_signout);

        // Setting Dialog Message
        alertDialog2.setMessage(R.string.text_confirm);

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton(R.string.yes,
                (dialog , which) -> {
                    // Write your code here to execute after dialog
                    mAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    finish();
                    Intent i = new Intent(getApplicationContext() ,
                            MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                });

        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton(R.string.no ,
                (dialog , which) -> {
                    // Write your code here to execute after dialog
                    Toast.makeText(getApplicationContext() ,
                            R.string.noaction, Toast.LENGTH_SHORT)
                            .show();
                    dialog.cancel();
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                });

        // Showing Alert Dialog
        alertDialog2.show();


    }

    //animation para el boton flotante
    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isOpen = false;
        } else {
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isOpen = true;
        }
    }

    //metodo para compartir mi app
    public void shareapp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareTitle = ("https://play.google/store/apps/details?id=c0m.ready\n" +
                "Free download");
        String shareBody = ("");
        intent.putExtra(Intent.EXTRA_TEXT , shareTitle);
        intent.putExtra(Intent.EXTRA_SUBJECT , shareBody);
        startActivity(Intent.createChooser(intent , "Share"));
    }

    public void updateUi() {
        if (mUser != null) {
            username.setText(mUser.getDisplayName());
            email.setText(mUser.getEmail());
            if (mUser.getPhotoUrl() != null) {
                String mypicture = mUser.getPhotoUrl().toString();
                mypicture = mypicture + "?type=large";
                Picasso.get().load(mypicture).into(picture);
            }

        } else {
            username.setText("");
            email.setText("");
            picture.setImageResource(R.drawable.logodefault);

        }
    }


}