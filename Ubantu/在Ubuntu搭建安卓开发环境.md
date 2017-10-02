# ubuntu-for-Android
 在Ubuntu搭建安卓开发环境(https://github.com/gaoneng102/ubuntu-for-Android)
 ![Ubuntu && Android](/ubuntuforandroid.png)
## 安装ubuntu（基于16.04）
### 步骤
http://www.linuxdiyf.com/linux/20012.html
* 如果是Windows与Linux双系统安装，请选择其他选项，切记。您可以自己创建、调整分区，或者为 Ubuntu 选择多个分区。
* 四个分区即可/boot、/、/home、swap。/home尽量给大点，因为平时使用的主要目录还是这里
* 最后一步安装启动程序选择/boot，这样就不会覆盖其他系统的启动
* 建议在有线网络下安装，因为wifi可能无法使用，导致安装之后某些图形界面的组件没有正常下载安装导致 “The system is running in low-graphics mode”异常<br>
https://askubuntu.com/questions/141606/how-to-fix-the-system-is-running-in-low-graphics-mode-error

### 优化
http://noogel.xyz/2017/06/17/1.html
* 更新前先设置源为aliyun的，国内访问速度快。
```
sudo apt-get update
sudo apt-get upgrade
```
* 删除Amazon的链接
```
sudo apt-get remove unity-webapps-common
```
* 卸载libreOffice(用WPS来替代)
```
sudo apt-get remove libreoffice-common
```
* 删除不常用的软件
```
sudo apt-get remove thunderbird totem rhythmbox empathy brasero simple-scan gnome-mahjon
sudo apt-get remove gnome-mines cheese transmission-common gnome-orca webbrowser-app gno
sudo apt-get remove onboard deja-dup
```

### 美化
* 先装 Unity 图形管理工具
```
sudo apt-get install unity-tweak-tool
```
* 安装 Flatabulous 主题
```
sudo add-apt-repository ppa:noobslab/themes
sudo apt-get update
sudo apt-get install flatabulous-theme
```
* 安装配套图标
```
sudo add-apt-repository ppa:noobslab/icons
sudo apt-get update
sudo apt-get install ultra-flat-icons
```
* 安装字体(文泉)
```
sudo apt-get install fonts-wqy-microhei
```

### 必备软件
* vim
```
sudo apt-get install vim
```
* git
```
sudo apt-get install git
```
* curl <br/>
https://curl.haxx.se/download.html
```
sudo apt-get install curl
```
* jq (配合curl 格式化json数据的神器)<br/>
https://stedolan.github.io/jq/
```
sudo apt-get install jq
```
* 安装zsh(以及 oh-my-zsh)
```
sudo apt-get install zsh
sh -c "$(curl -fsSL https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"
```
* RAR
```
sudo apt-get install rar
```
* Shadowsocks-Qt5 <br/>
https://github.com/shadowsocks/shadowsocks-qt5/wiki/%E5%AE%89%E8%A3%85%E6%8C%87%E5%8D%97
```
sudo add-apt-repository ppa:hzwhuang/ss-qt5
sudo apt-get update
sudo apt-get install shadowsocks-qt5
```
* chrome <br/>
https://www.google.com/chrome/browser/desktop/index.html
```
sudo dpkg -i google-chrome-stable_current_amd64.deb
```
* SwitchyOmega <br/>
https://github.com/FelisCatus/SwitchyOmega/wiki/GFWList

* shutter
```
sudo apt-get install shutter
```
* 搜狗输入法 <br/>
https://pinyin.sogou.com/linux/?r=pinyin

* wps <br/>
http://linux.wps.cn/
```
sudo dpkg -i wps-office_10.1.0.5672~a21_amd64.deb
sudo apt-get install -f
```
* gimp
```
sudo apt-get install gimp
```
* System Load Indicator（系统状态指示器）
```
sudo add-apt-repository ppa:indicator-multiload/stable-daily
sudo apt-get update
sudo apt-get install indicator-multiload
```
* Atom <br/>
https://atom.io/
```
sudo dpkg -i atom-amd64.deb
sudo apt-get -f install
```

### 驱动
* Qualcomm Atheros Device wifi驱动<br>
https://askubuntu.com/questions/708061/qualcomm-atheros-device-168c0042-rev-30-wi-fi-driver-installation

```
#查看驱动型号
lspci -vvnn | grep Network
#开始安装
sudo apt-get install build-essential linux-headers-$(uname -r) git
echo "options ath10k_core skip_otp=y" | sudo tee /etc/modprobe.d/ath10k_core.conf
wget https://www.kernel.org/pub/linux/kernel/projects/backports/stable/v4.4.2/backports-4.4.2-1.tar.gz
tar -zxvf backports-4.4.2-1.tar.gz
cd backport-4.4.2-1
make defconfig-wifi
make
sudo make install
git clone https://github.com/kvalo/ath10k-firmware.git
sudo cp -r ath10k-firmware/QCA9377 /lib/firmware/ath10k/
sudo cp /lib/firmware/ath10k/QCA9377/hw1.0/firmware-5.bin_WLAN.TF.1.0-00267-1 /lib/firmware/ath10k/QCA9377/hw1.0/firmware-5.bin
#需要重启才能生效

#如果更新了内核导致wifi驱动失败
cd backports-4.4.2-1
make clean
make defconfig-wifi
make
sudo make install

#查看wifi是否禁用
rfkill list
#开启wifi
rfkill unblock all
```

### 快捷键
* 修改快捷键<br/>
http://www.linuxdiyf.com/linux/22726.html
* 禁用ALT+右键快捷键
```
# 将默认的Alt+鼠标按键操作窗口（拖动、缩放、显示关闭菜单等）改为Windows键
gsettings set org.gnome.desktop.wm.preferences mouse-button-modifier '<Super>'
# 另外在设置前后可以用来查看设置的值
gsettings get org.gnome.desktop.wm.preferences mouse-button-modifier
# 不要将键值设置为'none'，否则会发现鼠标除了拖动窗口别无用处！!!
```

## 安卓开发环境搭建
* SDKMAN! CLI <br/>
http://sdkman.io
```
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk version
```
* java(通过上面的sdk命令安装)
```
sdk install java
```
* gradle <br/>
https://gradle.org/install/<br/>
https://services.gradle.org/distributions/
```
sdk install gradle 4.1
# Upgrade with the Gradle Wrapper
./gradlew wrapper --gradle-version=4.1 --distribution-type=bin
```

* android studio <br/>
https://developer.android.com/studio/index.html?hl=zh-cn#linux-bundle
```
# 64位需要安装32位的兼容库
sudo apt-get install -y libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1
# android 环境变量
export PATH=/home/gaoneng/Sdk/tools:$PATH
export PATH=/home/gaoneng/Sdk/platform-tools:$PATH
# android studio 环境变量
export PATH=/usr/local/android-studio/bin:$PATH
```
* android studio 常用设置<br/>
 1. 系统字体设置
 ```
 Settings –> Appearance ，勾选 Override default fonts by (not recommended)
 ```
 2. 修改默认快捷键
 ```
 Main menu –> Code –> Completion –> Basic ，修改为你想替换的快捷键组合
 ```
 3. 设置right margin警示线
 ```
 Settings –> Editor –> Appearance ，勾选 Show right margin
 ```
 4. 显示行号
 ```
 Settings –> Editor –> Appearance ，勾选 Show right margin
 ```
 5. 禁用拼写检查
 ```
 Settings –> Inspections –> Spelling ，取消勾选
 ```
 6. 自动导入设置
 ```
 Settings –> Editor –> Auto Import ，勾选 Add unambiguous improts on the fly
 ```
 7. android 导入模板文件
 ```
 https://github.com/keyboardsurfer/idea-live-templates
 ```
* android studio 实用插件<br/>
 1. GsonFormat
 ```
 快速将json字符串转换成一个Java Bean，免去我们根据json字符串手写对应Java Bean的过程。
 ```
 2. Android Parcelable code generator
 ```
 JavaBean序列化，快速实现Parcelable接口。
 ```
 3. adb-idea
 ```
 可以一键清理缓存并重启APP
 ```
 4. CodeGlance
 ```
 在右边可以预览代码，实现快速定位
 ```
 5. idea-markdown
 ```
 markdown插件
 ```
 6. WakaTime
 ```
 https://github.com/wakatime/jetbrains-wakatime
 记录你在IDE上的工作时间
 ```
