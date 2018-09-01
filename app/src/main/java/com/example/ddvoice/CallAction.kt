package com.example.ddvoice

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper


class CallAction(person: String?, code: String?, internal var mContext: Context) {
    private var mPerson: String? = null
    private var number: String? = null
    
    init {
        mPerson = person
        number = code
    }
    
    
//    @SuppressLint("MissingPermission")
    fun start(): Boolean {
        if (!mPerson.isNullOrEmpty()) {
            //            if (mPerson.isNullOrEmpty())
            //                //speak("至少告诉我名字或者号码吧？", false);
            //
            //                //            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ""));
            //                //            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //                //            mActivity.startActivity(intent);
            //            } else {
            //                mPerson = mPerson!!.trim { it <= ' ' }
            //                number = gContactNameNumMap.get(PinyinHelper.convertToPinyinString(mPerson!!.toLowerCase(),
            //                        "",
            //                        PinyinFormat.WITHOUT_TONE))
            number = gContactNamePYNumMap.get(PinyinHelper.convertToPinyinString(mPerson!!
                    .toLowerCase(),
                    "", PinyinFormat
                    .WITHOUT_TONE))
            if (number.isNullOrEmpty()) {
                speak("没有找到" + mPerson + "的号码。")
//                genContactNameNumStr(gApplicationContext)   //update map
                gContactSyncOK = false
                return false
            } else {
                //打电话
                speak("即将拨给$mPerson...")
                Handler().postDelayed({
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number!!))
                    mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }, 3500)
            }
            //            }
        } else {
            speak("即将拨给$number...")
            Handler().postDelayed({
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number!!)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext.startActivity(intent)
            }, 6000)
            
        }
        
        return true
    }
    
    
    //   private String getNumberByName(String name, Context context)//通过名字查找号码
    //   {
    //      Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, name);
    //      ContentResolver resolver = context.getContentResolver();
    //      Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts._ID}, null,
    //				null, null);
    //      if ((cursor != null) && (cursor.moveToFirst())) {
    //         int idCoulmn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
    //         long id = cursor.getLong(idCoulmn);
    //         cursor.close();
    //         cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new
    //					String[]{"data1"}, "contact_id = ?", new String[]{Long.toString(id)}, null);
    //         if ((cursor != null) && (cursor.moveToFirst())) {
    //            int m = cursor.getColumnIndex("data1");
    //            String num = cursor.getString(m);
    //            cursor.close();
    //            return num;
    //         }
    //      }
    //      return null;
    //   }
    
    
    /*private fun getNumberByName(asrName: String): String {
        //联系人的Uri，也就是content://com.android.contacts/contacts
        val uri = ContactsContract.Contacts.CONTENT_URI
        //指定获取_id和display_name两列数据，display_name即为姓名
        val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        val cursor = mContext.contentResolver.query(uri, projection, null, null, null)
        val arr = arrayOfNulls<String>(cursor!!.count)
        var i = 0
        if (cursor != null && cursor.moveToFirst()) {
            val asrNamePinYin = PinyinHelper.convertToPinyinString(asrName.toLowerCase(), "", PinyinFormat
                    .WITHOUT_TONE)
            do {
                val id = cursor.getLong(0)
                //获取姓名
                val name = cursor.getString(1)
                //指定获取NUMBER这一列数据
                val phoneProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                //            arr[i] = id + " , 姓名：" + name;
                
                //根据联系人的ID获取此人的电话号码
                val phonesCusor = mContext.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null)
                
                //因为每个联系人可能有多个电话号码，所以需要遍历
                var num = ""
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    //               do {
                    num = phonesCusor.getString(0)
                    //                  arr[i] += " , 电话号码：" + num;
                    //               }while (phonesCusor.moveToNext());
                }
                
                val namePinYin = PinyinHelper.convertToPinyinString(name.toLowerCase(), "", PinyinFormat
                        .WITHOUT_TONE)
                if (namePinYin == asrNamePinYin) {
                    return num
                }
                i++
            } while (cursor.moveToNext())
        }
        
        return ""
        //      return arr;
    }*/
}
