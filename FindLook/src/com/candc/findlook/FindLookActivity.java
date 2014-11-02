package com.candc.findlook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.zbar.lib.CaptureActivity;

/**
 * 作者: Stonekity(596017443@qq.com)
 * 
 * 时间: 2014年10月10日 上午02:05
 * 
 * 描述: FindLook主列表界面
 */
public class FindLookActivity extends Activity implements OnItemClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = "FindLookActivity";

	private ListView lvFindRes;
	private GoodsListAdapter goodsListAdapter;
	private List<Good> goodsList;

	private String scanResString;
	private Good curGood; // 当前扫描到的商品
	private int hasBiggerValueGoodsCount; // 性价比比扫描到的商品要高的商品数量

	private ProgressDialog progress;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == MsgType.MSG_FIND_CURRENT_GOOD_START) {
				// 查找当前商品
				queryGoodById(scanResString);
			}

			if (msg.what == MsgType.MSG_FIND_RES_START) {
				// 获得特定类型的商品中比扫描出的商品的性价比更高的商品的总数量
				getHigherValueCount(curGood);
				// 开始查找性价比前10的商品列表
				findListData(curGood);
			}

			if (msg.what == MsgType.MSG_FIND_RES_FINISH) {
				// 刷新列表
				// 通知Adapter数据更新
				goodsListAdapter.setScanResItemRank(hasBiggerValueGoodsCount
						+ "");
				goodsListAdapter.refresh((ArrayList<Good>) goodsList);
				goodsListAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化Bmob
		Bmob.initialize(this, "15a0e5f3745039a54b1855bcb6fb8fbc");
		setContentView(R.layout.activity_find_look);

		initView();

		onClickScan();
	}

	private void initView() {
		curGood = new Good();
		goodsList = new ArrayList<Good>();
		lvFindRes = (ListView) findViewById(R.id.lv_find_res);
		goodsListAdapter = new GoodsListAdapter(this, goodsList);
		lvFindRes.setAdapter(goodsListAdapter);
		lvFindRes.setOnItemClickListener(this);

		progress = new ProgressDialog(this);
		progress.setCanceledOnTouchOutside(false);
	}

	/**
	 * 根据当前扫描到的商品ID，获得对应的商品
	 * 
	 * @param objectId
	 */
	private void queryGoodById(String objectId) {
		if (objectId.equals("")) {
			toast("扫描结果为空");
			return;
		}
		progress.show();
		BmobQuery<Good> query = new BmobQuery<Good>();
		query.getObject(this, objectId, new GetListener<Good>() {

			@Override
			public void onSuccess(Good good) {
				progress.dismiss();
				if (good != null) {
					curGood = good;
					Message msg = new Message();
					msg.what = MsgType.MSG_FIND_RES_START;
					mHandler.sendMessage(msg);
				} else {
					toast("该商品不存在");
				}
			}

			@Override
			public void onFailure(int code, String arg0) {
				progress.dismiss();
				toast("查询失败：" + arg0);
			}

		});
	}

	/**
	 * 获得特定类型的商品中比扫描出的商品的性价比更高的商品的总数量
	 * 
	 * @param good
	 */
	private void getHigherValueCount(Good good) {
		BmobQuery<Good> query = new BmobQuery<Good>();
		query.addWhereEqualTo("type", good.getType());
		query.addWhereGreaterThan("value", good.getValue());
		query.order("-value,createdAt");
		query.count(this, Good.class, new CountListener() {

			@Override
			public void onSuccess(int count) {
				hasBiggerValueGoodsCount = count;
			}

			@Override
			public void onFailure(int code, String msg) {
				toast("count failure：" + msg);
			}
		});
	}

	/**
	 * 根据特定的商品找到该分类的排列（前9名）
	 * 
	 * @param good
	 */
	private void findListData(final Good good) {
		if (good.equals("")) {
			toast("商品不存在");
			return;
		}
		progress.show();
		BmobQuery<Good> query = new BmobQuery<Good>();
		query.addWhereEqualTo("type", good.getType());
		query.addWhereGreaterThan("value", good.getValue());
		query.order("-value,createdAt");
		query.setLimit(9);
		query.findObjects(this, new FindListener<Good>() {

			@Override
			public void onSuccess(List<Good> object) {
				progress.dismiss();
				if (object != null) {
					goodsList = object;
					goodsList.add(good);
					// 发送消息数据加载完成，开始刷新列表
					Message msg = new Message();
					msg.what = MsgType.MSG_FIND_RES_FINISH;
					mHandler.sendMessage(msg);
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				progress.dismiss();
				toast("获取数据失败了");
			}
		});
	}

	public void onClickScan() {
		// 清空上次的查询数据
		goodsList.clear();
		goodsListAdapter.refresh((ArrayList<Good>) goodsList);
		goodsListAdapter.notifyDataSetChanged();
		// 启动二维码扫描
		startActivityForResult(new Intent(FindLookActivity.this,
				CaptureActivity.class), 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				scanResString = data.getStringExtra("scan_res");
				Message msg = new Message();
				msg.what = MsgType.MSG_FIND_CURRENT_GOOD_START;
				mHandler.sendMessage(msg);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "扫一扫");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == 1) {
			// 启动二维码扫描
			startActivityForResult(new Intent(FindLookActivity.this,
					CaptureActivity.class), 1);
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void toast(String toast) {
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		 Intent toWebView = new Intent(FindLookActivity.this,
		 WebActivity.class);
		 Bundle bundle = new Bundle();
		 bundle.putString("url", goodsList.get(position).getUrl());
		 toWebView.putExtras(bundle);
		 startActivity(toWebView);
	}

}
