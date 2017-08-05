# 解决Android Studio配置完Kotlin下载慢的问题

使用阿里云的国内镜像仓库地址，就可以快速的下载需要的文件

修改项目根目录下的文件

	build.gradle ：buildscript {
	    repositories {
			//加入下面这句
	        maven{ url 'http://maven.aliyun.com/nexus/content/groups/public/'}
	    }
	}
	
	allprojects {
	    repositories {
			//加入下面这句
	        maven{ url 'http://maven.aliyun.com/nexus/content/groups/public/'}
	    }
	}

作者：DIABLOHL
链接：https://www.zhihu.com/question/37810416/answer/153168766
来源：知乎