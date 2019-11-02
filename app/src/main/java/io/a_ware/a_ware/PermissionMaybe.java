package io.a_ware.a_ware;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import eu.chainfire.libsuperuser.Shell;

public class PermissionMaybe extends Worker {

    public PermissionMaybe(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.
        String commandToRun = getInputData().getString("command");

        Shell.SU.run(new String[] {
            commandToRun
        });

        Log.d("periodic permission deny job - Aware - ", commandToRun);
        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }
}
