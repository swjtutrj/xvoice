//package com.example.ddvoice;
//
//import android.os.Handler;
//
//
//
//public class Translation {
//	private String mTarget,mSource,mContent,mResult;
//	private MainActivity mActivity;
//	private Handler handler = new Handler();
//
//	public Translation(String target,String source,String content,MainActivity activity){
//		mTarget=target;
//		mSource=source;
//		mContent=content;
//		mActivity=activity;
//	}
//
//	public void start(){//��ʼ����
//		mTarget=changeLanguageCode(mTarget);
//		mSource=changeLanguageCode(mSource);
//		new Thread(r).start();
//	}
//
//	private String changeLanguageCode(String originalCode) {//�ı����Ա��뷽ʽ
//		// TODO Auto-generated method stub
//		switch(originalCode){
//		case "ja":{
//			return "jp";
//		}
//		case "ko":{
//			return "kor";
//		}
//		case "es":{
//			return "spa";
//		}
//		case "fr":{
//			return "fra";
//		}
//		default:return originalCode;
//		}
//	}
//
//	Runnable r = new Runnable() {//���ðٶȷ���API
//		@Override
//		public void run() {
//			mResult = Utils.upLoad(mContent,mSource,mTarget);
//			handler.post(new Runnable() {
//				@Override
//				public void run() {
//					speak(mResult, false);
//				}
//			});
//
//		}
//	};
//}
