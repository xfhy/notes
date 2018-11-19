> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/cuiweicai/article/details/51325010 版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/cuiweicai/article/details/51325010

最近遇到一个问题，在 Listview 中的 item 放个 editview ，然后监听 editview 的文本变化，来设置数组中的数，addTextChangedListener, 发现每个 item 的监听都被触发了, 导致我的数组中的每个数都是一样的。。然后 notifyDataSetChanged 后，就把界面 Listview 中的每个 item 中的文本框中的内容都变成刚才文本改的一样的。。

经过苦苦查询，终于找到解决方法，在此特别记录下。

**第一步**：在自定义的 adapter 中定义一个记录是否用原始记录还是文本监听改变记录的标识符。

```
 private boolean ischange=true;
```

**第二步**：
将监听放在初始化 viewholder 的那段代码里面，在监听事件中判断标识符，如果是 true 则不做任何操作，false 的话再接着操作。

**第三步**：
将 在 holder.product_ext.setText 之前设置为标识符 true，在 holder.product_ext.setText 之后设置为标识符 false。

二，三步的代码如下：

```

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OrderChanPin orderChanPin=mList.get(position);
        ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.commit_item,null);
            holder.product_name= (TextView) convertView.findViewById(R.id.product_name);
            holder.product_pic= (ImageView) convertView.findViewById(R.id.product_pic);
            holder.product_rating= (RatingBar) convertView.findViewById(R.id.product_rating);
            holder.product_ext= (EditText) convertView.findViewById(R.id.product_ext);
            holder.gridView =(GridView)convertView.findViewById(R.id.gridView);
            holder.lablegridView =(GridView)convertView.findViewById(R.id.gv_productlable);

// 第二步 

           holder.product_ext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!ischange) {
                    mList.get(position).setCommit(s + "");
                }
            }
        });

        holder.product_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!ischange) {
                    String str = String.valueOf(rating);
                    str = str.substring(0, str.indexOf("."));
                    orderChanPin.setRigbar(str);
                    mList.get(position).setRigbar(str);
                    notifyDataSetChanged();
                }

            }
        });
        convertView.setTag(holder);
    }else {
        holder= (ViewHolder) convertView.getTag();
        }

        //第三步
        ischange =true;
        holder.product_ext.setText(mList.get(position).getCommit());
        if(!mList.get(position).getRigbar().isEmpty()){
        holder.product_rating.setRating(Integer.parseInt(mList.get(position).getRigbar()));
        }
        ischange =false;

        return convertView;
        }
```

然后，这里代码中还有 RatingBar 的这个组件，当根据 setOnRatingBarChangeListener 这个监听来设置星级的时候，也会发生和文本监听变化类似的错误，会让整个 Listview 中的评星级都变成一样的等级。所以也需要和文本变化监听类似的处理方式。

如此处理后，即使复用 viewholder 也不会发生改一个文本，所有的文本都变成一样的。

<link href="https://csdnimg.cn/release/phoenix/mdeditor/markdown_views-7f770a53f2.css" rel="stylesheet">