package app.turp.iconlab;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

final class SelfUpdater {
    static final String ACTION_INSTALL_RESULT = "app.turp.iconlab.INSTALL_RESULT";

    private SelfUpdater() {}

    static void install(Context context, File apk) throws Exception {
        PackageInstaller installer = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(context.getPackageName());
        params.setSize(apk.length());
        if (Build.VERSION.SDK_INT >= 31) {
            params.setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED);
        }

        int sessionId = installer.createSession(params);
        try (PackageInstaller.Session session = installer.openSession(sessionId);
             FileInputStream input = new FileInputStream(apk);
             OutputStream output = session.openWrite("base.apk", 0, apk.length())) {
            byte[] buffer = new byte[128 * 1024];
            int n;
            while ((n = input.read(buffer)) != -1) output.write(buffer, 0, n);
            session.fsync(output);

            Intent result = new Intent(context, InstallResultReceiver.class).setAction(ACTION_INSTALL_RESULT);
            PendingIntent pending = PendingIntent.getBroadcast(
                    context,
                    sessionId,
                    result,
                    PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_MUTABLE : 0));
            session.commit(pending.getIntentSender());
        } catch (Exception e) {
            installer.abandonSession(sessionId);
            throw e;
        }
    }
}
