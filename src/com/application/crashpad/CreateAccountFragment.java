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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Uses AsyncTask
public class CreateAccountFragment extends Fragment
{
	private static final String REGISTER_URL = "http://taz.harding.edu/~dcrouch1/crashpad/register.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_NAME= "name";
    
    private static final String PREF_FILE = "CrashPadLoginPrefsFile";
    private static final String PREF_REMEM = "remember";

	private EditText mUsernameEditText;
	private EditText mPasswordNewEditText;
	private EditText mPasswordConfirmEditText;
	private EditText mEmailEditText;
	private Button mConfirmAccountCreate;
	
	private ProgressDialog mProgressDialog;
    private boolean mTaskRunning;
		
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
		View view = inflater.inflate(R.layout.fragment_create_account, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }

		mUsernameEditText = (EditText)view.findViewById(R.id.new_username);
		mPasswordNewEditText = (EditText)view.findViewById(R.id.new_password);
		mPasswordConfirmEditText = (EditText)view.findViewById(R.id.confirm_password);
		mEmailEditText = (EditText)view.findViewById(R.id.new_email);
		
		mConfirmAccountCreate = (Button)view.findViewById(R.id.account_create_button);
		mConfirmAccountCreate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String user = mUsernameEditText.getText().toString();
				String pass1 = mPasswordNewEditText.getText().toString();
				String pass2 = mPasswordConfirmEditText.getText().toString();
				String email = mEmailEditText.getText().toString();
				
				if (user.length() != 0 && email.length() != 0 &&
						pass1.length() != 0 && pass1.equals(pass2))
				{
					new CreateUser().execute();
				}
				else if (!pass1.equals(pass2))
				{
					Toast.makeText(getActivity(), R.string.password_toast, Toast.LENGTH_SHORT).show();
				}
				else if (!isValidEmail(email))
				{
					Toast.makeText(getActivity(), R.string.email_toast, Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getActivity(), R.string.creation_error_toast, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return view;
	}
	
	protected boolean isValidEmail(CharSequence target)
	{
		if (TextUtils.isEmpty(target))
		{
			return false;
		}
		else
		{
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}
	
	class CreateUser extends AsyncTask<String, String, String>
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
            String password = mPasswordNewEditText.getText().toString();
            String email = mEmailEditText.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("email", email));

            try
            {
                JSONParser jsonParser = new JSONParser();
                JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                	//Save Login Info
            		SharedPreferences userInfo = getActivity().getSharedPreferences(PREF_FILE, 0);
            		SharedPreferences.Editor editor = userInfo.edit();
            		Log.d("TAG", "Not Saved");
            		editor.putBoolean(PREF_REMEM, false);
            		editor.commit();
                	
                	JSONObject o = json.getJSONObject(TAG_ACCOUNT);
                	Account tempAccount = new Account(username, password, email,
                			o.getString(TAG_NAME));

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
            catch (Exception e)
            {
            	e.printStackTrace();
            	return "Network Problems\nCheck WiFi Connection";
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
        mProgressDialog.setMessage("Creating Account...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
	}
}
