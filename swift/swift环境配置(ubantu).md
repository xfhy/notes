# Ubantu下配置swift开发环境 

## 步骤:

1. 首先安装`vim`编辑器(`apt-get install vim`)

2. 下载`Swift`官方压缩包：`https://swift.org/download/`,选择自己的环境.我选择的是`swift-3.0.1-PREVIEW-1-ubuntu16.04`   下载完成之后,解压缩.


3. 现在开始配置环境变量,`vim ~/.bashrc`   编辑这个文件,用vim打开,编辑模式,拖到最后一行在其后面插入`export SWIFT_HOME=~/Downloads/swift-3.0.1-PREVIEW-1-ubuntu16.04
export PATH=$SWIFT_HOME/usr/bin:$PATH`    注意,上面第一行:等号后面的表示你的swift解压的路径,下面的不用管.

4. 验证环境变量,在终端输入`swift`,如果出现`Welcome to Swift....`就说明是环境变量配置成功啦!恭喜.  现在可以直接在这里写入临时的`swift`代码,注意是临时的.


5. 如果想要写`swift`,需要新建一个文件,比如`touch hello.swift`.用`vim`打开,在里面写入`Hello World`的代码,保存退出.


6. 编译上面的代码源文件,打开终端,找到源代码所在目录,输入命令`swiftc hello.swift`,即可编译,如果没有出现错误,则说明编译成功.(ps:如果编译源代码时出现链接错误,解决办法是安装编译依赖`clang libicu-dev`,输入下面命令回车(会询问当前用户密码))


7. 运行上面的`hello`,编译之后会出现一个新文件`hello`;这是`ubantu`的可执行文件,直接输入 `./hello`即可执行