package com.candc.findlook;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "CarActivity";
	
	private WebView wsqWebView;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		
		url = getIntent().getStringExtra("url");
		
		wsqWebView = (WebView) findViewById(R.id.webview);
		init();
		
	}
	
	public void init () {
	    if(Build.VERSION.SDK_INT >= 19) {
	    	wsqWebView.getSettings().setLoadsImagesAutomatically(true);
	    } else {
	    	wsqWebView.getSettings().setLoadsImagesAutomatically(false);
	    }
	    
		// -----------------------------------------------------------------
	    wsqWebView.getSettings().setJavaScriptEnabled(true);	// 设置使用够执行JS脚本
		//wsqWebView.getSettings().setBuiltInZoomControls(true);	// 设置使支持缩放
		wsqWebView.getSettings().setDefaultFontSize(12);
		wsqWebView.setWebChromeClient(new WebChromeClient());
		wsqWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					String url) {
				view.loadUrl(url);// 使用当前WebView处理跳转
				return true;// true表示此事件在此处被处理，不需要再广播
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
			    if(!wsqWebView.getSettings().getLoadsImagesAutomatically()) {
			    	wsqWebView.getSettings().setLoadsImagesAutomatically(true);
			    }
			}
			
		});
		wsqWebView.loadUrl(url);
		// ------------------------------------------------
	}

}
