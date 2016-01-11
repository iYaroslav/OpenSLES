package uz.yarilocode.opensles;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Yaroslav on 11.12.15.
 * Copyright 2015 iYaroslav LLC.
 */
public class PermissionsService {

	public static Ternary check(AppCompatActivity activity, String permission, int callback) {
		int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);

		if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
			return Ternary.TRUE;
		} else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
			return Ternary.FALSE;
		} else {
			ActivityCompat.requestPermissions(activity, new String[]{permission}, callback);
			return Ternary.UNKNOWN;
		}
	}

	public static boolean checkResults(int[] grantResults) {
		return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
	}

	public static void openDialog(AppCompatActivity activity, String[] permissions) {
		Bundle bundle = new Bundle();
//		TODO send params!

//		activity.runActivity(PermissionNotGrantedActivity.class, bundle);
	}

}