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

//Uses AsyncTask
public class LoginFragment extends Fragment
{
	private static final String LOGIN_URL = "http://taz.harding.edu/~dcrouch1/crashpad/login.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_EMAIL = "a_email";
    private static final String TAG_NAME= "a_name";
	
	private EditText mUsernameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private Button mCreateNewAccountButton;
	private ProgressDialog mProgressDialog;
    private boolean mTaskRunning;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mTaskRunning = false;
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
				if (mUsernameEditText.getText().toString().length() != 0 &&
						mPasswordEditText.getText().toString().length() != 0)
				{
					new AttemptLogin().execute();
				}
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
            showProgressDialog();
            mTaskRunning = true;
        }

		@Override
		protected String doInBackground(String... args)
		{			
            int success;
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            try
            {
            	JSONParser jParser = new JSONParser();
                JSONObject json = jParser.makeHttpRequest(LOGIN_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                	JSONObject o = json.getJSONObject(TAG_ACCOUNT);
                	Account tempAccount = new Account(username, password,
                			o.getString(TAG_EMAIL), o.getString(TAG_NAME));

                	AccountCurrent.get(getActivity()).setPresentAccount(tempAccount);
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
        	mTaskRunning = false;
        	mProgressDialog.dismiss();
            if (message != null)
            {
            	Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);
        if (mTaskRunning)
        {
        	showProgressDialog();
        }
    }
	
	@Override
    public void onDetach()
	{
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
        	mProgressDialog.dismiss();
        }
        super.onDetach();
    }
	
	private void showProgressDialog()
	{
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Attempting Login...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
	}
}
