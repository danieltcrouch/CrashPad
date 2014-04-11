package com.application.crashpad;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment
{
	private static final String LOGIN_URL = "http://taz.harding.edu/~dcrouch1/crashpad/login.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
	
	private EditText mUsernameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private Button mCreateNewAccountButton;
	private ProgressDialog pDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_login, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }
		
		mUsernameEditText = (EditText)view.findViewById(R.id.username);
		mPasswordEditText = (EditText)view.findViewById(R.id.password);
		
		mLoginButton = (Button)view.findViewById(R.id.login_button);
		mLoginButton.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				new AttemptLogin().execute();
			}
		});
		
		mCreateNewAccountButton = (Button)view.findViewById(R.id.create_account_button);
		mCreateNewAccountButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(getActivity(), CreateAccountActivity.class);
				startActivity(i);
			}
		});
		
		return view;
	}
	
	class AttemptLogin extends AsyncTask<String, String, String>
	{
		boolean failure = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            //FIX
            //Fix all strings in Program
            pDialog.setMessage("Attempting Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... args)
		{			
            int success;
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            
            try
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

            	JSONParser jParser = new JSONParser();
                JSONObject json = jParser.makeHttpRequest(LOGIN_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                	AccountCurrent.get(getActivity()).setPresentAccount(new Account(username, password));
    				Intent i = new Intent(getActivity(), HomeActivity.class);
                    startActivity(i);
                    
                	return json.getString(TAG_MESSAGE);
                }
                else
                {
                	return json.getString(TAG_MESSAGE);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
		}
		
        protected void onPostExecute(String message)
        {
            pDialog.dismiss();
            if (message != null)
            {
            	Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
	}
}
