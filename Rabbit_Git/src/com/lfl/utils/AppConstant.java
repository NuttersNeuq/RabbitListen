package com.lfl.utils;

import java.io.File;

import android.os.Environment;

public class AppConstant
{
	/**
	 * 文件夹构成： NUT{mp3,lrc,pic,log.nut}
	 * 
	 * @author FIRE_TRAY
	 * 
	 */
	public static class FilePath
	{
		/**
		 * mnt/sdcard/ (依据手机情况具体判断)
		 */
		public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
		/**
		 * sdcard/NUT/
		 */
		public static final String NUT_ROOT = SDCARD_ROOT + "NUT" + File.separator;

		/**
		 * sdcard/NUT/mp3/
		 */
		public static final String MP3_FILE_PATH = NUT_ROOT + "mp3" + File.separator;

		/**
		 * sdcard/NUT/lrc/
		 */
		public static final String LRC_FILE_PATH = NUT_ROOT + "lrc" + File.separator;

		/**
		 * sdcard/NUT/pic/
		 */
		public static final String PIC_FILE_PATH = NUT_ROOT + "pic" + File.separator;

		/**
		 * sdcard/NUT/log.nut
		 */
		public static final String LOG_FILE_PATH = NUT_ROOT + "log.nut";

	}

	public static class URL
	{
		public static final String TEST_URL = "http://192.168.1.102:8080/nut/";

		public static final String NCC_NEUQ_MP3_URL = "http://ncc.neuq.edu.cn/box/file/mp3/";
		public static final String NCC_NEUQ_LRC_URL = "http://ncc.neuq.edu.cn/box/file/lrc/";
		public static final String NCC_NEUQ_PIC_URL = "http://ncc.neuq.edu.cn/box/file/pic/";

		public static final String CLOUD_URL = "http://121.40.185.131/android/";
		public static final String LATESTLISTEN_PHP_URL = CLOUD_URL + "latestlisten.php";
		public static final String TUIJIANBYBIAOQIAN_PHP_URL = CLOUD_URL + "tuijianbybiaoqian.php";
		public static final String START_PHP_URL = CLOUD_URL + "start.php";
		public static final String AIMSET_PHP_URL = CLOUD_URL + "aimset.php";
		public static final String ADD_TO_JINGTING_URL = CLOUD_URL + "addjingting.php";
		public static final String JINGTING_LIST_URL = CLOUD_URL + "jinglist.php";
		public static final String PAIHANG_LIST_URL = CLOUD_URL + "paihang.php";
		public static final String ALL_WORDS_LIST_URL = CLOUD_URL + "word.php";
		public static final String SENTENCE_LIST_URL = CLOUD_URL + "sentence.php";
		public static final String SEARCH_RESULT_LIST_URL = CLOUD_URL + "search.php";
		public static final String DEL_JINGTING_MP3_URL = CLOUD_URL + "del.php";
		public static final String SHOUCANG_MP3_URL = CLOUD_URL + "sslisten.php";
		public static final String MY_SUBCRIBE_URL = CLOUD_URL + "course.php";
		public static final String JINGTING_PASS_URL = CLOUD_URL + "finish.php";
		public static final String CURRENT_WORD_LIST_URL = CLOUD_URL + "lword.php";
		public static final String DIFFICULT_SENTENCE_LIST_URL = CLOUD_URL + "important.php";
		public static final String JINGTING_LISTENHISTORY_URL = CLOUD_URL + "listenhistory.php";
//		public static final String WORD_MEANING_LIST_URL = CLOUD_URL + "translation.php";
		public static final String MY_SHOUCANG_LIST_URL = CLOUD_URL + "sslistenlist.php";
		public static final String TINGLIKU_JINGTING_LIST_URL = CLOUD_URL + "jingall.php";
		public static final String JINRITUIJIAN_MP3_LIST_URL = CLOUD_URL + "jinglistnew.php";
		public static final String RESET_JINGTING_RPOGRESS = CLOUD_URL + "reset.php";
		
		public static final String ONLINE_DICTIONARY_URL = "http://fy.webxml.com.cn/webservices/EnglishChinese.asmx/Translator";
		
		public static final String ENCODING_DEFAULT = "GBK";
	}

