package com.animewatcher.animewatcher;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link submit_anime#newInstance} factory method to
 * create an instance of this fragment.
 */
public class submit_anime extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public submit_anime() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment submit_anime.
     */
    // TODO: Rename and change types and number of parameters
    public static submit_anime newInstance(String param1, String param2) {
        submit_anime fragment = new submit_anime();
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

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    RequestQueue requestQueue;
    String URL;

    EditText txt_username, txt_email, txt_anime, txt_body;
    Button btn_submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_submit_anime, container, false);

        txt_username = v.findViewById(R.id.txt_subAnime_username);
        txt_email = v.findViewById(R.id.txt_subAnime_email);
        txt_anime = v.findViewById(R.id.txt_subAnime_anime);
        txt_body = v.findViewById(R.id.txt_subAnime_body);
        btn_submit = v.findViewById(R.id.btn_subAnime);

        //If the user is logged in, prefill fields
        SharedPreferences pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.getString("Token", null) != null)
        {
            txt_username.setText(pref.getString("username", ""));
            txt_email.setText(pref.getString("email", ""));
        }

        btn_submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!txt_username.getText().toString().isEmpty() && !txt_email.getText().toString().isEmpty() && !txt_anime.getText().toString().isEmpty())
                        {
                            if(Patterns.EMAIL_ADDRESS.matcher(txt_email.getText().toString()).matches())
                            {
                                Date time = Calendar.getInstance().getTime();
                                String formTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(time);
                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                URL = getText(R.string.website_link) + "api/submit-anime";


                                if(!txt_body.getText().toString().isEmpty())
                                {
                                    String data = "{"+
                                            "\"mAnimeName\":" + "\"" + txt_anime.getText().toString() + "\","+
                                            "\"mUserEmail\":" + "\"" + txt_email.getText().toString() + "\","+
                                            "\"mUsername\":" + "\"" + txt_username.getText().toString() + "\","+
                                            "\"mMoreInfo\":" + "\"" + txt_body.getText().toString() + "\","+
                                            "\"mSubDate\":" + "\"" + date + "T" + formTime + "\""+
                                            "}";

                                    Submit(data);
                                }
                                else
                                {
                                    String data = "{"+
                                            "\"mAnimeName\":" + "\"" + txt_anime.getText().toString() + "\","+
                                            "\"mUserEmail\":" + "\"" + txt_email.getText().toString() + "\","+
                                            "\"mUsername\":" + "\"" + txt_username.getText().toString() + "\","+
                                            "\"mSubDate\":" + "\"" + date + "T" + formTime + "\""+
                                            "}";
                                    Submit(data);
                                }
                            }
                            else
                            {
                                Toast.makeText(getActivity(), R.string.subAnime_email_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(), R.string.subAnime_input_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Inflate the layout for this fragment
        return v;
    }

    private void Submit(String data)
    {
        final String savedata = data;

        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    if (objres.getString("Status").equals("error"))
                    {
                        Toast.makeText(getActivity(), objres.getString("Error"), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(objres.getString("Status").equals("Success"))
                        {
                            Toast.makeText(getActivity(), R.string.subAnime_success, Toast.LENGTH_SHORT).show();

                            txt_anime.setText("");
                            txt_body.setText("");
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return savedata == null ? null : savedata.getBytes(StandardCharsets.UTF_8);
            }

        };
        requestQueue.add(stringRequest);
    }
}