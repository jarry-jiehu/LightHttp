命令说明：
生成jar包命令： 在项目的跟目录运行 buildJar.sh脚本
   生成目录: target/
   文件说明： 生成的OKHTTP和OKIO是适配jdk 1.6的，如果不需要可以忽略

编译demo命令：  gradle assembleDebug
   生成目录：app/build/outputs/apk/app-debug.apk

生成doc方法：
    ./gradlew zipJavadoc
    生成目录：target

提交代码：git push origin HEAD:refs/for/master
