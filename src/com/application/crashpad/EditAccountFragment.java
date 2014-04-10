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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditAccountFragment extends Fragment
{
	private static final String EDIT_URL = "http://taz.harding.edu/~dcrouch1/crashpad/edit_account.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private String mUsername;
	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private TextView mUsernameTextView;
	private Button mConfirmChangesButton;
	private ProgressDialog mProgressDialog;
	
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
		
		mUsername = AccountCurrent.get(getActivity()).getPresentAccount().getName();
		
		mPasswordEditText = (EditText)view.findViewById(R.id.edit_password);
		mEmailEditText = (EditText)view.findViewById(R.id.edit_email);

		mUsernameTextView = (TextView)view.findViewById(R.id.username_view);
		mUsernameTextView.setText(mUsername);
		
		mConfirmChangesButton = (Button)view.findViewById(R.id.confirm_changes_button);
		mConfirmChangesButton.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				new EditAccount().execute();
			}
		});
		
		return view;	
	}
	
	class EditAccount extends AsyncTask<String, String, String>
	{
		boolean failure = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Editing Account...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

		@Override
		protected String doInBackground(String... args)
		{			
            int success;
            String username = mUsername;
            //FIX
            //Is this the best place and way to do this?
            String email = (mEmailEditText.getText().toString().length() != 0)?
            		mEmailEditText.getText().toString() : AccountCurrent.get(getActivity()).getPresentAccount().getEmail();
            String password = (mPasswordEditText.getText().toString().length() != 0)?
            		mPasswordEditText.getText().toString() : AccountCurrent.get(getActivity()).getPresentAccount().getPassword();
            
            try
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("password", password));

            	JSONParser jParser = new JSONParser();
                JSONObject json = jParser.makeHttpRequest(EDIT_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
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
            mProgressDialog.dismiss();
            if (message != null)
            {
            	Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
	}
}
