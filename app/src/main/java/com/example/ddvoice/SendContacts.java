//package com.example.ddvoice;
//
//import java.util.List;
//
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.ContactsContract;
//import android.telephony.SmsManager;
//import android.util.Log;
//
//public class SendContacts {
//
//	private String mName,mReceiver,mNumberSender,mNumberReceiver;
//
//    MainActivity mActivity;
//
//	public SendContacts(String name,String receiver,MainActivity activity){
//		mName=name;
//		mReceiver=receiver;
//		mActivity=activity;
//	}
//
//	public void start(){
//		mNumberSender=getNumberByName(mName,mActivity);
//		mNumberReceiver=getNumberByName(mReceiver,mActivity);
//		if((mNumberSender == null)||(mNumberReceiver==null))
//        {
//			if(mNumberSender == null){
//				speak("ͨѶ¼û���ҵ�"+mName, false);
//			}
//			else speak("ͨѶ¼û���ҵ�"+mReceiver, false);
//
//        }else{
//       	 //����Ƭ
//       	 SmsManager smsManager = SmsManager.getDefault();
//             smsManager.sendTextMessage(mNumberReceiver, null, mNumberSender, null, null);
//             insertDB(mNumberReceiver,mNumberSender);
//
//       }
//	}
//
//	private  String getNumberByName(String name, Context context)
//	  {
//		 Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, name);
//		  ContentResolver  resolver  = context.getContentResolver();
//		  Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts._ID}, null, null, null);
//		  if((cursor!=null)&&(cursor.moveToFirst())){
//			  int idCoulmn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
//			  long id = cursor.getLong(idCoulmn);
//		      cursor.close();
//		      cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  new String[]{"data1"}, "contact_id = ?",  new String[]{Long.toString(id)}, null);
//		      if ((cursor != null) && (cursor.moveToFirst()))
//		      {
//		        int m = cursor.getColumnIndex("data1");
//		        String num = cursor.getString(m);
//		        cursor.close();
//		       return num;
//		      }
//		  }
//		  return null;
//	  }
//
//	 private void insertDB(String number,String content){//�����͵Ķ��Ų���ϵͳ���ݿ��У�ʹ���ڶ��Ž�����ʾ
//	    	//////////////////////���׳�null���쳣---�ѽ��--- mActivity.getContentResolver()�ſ���
//	    	try{
//		    	ContentValues values = new ContentValues();
//		    	values.put("date", System.currentTimeMillis());
//		    	 //�Ķ�״̬
//		        values.put("read", 0);
//		         //1Ϊ�� 2Ϊ��
//		       values.put("type", 2);
//		         //�ʹ����
//		      // values.put("status", -1);
//		       values.put("address",number);
//		         //�ʹ�����
//		       values.put("body", content);
//		         //������ſ�
//		      // getContentResolver
//		       mActivity.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
//		       speak("���ŷ��ͳɹ�",false);
//	    	}catch (Exception e) {
//	            Log.d("dd", "�������ݿ����⣺"+e.getMessage());
//	    	  }
//	    }
//}
