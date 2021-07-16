package cl.tofcompany.ready.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import cl.tofcompany.ready.Controllers.AddNewTask;
import cl.tofcompany.ready.Controllers.HomeActivity;
import cl.tofcompany.ready.Model.ToDoModel;
import cl.tofcompany.ready.R;
import cl.tofcompany.ready.Utils.DataBaseHelper;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> mList;
    private HomeActivity activity;
    private DataBaseHelper mydb;


    public ToDoAdapter(DataBaseHelper mydb , HomeActivity activity) {
        this.activity = activity;
        this.mydb = mydb;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout , parent , false);
        return new MyViewHolder(v);


    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder , int position) {

        final ToDoModel item = mList.get(position);
        holder.mCheckBox.setText(item.getTask());
        holder.tvdate.setText(item.getDate());
        holder.tvtime.setText(item.getTime());
        holder.mCheckBox.setChecked(toBoolean(item.getStatus()));
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView , boolean isChecked) {

                if (isChecked) {

                    mydb.updateStatus(item.getId() , 1);
                    //decidimos que al completar la tarea que se borra para no guardar tareas antiguas

                    deleteTask(position , buttonView);

                } else {
                    mydb.updateStatus(item.getId() , 0);
                }

            }

        });
    }

    public boolean toBoolean(int num) {
        return num != 0;
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position , View v) {
        ToDoModel item = mList.get(position);
        mydb.deleteTask(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
        //para snackbar undo
        Snackbar.make(v , R.string.Taskcompleted , Snackbar.LENGTH_LONG)
                .setDuration(3000)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAction(R.string.UNDO , new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mList.add(position , item);
                        notifyItemInserted(position);
                        notifyItemChanged(position , mList.size());
                    }
                })
                .show();

    }

    public void achivedItem(int position) {
        ToDoModel item = mList.get(position);
        mydb.archiveTask(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(activity , R.string.TaskArchived , Toast.LENGTH_SHORT).show();

    }

    public void editItem(int position) {
        ToDoModel item = mList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id" , item.getId());
        bundle.putString("task" , item.getTask());
        bundle.putString("date" , item.getDate());
        bundle.putString("time" , item.getTime());


        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager() , task.getTag());

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox mCheckBox;
        TextView tvdate, tvtime;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            tvdate = itemView.findViewById(R.id.date1);
            tvtime = itemView.findViewById(R.id.time1);


        }
    }
}
