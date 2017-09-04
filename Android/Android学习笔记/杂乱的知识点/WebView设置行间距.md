# WebView设置行间距

> 当服务器返回的数据是用<p>标签包裹的html代码

这时候用WebView来加载这段HTML代码,有很多坑,比如img适配,字体大小设置,进度条等等

# 设置行间距
``` java

	String codePrefixOne = "<!DOCTYPE html PUBLIC -//W3C//DTD HTML 4.01 Transitional//EN " +
                "http://www.w3.org/TR/html4/loose.dtd>" +
                "<html>" +
                "<head>" +
                "<meta http-equiv=Content-Type content=text/html; charset=>" +
                "<style type=text/css>";

        String codePrefixTwo = "</style>" + "</head>" + "<body>";

        String codeSubfix = "</body></html>";

        String webData = codePrefixOne + "p {line-height:30px;}" + codePrefixTwo +
                getNewContent(sciNewsDetailItem.detail) + codeSubfix;

        wvScienceDetails.loadDataWithBaseURL(null, webData,
                "text/html", "utf-8", null);

```