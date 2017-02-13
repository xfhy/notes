# Android 五子棋开发经验 #
<font size="5"><b>
1. 当Activity继承自AppCompatActivity,这时想要去掉标题栏的话,则需要在AndroidManifest文件中将android:theme="@style/Theme.AppCompat.Light.NoActionBar"设置成这样既可.<br/>
2. 安卓游戏音效播放(短的音效,eg:棋子下棋,枪声):<br/>
首先是短音乐(7秒以内),所以需要使用SoundPool <br/>

	//实例化AudioManager对象，控制声音
    private AudioManager audioManager =null;
    //最大音量
    float audioMaxVolumn;
    //当前音量
    float audioCurrentVolumn;
    float volumnRatio;
    //音效播放池
    private SoundPool playSound = new SoundPool(2,AudioManager.STREAM_MUSIC,0);
    //存放音效的HashMap
    private Map<Integer,Integer> map = new HashMap<Integer,Integer>();

------------------------------------
	 /*
      初始化游戏音效
     */
    private void initPlaySound(){
        //实例化AudioManager对象，控制声音
        audioManager = (AudioManager)MyApplication.getContext().
                getSystemService(MyApplication.getContext().AUDIO_SERVICE);
       //最大音量
        audioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
       //当前音量
        audioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumnRatio = audioCurrentVolumn/audioMaxVolumn;
        map.put(0, playSound.load(MyApplication.getContext(),R.raw.chess_sound,1));
        map.put(1, playSound.load(MyApplication.getContext(),R.raw.chess_sound,1));
    }

---------------------------------------------

    //开始播放
	playSound.play(
                        map.get(0),//声音资源
                        volumnRatio,//左声道
                        volumnRatio,//右声道
                        1,//优先级
                        0,//循环次数，0是不循环，-1是一直循环
                        1);//回放速度，0.5~2.0之间，1为正常速度

在上面的代码中,这个代码可以连续播放30次以上,而网上的那些代码我的真机测试只能播放30次左右,不知为何(谷歌,百度找了很久,没找到答案).<br/>
后来发现
`private SoundPool playSound = new SoundPool(2,AudioManager.STREAM_MUSIC,0);`这个放到属性里面初始化即可,不要放到方法里面去初始化.<br/>
3. <br/>
</b></font>