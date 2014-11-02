package com.candc.findlook;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 作者: Stonekity(596017443@qq.com)
 * 
 * 时间: 2014年10月10日 上午02:05
 * 
 * 描述: 查找结果商品列表适配器
 */
public class GoodsListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Good> mGoodsList; // 商品列表信息
	private LayoutInflater mInflater = null;
	
	//扫描到的商品的排名
	private String scanResItemRank = "";

	public GoodsListAdapter(Context context, List<Good> goodsList) {
		mContext = context;
		mGoodsList = goodsList;
		mInflater = LayoutInflater.from(context);
	}
	
	public String getScanResItemRank() {
		return scanResItemRank;
	}
	
	public void setScanResItemRank(String rank) {
		scanResItemRank = rank;
	}

	@Override
	public int getCount() {
		return mGoodsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mGoodsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// 刷新列表中的数据
	public void refresh(List<Good> list) {
		mGoodsList = list;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GoodsHolder goodHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_find_res, null);
			goodHolder = new GoodsHolder();
			goodHolder.tvGoodRank = (TextView) convertView
					.findViewById(R.id.tv_goods_rank);
			goodHolder.imgGoodPicture = (ImageView) convertView
					.findViewById(R.id.img_good_picture);
			goodHolder.tvGoodName = (TextView) convertView
					.findViewById(R.id.tv_good_name);
			goodHolder.tvGoodDescription = (TextView) convertView
					.findViewById(R.id.tv_good_description);
			convertView.setTag(goodHolder);
		} else {
			goodHolder = (GoodsHolder) convertView.getTag();
		}
		
		goodHolder.tvGoodRank.setText( "n."+(position+1) );
		if(position == mGoodsList.size()-1) {
			goodHolder.tvGoodRank.setTextColor(Color.RED);
			//为扫描到的商品添加排名
			goodHolder.tvGoodRank.setText("n."+scanResItemRank);
		}else {
			goodHolder.tvGoodRank.setTextColor(R.color.text_rank_color);
		}
		// 加载缩略图
		if (mGoodsList.get(position).getPicture() != null)
			mGoodsList.get(position).getPicture().loadImageThumbnail(mContext, goodHolder.imgGoodPicture, 72, 72, 100);
		goodHolder.tvGoodName.setText(mGoodsList.get(position).getName());
		goodHolder.tvGoodDescription.setText(mGoodsList.get(position).getDescription());
		return convertView;
	}

}
