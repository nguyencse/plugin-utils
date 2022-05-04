#!/bin/sh
"/home/nguyenny/.gradle/caches/modules-2/files-2.1/com.jetbrains/jbre/jbr-11_0_10-linux-x64-b1145.96/jbr/bin/java" -cp "/opt/android-studio/plugins/git4idea/lib/git4idea-rt.jar:/opt/android-studio/lib/xmlrpc-2.0.1.jar:/opt/android-studio/lib/commons-codec-1.14.jar" org.jetbrains.git4idea.http.GitAskPassApp "$@"
