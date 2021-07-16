package cl.tofcompany.ready.Controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Data;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import cl.tofcompany.ready.Model.ToDoModel;
import cl.tofcompany.ready.Notify.WorkManagerNotify;
import cl.tofcompany.ready.DialogCloseListner.OnDialogCloseListner;
import cl.tofcompany.ready.R;
import cl.tofcompany.ready.Utils.DataBaseHelper;
import dmax.dialog.SpotsDialog;

public class AddNewTask extends BottomSheetDialogFragment{

    public static final String TAG = "AddNewTask";

    //variable
    private EditText mEditText;
    //savetask btn
    private TextView mSaveButton;
    //imgbtn calendar y time
    private ImageButton btncalendar,btntime;
    //txtv date
     TextView tvdate;
    //txtv time
    private TextView tvtime;
    //Alertdialog
    AlertDialog mdialog;

    Calendar actual = Calendar.getInstance();
    Calendar calendar = Calendar.getInstance();

    private  int minute,time,day,month,year;

    private DataBaseHelper mydb;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_newtask , container , false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.edittext);
        mSaveButton = view.findViewById(R.id.button_save);
        btncalendar = view.findViewById(R.id.btncalendar);
        btntime = view.findViewById(R.id.btntime);
        tvdate = view.findViewById(R.id.tvdate);
        tvtime = view.findViewById(R.id.tvtime);

        mdialog = new  SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Loading...")
                .setCancelable(false)
                .build();

        mydb = new DataBaseHelper(getActivity());


        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            mEditText.setText(task);

            if (task.isEmpty()){
                mSaveButton.setEnabled(false);
            }


        }

        btncalendar.setOnClickListener(v -> {

            year = actual.get(Calendar.YEAR);
            month = actual.get(Calendar.MONTH);
            day = actual.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view1 , y , m , d) -> {
                calendar.set(Calendar.DAY_OF_MONTH,d);
                calendar.set(Calendar.MONTH,m);
                calendar.set(Calendar.YEAR,y);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = format.format(calendar.getTime());
                tvdate.setText(strDate);
            } ,year,month,day);
            datePickerDialog.show();
        });
        btntime.setOnClickListener(v -> {
            time = actual.get(Calendar.HOUR_OF_DAY);
            minute = actual.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext() , (view12 , h , m) -> {
                calendar.set(Calendar.HOUR_OF_DAY,h);
                calendar.set(Calendar.MINUTE,m);

                tvtime.setText(String.format("%02d:%02d",h,m));
            } ,time,minute,true);
            timePickerDialog.show();
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    mSaveButton.setEnabled(false);
                    mSaveButton.setBackgroundColor(Color.GRAY);
                }else{
                    mSaveButton.setEnabled(true);
                    mSaveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        final boolean finalIsUpdate = isUpdate;
        mSaveButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString();
            String date = tvdate.getText().toString();
            String time = tvtime.getText().toString();
            if (finalIsUpdate){
                mydb.updateTask(bundle.getInt("id") , text);
                mydb.updateDate(bundle.getInt("id"), date);
                mydb.updateTime(bundle.getInt("id"), time);
            }else{
                ToDoModel item = new ToDoModel();

                item.setDate(date);
                item.setTime(time);
                item.setTask(text);
                item.setStatus(0);
                mydb.insertTask(item);
                //ese es para la notification
                String tag = generateKey();
                Long AlertTime = calendar.getTimeInMillis() - System.currentTimeMillis();
                int random = (int)(Math.random() * 50 + 1);
                Data data = GuardarData("New Message",getString(R.string.Youhaveanewreminder),random);
                WorkManagerNotify.GuardarNotify(AlertTime,data,tag);
                Toast.makeText( getActivity().getApplicationContext(),R.string.Tasksave , Toast.LENGTH_LONG).show();
            }

            dismiss();

        });




    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListner){
            ((OnDialogCloseListner)activity).onDialogClose(dialog);
        }
    }

    private String generateKey(){
        return UUID.randomUUID().toString();
    }

    private Data GuardarData(String title, String titleDetail, int id_notify){
        return new Data.Builder()
                .putString("New Message",title)
                .putString("titleDetail",titleDetail)
                .putInt("id_notify",id_notify).build();
    }




}