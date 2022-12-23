package com.psdk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;

public class MainActivity extends android.app.Activity {

	private static final int START_GAME_REQUESTCODE = 8700;

	private SharedPreferences m_projectPreferences;
	private static final String PROJECT_KEY = "PROJECT";
	private String m_errorMessage = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isTaskRoot()) {
			m_projectPreferences = getSharedPreferences(PROJECT_KEY, MODE_PRIVATE);

			final String errorUnpackAssets = AppInstall.unpackExtraAssetsIfNeeded(this, m_projectPreferences);
			if (errorUnpackAssets != null) {
				unableToUnpackAssetsMessage(errorUnpackAssets);
			}
		}
		loadScreen();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case START_GAME_REQUESTCODE:
				if (resultCode != RESULT_OK) {
					appendError("Error starting game activity, code : " + resultCode);
					return;
				}
				break;
			default:
				break;
		}
	}

	private void appendError(String error) {
		final TextView lastErrorLog = (TextView) findViewById(R.id.projectLastError);
		if (error != null && !error.trim().isEmpty()) {
			m_errorMessage += error + "\n";
		}
		if (lastErrorLog != null) {
			lastErrorLog.setText(m_errorMessage);
		}
	}

	private void loadScreen() {
		setContentView(R.layout.main);

		final Intent switchActivityIntent = new Intent(MainActivity.this, android.app.NativeActivity.class);
		switchActivityIntent.putExtra("EXECUTION_LOCATION", getExecutionLocation());
		switchActivityIntent.putExtra("INTERNAL_STORAGE_LOCATION", getFilesDir().getPath());
		final String outputFilename = getExecutionLocation() + "/last_stdout.log";
		switchActivityIntent.putExtra("OUTPUT_FILENAME", outputFilename);

		// Force error message refresh on view
		appendError(null);

		try {
			FileWriter fw = new FileWriter(outputFilename, false);
			fw.flush();
			final String startScript = PsdkProcess.readFromAssets(this, "start.rb");
			switchActivityIntent.putExtra("START_SCRIPT", startScript);
			MainActivity.this.startActivityForResult(switchActivityIntent, START_GAME_REQUESTCODE);
		} catch (Exception e) {
			appendError(e.getLocalizedMessage());
			Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}

	}

	private String getExecutionLocation() {
		return getApplicationInfo().dataDir;
	}

	private void unableToUnpackAssetsMessage(final String error) {
		appendError("Unable to unpack application assets : " + error);
		Toast.makeText(getApplicationContext(), m_errorMessage, Toast.LENGTH_LONG).show();
	}

}
