package me.jp.sticker;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.jp.sticker.adapter.GalleryAdapter;
import me.jp.sticker.view.StickerView;


public class MainActivity extends Activity {
    RecyclerView mRecyclerView;
    ImageView mImageView;
    GalleryAdapter mGalleryAdapter;

    int mStatusBarHeight;

    List<StickerView> mStickers = new ArrayList<>();
    int[] mResIds = new int[]{R.mipmap.ic_sticker_01,R.mipmap.ic_sticker_02,R.mipmap.ic_sticker_03,R.mipmap.ic_sticker_04,R.mipmap.ic_sticker_05,R.mipmap.ic_sticker_06,R.mipmap.ic_sticker_07,R.mipmap.ic_sticker_08};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        mGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int resId) {
                addStickerItem(resId);
            }
        });
    }

    private void addStickerItem(int resId) {
        resetStickersFocus();
        StickerView stickerView = new StickerView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.image);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.image);
        ((ViewGroup) mImageView.getParent()).addView(stickerView, params);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        stickerView.setWaterMark(bitmap);
        mStickers.add(stickerView);
        stickerView.setOnStickerDeleteListener(new StickerView.OnStickerDeleteListener() {

            @Override
            public void onDelete(StickerView stickerView) {
                if (mStickers.contains(stickerView))
                    mStickers.remove(stickerView);
            }

        });
    }

    private void resetStickersFocus() {
        for (StickerView stickerView : mStickers) {
            stickerView.setFocusable(false);
        }
    }

    private void initView() {
        mStatusBarHeight = getStatusBarHeight();
        mImageView = (ImageView) findViewById(R.id.image);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mImageView.setImageBitmap(getImageFromAssetsFile("photo.jpg"));

        mGalleryAdapter = new GalleryAdapter(mResIds);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mGalleryAdapter);
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            //calculate action point Y apart from Container layout origin
            int y = (int) ev.getY() - mStatusBarHeight;
            for (StickerView stickerView : mStickers) {
                boolean isContains = stickerView.getContentRect().contains(x, y);
                if (isContains) {
                    resetStickersFocus();
                    stickerView.setFocusable(true);
                }
            }
        }
        return super.dispatchTouchEvent(ev);

    }
}
