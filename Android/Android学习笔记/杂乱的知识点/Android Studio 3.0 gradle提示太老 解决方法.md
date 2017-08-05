# Android Studio 3.0 gradle提示太老 解决方法

The android gradle plugin version 3.0.0-alpha1 is too old, please update to the latest version. To override this check from the command line please set the 
ANDROID_DAILY_OVERRIDE environment variable to "d27b293f4c7c48dfe922ba160164f3fa511cb3b9" 
Upgrade plugin to version 3.0.0-alpha1 and sync project Open File

它是想告诉你,你的版本太老了;

解决方法:去环境变量那里配置一下吧新建一个变量:
变量名:ANDROID_DAILY_OVERRIDE
变量值:d27b293f4c7c48dfe922ba160164f3fa511cb3b9

楼主完美解决....