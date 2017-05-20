# ButterKnifeZelezny无需findViewById

效果如下:

![](http://olg7c0d2n.bkt.clouddn.com/17-5-18/3947276-file_1495068274892_1112c.gif)

# 1. 配置

1.安装ButterKnifeZelezny插件

File -> Seting -> Plugins -> Browser Responsities ->Butterknife Zelezny

2.配置应用的build.gradle

	buildscript {
	  repositories {
	    mavenCentral()
	   }
	  dependencies {
	    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
	  }
	}

然后在app的下面的build.gradle配置如下:

	apply plugin: 'android-apt'

	android {
	  ...
	}
	
	dependencies {
	  compile 'com.jakewharton:butterknife:8.2.1'
	  apt 'com.jakewharton:butterknife-compiler:8.2.1'
	}

3.最后右键单击所需布局,点击`Generate`,（例如，您的Activity或Fragment中的R.layout.main），然后点击`Generate ButterKnife Injections`