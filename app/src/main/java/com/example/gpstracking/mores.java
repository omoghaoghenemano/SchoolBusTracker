package com.example.gpstracking;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mores#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mores extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
private CoordinatorLayout bottomsheetlayout;
    private BottomSheetBehavior sheetBehavior;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Toolbar userdetails;
    FirebaseUser firebaseUser;
    LinearLayout logout;

    FirebaseDatabase myaccount;
    DatabaseReference myuser;
    FirebaseAuth firebaseAuth;
private TextView myname;
    public mores() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mores.
     */
    // TODO: Rename and change types and number of parameters
    public static mores newInstance(String param1, String param2) {
        mores fragment = new mores();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View view =  inflater.inflate(R.layout.fragment_mores, container, false);
        userdetails = view.findViewById(R.id.userdetails);
        myname = view.findViewById(R.id.myname);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myaccount = FirebaseDatabase.getInstance();
        myuser = FirebaseDatabase.getInstance().getReference("User");
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();


            }
        });

LoadInformation();

        userdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "you clicked on me", Toast.LENGTH_SHORT).show();
            }
        });


       return view;
    }
    public void LoadInformation(){
        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        myname.setText("Hello,"+"\t"+email);

    }
}