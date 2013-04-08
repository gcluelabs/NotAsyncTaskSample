package com.example.asynctasksample;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class AsyncTaskSample extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// 描画クラスのインスタンスを生成
		MySurfaceView mSurfaceView = new MySurfaceView(this);
		setContentView(mSurfaceView);
	}
}

class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	/** 太鼓画像データを保持する。 */
	private Bitmap taikoOffImage;
	private Bitmap taikoOnImage;
	/** GClueマーク */
	private Bitmap gclueImage;
	/** 太鼓画像の原点（左上）のx座標を保持する。 */
	private int taikoX = 100;
	/** 太鼓画像の原点（左上）のy座標を保持する。 */
	private int taikoY = 50;
	/** サウンド再生データを保持する。 */
	private MediaPlayer mp;
	/** 太鼓押下フラグ */
	private boolean isTaiko = false;
	/** タイマー */
	private int timer = 30;
	/** カウンター */
	private int counter = 0;

	public MySurfaceView(Context context) {
		super(context);
		// イベント取得できるようにFocusを有効にする
		setFocusable(true);
		// Resourceインスタンスの生成
		Resources res = this.getContext().getResources();
		// 画像の読み込み（/res/drawable-hdpi/taiko.png）
		taikoOffImage = BitmapFactory.decodeResource(res, R.drawable.taiko);
		taikoOnImage = BitmapFactory.decodeResource(res, R.drawable.taicoclick);
		gclueImage = BitmapFactory.decodeResource(res, R.drawable.logo);
		// サウンドデータを読み込む(/res/raw/pon.mp3)
		mp = MediaPlayer.create(context, R.raw.pon);

		// Callbackを登録する
		getHolder().addCallback(this);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		drawTaiko();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	/**
	 * タッチイベント
	 */
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 指がタッチされたx,y座標の取得
			int touchX = (int) event.getX();
			int touchY = (int) event.getY();

			// 太鼓の中心座標を計算
			int centerX = taikoX + taikoOffImage.getWidth() / 2;
			int centerY = taikoY + taikoOffImage.getHeight() / 2;

			// 太鼓の中心座標と指の距離を計算
			double distance = Math.sqrt(Math.pow((centerX - touchX), 2)
					+ Math.pow((centerY - touchY), 2));

			// 太鼓画像の半径
			int taikoR = taikoOffImage.getWidth() / 2;

			// あたり判定
			if (distance < taikoR) {
				// サウンド再生
				mp.start();
				isTaiko = true;
				counter++;
				drawTaiko();
			}

			// GClueイメージのタッチイベント
			// GClueマークの中心座標を計算
			centerX = gclueImage.getWidth() / 2;
			centerY = taikoY + taikoOffImage.getHeight()
					+ gclueImage.getHeight() / 2;

			// GClueマークの中心座標と指の距離を計算
			distance = Math.sqrt(Math.pow((centerX - touchX), 2)
					+ Math.pow((centerY - touchY), 2));

			// GClueマーク画像の半径
			int gclueR = gclueImage.getWidth() / 2;

			// あたり判定
			if (distance < gclueR) {
				counter = 0;
				// 30秒カウントする
				try {
					for (int i = 30; i > 0; i++) {
						Thread.sleep(1000);
						timer -= 1;
						drawTaiko();
					}
				} catch (Exception e) {}
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			isTaiko = false;
			drawTaiko();
		}
		return true;
	}

	/**
	 * 太鼓の画像を描画するメソッド
	 */
	private void drawTaiko() {
		// Canvasを取得する
		Canvas canvas = getHolder().lockCanvas();

		// 背景色を設定する
		canvas.drawColor(Color.BLACK);

		// Bitmapイメージの描画
		Paint mPaint = new Paint();
		if (!isTaiko) {
			canvas.drawBitmap(taikoOffImage, taikoX, taikoY, mPaint);
		} else {
			canvas.drawBitmap(taikoOnImage, taikoX, taikoY, mPaint);
		}
		canvas.drawBitmap(gclueImage, 0, taikoY + taikoOffImage.getHeight(),
				mPaint);

		// タイマーの設定
		mPaint.setTextSize(50);
		mPaint.setColor(Color.WHITE);
		canvas.drawText("タイマー:" + timer, gclueImage.getWidth() + 100, taikoY
				+ taikoOffImage.getHeight() + 50, mPaint);
		// カウンターの設定
		mPaint.setTextSize(100);
		mPaint.setColor(Color.WHITE);
		canvas.drawText("太鼓:" + counter, gclueImage.getWidth() + 100, taikoY
				+ taikoOffImage.getHeight() + gclueImage.getHeight() + 200,
				mPaint);
		// 画面に描画をする
		getHolder().unlockCanvasAndPost(canvas);
	}

}