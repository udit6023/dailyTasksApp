package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.example.todoapp.Adapter.ToDoAdapter;
import com.example.todoapp.Model.base;
import com.example.todoapp.Utils.DataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListner {

    public RecyclerView recyclerView;
    FloatingActionButton fab;
    DataBaseHelper mydb;
    public List<base> mList;
    public ToDoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerView);
        fab=findViewById(R.id.fab);
        mydb=new DataBaseHelper(MainActivity.this);
        mList=new ArrayList<>();
        adapter=new ToDoAdapter(mydb,MainActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mList=mydb.getAllTask();
        Collections.reverse(mList);
        adapter.setTasks(mList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask.newInstance().show(getSupportFragmentManager(),addNewTask.TAG);
            }
        });

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDialogClose(DialogInterface dialog) {
        mList=mydb.getAllTask();
        //so here we are making our task to  display at the top
        Collections.reverse(mList);
        adapter.setTasks(mList);
        adapter.notifyDataSetChanged();
    }
    //so this ItemTouchHelper will help us in delete or edit our task with a swipe.
   ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
       @Override
       public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
           return false;
       }

       @Override
       public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


           final int position=viewHolder.getAdapterPosition();
           if(direction==ItemTouchHelper.RIGHT)
           {
               AlertDialog.Builder builder=new AlertDialog.Builder(adapter.getContext());
               builder.setTitle("Delete Task");
               builder.setMessage("Are You Sure ?");
               builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       adapter.deletTask(position);
                   }
               });
               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       adapter.notifyItemChanged(position);
                   }
               });
               AlertDialog dialog=builder.create();
               dialog.show();
           }else
               adapter.editTask(position);
       }

       @Override
       public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext() , R.color.design_default_color_primary_dark))
//                .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
//                .addSwipeRightBackgroundColor(Color.RED)
//                .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
//                .create()
//                .decorate();


           final int DIRECTION_RIGHT=1;
           final int DIRECTION_LEFT=0;

           if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive)
           {
               int direction=dX>0 ? DIRECTION_RIGHT : DIRECTION_LEFT;
               int absoluteDisplacement=Math.abs((int)dX);

               switch(direction)
               {
                   case DIRECTION_RIGHT:
                       //draw background
                       View itemView=viewHolder.itemView;
                       ColorDrawable bg=new ColorDrawable();
                       bg.setColor(Color.RED);
                       bg.setBounds(itemView.getLeft(),itemView.getTop(),itemView.getRight(),itemView.getBottom());
                       bg.draw(c);

                       //draw an icon
                       Drawable icon= ActivityCompat.getDrawable(MainActivity.this,R.drawable.ic_baseline_delete_24);
                       int top=((itemView.getHeight())-(icon.getIntrinsicHeight()))/2;//+itemView.getTop();
                       //icon.setBounds(0,top,icon.getIntrinsicHeight(),top+icon.getIntrinsicHeight());
                       icon.setBounds(itemView.getLeft()+top,itemView.getTop()+top,itemView.getLeft()+top+icon.getIntrinsicHeight(),itemView.getBottom()-top);

                       icon.draw(c);

                       break;

                   case DIRECTION_LEFT:
                       //draw background
                       View itemView1=viewHolder.itemView;
                       ColorDrawable bg1=new ColorDrawable();
                       bg1.setColor(Color.BLACK);
                       bg1.setBounds(itemView1.getLeft(),itemView1.getTop(),itemView1.getRight(),itemView1.getBottom());
                       bg1.draw(c);

                       //draw an icon
                       Drawable icon1= ActivityCompat.getDrawable(MainActivity.this,R.drawable.ic_baseline_edit_24);
                       int top1=((itemView1.getHeight())-(icon1.getIntrinsicHeight()))/2;//+itemView1.getTop();
                       icon1.setBounds(itemView1.getRight()-top1-icon1.getIntrinsicHeight(),itemView1.getTop()+top1,itemView1.getRight()-top1,itemView1.getBottom()-top1);
                       icon1.draw(c);

                       break;
               }

           }

           super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
       }
   };
}