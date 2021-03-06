package com.application.crashpad;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//Uses AsyncTask
public class EditAccountFragment extends Fragment
{
	private static final String EDIT_URL = "http://taz.harding.edu/~dcrouch1/crashpad/edit_account.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private String mUsername;
	private EditText mEmailEditText;
	private EditText mNameEditText;
	private EditText mPasswordNewEditText;
	private EditText mPasswordConfirmEditText;
	private TextView mUsernameTextView;
	private Button mConfirmChangesButton;
	
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
		View view = inflater.inflate(R.layout.fragment_edit_account, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }
		
		mUsername = AccountCurrent.get(getActivity()).getPresentAccount().getUsername();
		
		mEmailEditText = (EditText)view.findViewById(R.id.edit_email);
		mNameEditText = (EditText)view.findViewById(R.id.edit_name);
		mEmailEditText.setHint(AccountCurrent.get(getActivity()).getPresentAccount().getEmail());
		mNameEditText.setHint(AccountCurrent.get(getActivity()).getPresentAccount().getName());
		
		mPasswordNewEditText = (EditText)view.findViewById(R.id.edit_password);
		mPasswordConfirmEditText = (EditText)view.findViewById(R.id.confirm_password);

		mUsernameTextView = (TextView)view.findViewById(R.id.username_view);
		mUsernameTextView.setText(mUsername);
		
		mConfirmChangesButton = (Button)view.findViewById(R.id.update_info_button);
		mConfirmChangesButton.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				String originalEmail = AccountCurrent.get(getActivity()).getPresentAccount().getEmail();
				String originalName = AccountCurrent.get(getActivity()).getPresentAccount().getName();
				String originalPass = AccountCurrent.get(getActivity()).getPresentAccount().getPassword();
				
				String email = mEmailEditText.getText().toString();
				String name = mNameEditText.getText().toString();
				String pass1 = mPasswordNewEditText.getText().toString();
				String pass2 = mPasswordConfirmEditText.getText().toString();

				//Checks if anything has changed
				if (!(	(originalPass.equals(pass1) || pass1.length() == 0) &&
						(originalEmail.equals(email) || email.length() == 0) &&
						(originalName.equals(name) || name.length() == 0) ))
				{
					if (pass1.equals(pass2) && isValidEmail(email))
					{
						new EditAccount().execute();
					}
					else if (!pass1.equals(pass2))
					{
						Toast.makeText(getActivity(), R.string.password_toast, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getActivity(), R.string.email_toast, Toast.LENGTH_SHORT).show();
					}
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
	
	class EditAccount extends AsyncTask<String, String, String>
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
            String username = mUsername;
            
            //Ignores blank field
            String email = (mEmailEditText.getText().toString().length() != 0)?
            		mEmailEditText.getText().toString() : AccountCurrent.get(getActivity()).getPresentAccount().getEmail();
            String name = (mNameEditText.getText().toString().length() != 0)?
            		mNameEditText.getText().toString() : AccountCurrent.get(getActivity()).getPresentAccount().getName();
            String password = (mPasswordNewEditText.getText().toString().length() != 0)?
            		mPasswordNewEditText.getText().toString() : AccountCurrent.get(getActivity()).getPresentAccount().getPassword();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("password", password));

            try
            {
            	JSONParser jParser = new JSONParser();
                JSONObject json = jParser.makeHttpRequest(EDIT_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                	Account tempAccount = AccountCurrent.get(getActivity()).getPresentAccount();
                	tempAccount.setEmail(email);
                	tempAccount.setName(name);
                	tempAccount.setPassword(password);

                	AccountCurrent.get(getActivity()).setPresentAccount(tempAccount);
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
        mProgressDialog.setMessage("Editing Account...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
	}
}
