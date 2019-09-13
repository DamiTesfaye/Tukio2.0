package com.exceptos.tukio.View.Fragments.AuthFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.exceptos.tukio.Listeners.OnAuthLevelClicked;
import com.exceptos.tukio.Utils.AppUtils;
import com.exceptos.tukio.Listeners.OnAuthLevelClicked;
import com.exceptos.tukio.R;
import com.exceptos.tukio.Utils.AppUtils;
import kotlin.text.Regex;
import org.jetbrains.annotations.NotNull;


public class PasswordRecoveryFragment extends Fragment {


    public PasswordRecoveryFragment() {
        // Required empty public constructor
    }
    private RelativeLayout submitButtonContainer;
    private TextView backToSignIn;
    private TextInputEditText emailEditText;
    private TextInputLayout emailEditTextContainer;
    private ProgressBar progressBar;
    private ImageView recoverySuccessImage;
    private TextView submitText;
    private TextView retrievalSuccess;
    private OnAuthLevelClicked onAuthLevelClicked;

    private static final String EMAILRE = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_recovery, container, false);
        submitButtonContainer = view.findViewById(R.id.submit_button_container);
        backToSignIn = view.findViewById(R.id.back_sign_in);
        emailEditText = view.findViewById(R.id.forgot_password_editText);
        emailEditTextContainer = view.findViewById(R.id.forgot_password_editText_container);
        progressBar = view.findViewById(R.id.submit_button_progressBar);
        recoverySuccessImage = view.findViewById(R.id.submit_button_image);
        submitText = view.findViewById(R.id.submit_button_text);
        firebaseAuth = FirebaseAuth.getInstance();
        retrievalSuccess = view.findViewById(R.id.retrieval_complete_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInIntent();
            }
        });

        submitButtonContainer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                emailRecovery();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            onAuthLevelClicked = (OnAuthLevelClicked) context;
        }catch (ClassCastException exception){
            throw new RuntimeException("");
        }
    }

    private void emailRecovery(){

        if (getActivity() != null && getContext() != null  ){

            if (!emailEditText.getText().toString().trim().equals("")){
                Regex regext = new Regex(EMAILRE);
                String emailString = emailEditText.getText().toString().trim();
                if (regext.matches(emailString)){
                    submitText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    submitButtonContainer.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.buttonbg_outline));
                    firebaseAuth.sendPasswordResetEmail(emailString)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull final Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        recoverySuccessImage.setVisibility(View.VISIBLE);
                                        submitButtonContainer.setBackground(ContextCompat.getDrawable(PasswordRecoveryFragment.this.getContext(), R.drawable.buttonbg_outline));
                                        retrievalSuccess.setVisibility(View.VISIBLE);

                                    } else {

                                        if (task.getException().getLocalizedMessage() != null){
                                            View vieW = getActivity().findViewById(android.R.id.content);
                                            AppUtils.Companion.getCustomSnackBar(vieW, task.getException().getLocalizedMessage(), getContext()).show();

                                            recoverySuccessImage.setVisibility(View.GONE);
                                            submitText.setVisibility(View.VISIBLE);
                                            submitButtonContainer.setBackground(ContextCompat.getDrawable(PasswordRecoveryFragment.this.getContext(), R.drawable.buttonbg));
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });
                }else{
                    emailEditTextContainer.setErrorEnabled(true);
                    emailEditTextContainer.setError("Please input a valid email");

                }
            }else{
                emailEditTextContainer.setErrorEnabled(true);
                emailEditTextContainer.setError("Please input an email");
            }
        }

    }


    private void signInIntent(){
       onAuthLevelClicked.goSignIn();
    }
}
