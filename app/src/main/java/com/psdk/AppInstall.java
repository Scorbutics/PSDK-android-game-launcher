package com.psdk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AppInstall {

	private static String INSTALL_NEEDED = "APP_INSTALL_NEEDED";

/*
	private static void copyAsset(Activity activity, String internalWriteablePath, String filename) throws IOException {
		final InputStream in = activity.getAssets().open(filename);
		File outFile = new File(internalWriteablePath, filename);

		final OutputStream out = new FileOutputStream(outFile);

		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}

		in.close();
		out.flush();
		out.close();
	}
*/
	public static String unpackExtraAssetsIfNeeded(Activity activity, SharedPreferences preferences) {
		if (preferences.getBoolean(INSTALL_NEEDED, true)) {
			final String internalWriteablePath = activity.getFilesDir().getAbsolutePath();

			try {
				final InputStream appInternalData = activity.getAssets().open("app-internal.zip");
				UnzipUtility.unzip(appInternalData, internalWriteablePath);

				final InputStream compiledGameData = activity.getAssets().open("compiled-game.zip");
				UnzipUtility.unzip(compiledGameData, internalWriteablePath);

				SharedPreferences.Editor edit = preferences.edit();
				edit.putBoolean(INSTALL_NEEDED, false);
				edit.commit();
			} catch (IOException exception) {
				Log.e("PSDK", "Error", exception);
				return exception.getMessage();
			}
		}
		return null;
	}

}
