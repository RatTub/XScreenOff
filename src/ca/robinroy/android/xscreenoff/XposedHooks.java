package ca.robinroy.android.xscreenoff;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedHooks implements IXposedHookLoadPackage {
	private static final String PACKAGE_NAME = XposedHooks.class.getPackage().getName();
	private static final String KEEP_SCREEN_OFF = "keep_screen_off";

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
    	if (lpparam.packageName.equals("android")) {
    		findAndHookMethod("com.android.server.power.PowerManagerService", lpparam.classLoader, "shouldWakeUpWhenPluggedOrUnpluggedLocked", Boolean.TYPE, Integer.TYPE, Boolean.TYPE, PowerManagerService_shouldWakeUpWhenPluggedOrUnpluggedLocked);
    	}
    }
    
    public final XC_MethodHook PowerManagerService_shouldWakeUpWhenPluggedOrUnpluggedLocked = new XC_MethodHook() {
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			XSharedPreferences settings = new XSharedPreferences(PACKAGE_NAME);
			boolean wakeup = !settings.getBoolean(KEEP_SCREEN_OFF, true); 

			if (wakeup) XposedBridge.log("[XScreenOff] Wakeup: true"); 
			else XposedBridge.log("[XScreenOff] Wakeup: false");
			
			XposedHelpers.setBooleanField(param.thisObject, "mWakeUpWhenPluggedOrUnpluggedConfig", wakeup);
		}
    };
}