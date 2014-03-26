package com.application.crashpad;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.application.crashpad.LoginFragment.AttemptLogin;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNewAccountFragment extends Fragment
{
	private static final String REGISTER_URL = "http://taz.harding.edu/~dcrouch1/crashpad/register.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	private EditText usernameEditText;
	private EditText nPasswordEditText;
	private EditText cPasswordEditText;
	private EditText emailEditText;
	private Button mConfirmAccountCreate;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
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
		View view = inflater.inflate(R.layout.fragment_create_new_account, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		usernameEditText = (EditText)view.findViewById(R.id.new_user_username);
		nPasswordEditText = (EditText)view.findViewById(R.id.new_user_password);
		cPasswordEditText = (EditText)view.findViewById(R.id.confirm_user_password);
		emailEditText = (EditText)view.findViewById(R.id.new_user_email);
		
		mConfirmAccountCreate = (Button)view.findViewById(R.id.confirm_create_account_button);
		mConfirmAccountCreate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String pass1 = nPasswordEditText.getText().toString();
				String pass2 = cPasswordEditText.getText().toString();
				if (pass1.equals(pass2))
				{
					new CreateUser().execute();
				}
				else
				{
					Toast.makeText(getActivity(), "Passwords do not match.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		return view;
	}
	
	class CreateUser extends AsyncTask<String, String, String>
	{
		boolean failure = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Creating Account...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... args)
		{
            int success;
            String username = usernameEditText.getText().toString();
            String password = nPasswordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            
            try
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("email", email));

                JSONObject json = jsonParser.makeHttpRequest(
                		REGISTER_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                
                if (success == 1)
                {
                	PresentAccount.get(getActivity()).setPresentAccount(new Account(username, password, email));
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
		
        protected void onPostExecute(String file_url)
        {
            pDialog.dismiss();
            if (file_url != null)
            {
            	Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
            }
        }
	}
}