	/**
	 * 网络异常 = "-1"; 缺少数据 = "0"; 交互成功 = "1"; 状态存储在Name中
	 * 
	 * @author FIRE_TRAY
	 * 
	 */
	public static class INTERACTION_STATUS
	{
		/**
		 * responseCode != 200
		 */
		public static final int NETWORK_CONNECTION_EXCEPTION = -1;
		/**
		 * 包括解析时的异常、FileNotFound异常以及无法提交，是所有返回码为"0"的情况
		 */
		public static final int SERVER_STATUS_EXCEPTION = 0;
		public static final int INTERACTION_SUCCESSFUL = 1;
		

		public static final String TOAST_NETWORK_CONNECTION_EXCEPTION = "网络似乎开小差叻";
		public static final String TOAST_SERVER_STATUS_EXCEPTION = "服务器似乎开小差叻";
		public static final String TOAST_INTERACTION_SUCCESSFUL = "提交成功";
		
	}

	public static class PlayParms
	{
		public static final int LRC_LANG_MODE_BOTH = 0xa1;
		public static final int LRC_LANG_MODE_CHS = 0xa2;
		public static final int LRC_LANG_MODE_ENG = 0xa3;
		public static final int LRC_LANG_MODE_NULL = 0xa4;

		public static final int PLAY_SPEED_1X = 1000;
		public static final int PLAY_SPEED_15X = 1050;
		public static final int PLAY_SPPED_2X = 1100;
		public static final int PLAY_SPEED_05X = 950;
		public static final int PLAY_SPEED_025X = 900;
		
		public static final int SYNC_HANDLER_DELAY_MILLS = 100;
	}

	public static class Actions
	{
		public static final String PLAYER_ASK_SERVICE_TO_PAUSE = "ACT1";
		public static final String PLAYER_ASK_SERVICE_TO_START = "ACT2";
		public static final String PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_START = "ACT3";
		public static final String PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_STOP = "ACT4";
		public static final String PLAYER_ASK_SERVICE_TO_CHANGE_CURRENTPOS = "ACT5";
		public static final String PLAYER_ASK_SERVICE_TO_SET_RATE = "ACT6";
		public static final String PLAYER_ASK_SERVICE_TO_SET_LRCLANG = "ACT7";
		public static final String PLAYER_ASK_SERVICE_TO_ADD_WORD = "ACT8";
		public static final String PLAYER_ASK_SERVICE_TO_DELETE_WORD = "ACT9";

		public static final String SERVICE_SEND_INIT_TO_PLAYER = "ACT10";
		public static final String SERVICE_SEND_CURRENTPOS_TO_PLAYER = "ACT11";

		public static final String SEND_MP3INFO_TO_SERVICE = "ACT12";

		public static final String PLAYER_GET_INIT_FROM_SERVICE = "ACT13";

		public static final String HEADSET_GET_INIT_STATUS_FROM_SERVICE = "ACT14";
		public static final String SERVICE_GIVE_INIT_STATUS_TO_HEADSET = "ACT15";

		public static final String PLAYERADAPTER_SEND_NEW_WORD = "ACT16";
		public static final String PLAYERADAPTER_SEND_NEW_LRCINDEX = "ACT17";

		public static final String PLAYERADAPTER_ASK_PLAYER_TO_PAUSE = "ACT18";
		public static final String PLAYERADAPTER_ASK_PLAYER_TO_RESUME = "ACT19";

		public static final String LOCALPLAYER_SHUTDOWN = "ACT20";

		public static final String PLAYERADAPTER_SEND_NEW_SENTENCE = "ACT21";

		public static final String PLAYER_ASK_SERVICE_TO_DELETE_SENTENCE = "ACT22";

		public static final String WORDBROWSER_DELETE_WORD = "ACT23";

		public static final String WORDBROWSER_ASK_TO_RESUME = "ACT24";

		public static final String SHARESDK_ASK_ONLINEPLAYER_TO_RESUME = "ACT25";
		
		public static final String PLAYER_ASK_SERVER_TO_RELEASE = "ACT26";
		
		public static final String PLAYER_ASK_SERVER_TO_INCREASE_ROUND = "ACT27";
		
	}

}
