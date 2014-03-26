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
import android.widget.TextView;
import android.widget.Toast;

public class EditAccountFragment extends Fragment
{
	private static final String EDIT_URL = "http://taz.harding.edu/~dcrouch1/crashpad/edit_account.php";
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	private EditText emailEditText;
	private EditText passwordEditText;
	private TextView usernameTextView;
	private Button mConfirmChangesButton;
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
		View view = inflater.inflate(R.layout.fragment_edit_account, parent, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (NavUtils.getParentActivityName(getActivity()) != null)
			{
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
        }
		
		passwordEditText = (EditText)view.findViewById(R.id.edit_password);
		emailEditText = (EditText)view.findViewById(R.id.edit_email);

		String userName = PresentAccount.get(getActivity()).getPresentAccount().getName();
		usernameTextView = (TextView)view.findViewById(R.id.username_view);
		usernameTextView.setText(userName);
		
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
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Editing Account...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... args)
		{			
            int success;
            String username = PresentAccount.get(getActivity()).getPresentAccount().getName();
            String email = (emailEditText.getText().toString().length() != 0)?
            		emailEditText.getText().toString() : PresentAccount.get(getActivity()).getPresentAccount().getEmail();
            String password = (passwordEditText.getText().toString().length() != 0)?
            		passwordEditText.getText().toString() : PresentAccount.get(getActivity()).getPresentAccount().getPassword();
            
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
