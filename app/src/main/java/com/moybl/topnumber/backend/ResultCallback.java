package com.moybl.topnumber.backend;

import android.support.annotation.NonNull;

public interface ResultCallback<T extends Result> {

  void onResult(@NonNull T result);

}
