# Android 多击事件

> 需求:有时候需要做控件的多击事件

> 分析:声明一个数组,每点击一次,就将数据放到最后一位上.每一次都将从第二位开始数据往前挪一位   这样最后判断之间的差值(第1下和第5下是否间隔小于500毫秒),差值小于500,就做点事情.

# 一.直接上代码

	public class MainActivity extends Activity implements OnClickListener {

		private ImageView iv_girl;
		private long mHits[] = new long[5];
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
	
			iv_girl = (ImageView) findViewById(R.id.iv_girl);
			iv_girl.setOnClickListener(this);
	
		}
	
		@Override
		public void onClick(View v) {
			
			System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
			mHits[mHits.length-1] = SystemClock.uptimeMillis();
			if(mHits[mHits.length-1]-mHits[0]<500){
				Toast.makeText(this, "果然是真男人!!!!!!\n送你一张美图", Toast.LENGTH_SHORT).show();
			}
		}
	
	}


如图:
![](http://olg7c0d2n.bkt.clouddn.com/17-5-3/22588509-file_1493779116748_153bc.png)
