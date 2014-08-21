/**
 * 
 */
package com.fsck.k9.activity;

import com.fsck.k9.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * <p>Title: Splash.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: KAIST., Ltd</p>
 * 
 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
 * @version v 1.0 2011. 4. 24.
 */

public class Splash extends Activity {
	
	final int TERMINATE_TIME = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		// Splash Activity
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			public void run() {
				finish();
			}
		}, TERMINATE_TIME*2000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }
}
