package com.example.todoapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todoapp.Model.base;
import com.example.todoapp.Utils.DataBaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class addNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";
    //widgets
    private EditText mEditText;
    private Button mSaveButton;

    private DataBaseHelper myDB;

    public static addNewTask newInstance(){
        return new addNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View v=inflater.inflate(R.layout.add_newtask,container,false);
         return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText=view.findViewById(R.id.edittext);
        mSaveButton=view.findViewById(R.id.button);

        myDB=new DataBaseHelper(getActivity());

        //so this will check if the user want to update the data or wanted to add a task.
        boolean isUpdate=false;

        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            isUpdate=true;
            String task=bundle.getString("task");
            mEditText.setText(task);

            if(task.length()==0)
            {
                mSaveButton.setEnabled(false);
            }
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals("")){
                    mSaveButton.setEnabled(false);
                    mSaveButton.setBackgroundColor(Color.parseColor("#808080"));

                }else{
                    mSaveButton.setEnabled(true);
                    mSaveButton.setBackgroundColor(getResources().getColor(R.color.teal_green));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    final boolean finalIsUpdate=isUpdate;
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=mEditText.getText().toString();

                if(finalIsUpdate){
                    myDB.updateTask(bundle.getInt("id"),text);

                }else{
                    base item=new base();
                    item.setTask(text);
                    item.setCheckBox(0);
                    myDB.insertTask(item);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity =getActivity();
        if(activity instanceof OnDialogCloseListner){
            ((OnDialogCloseListner)activity).onDialogClose(dialog);
        }
    }
}
