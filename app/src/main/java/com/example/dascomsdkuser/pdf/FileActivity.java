package com.example.dascomsdkuser.pdf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileActivity extends Activity {
	private  ListView listV = null;
	private List<File> list = null;
	private int a[] = {R.drawable.dir,R.drawable.pictures};
	private ArrayList<HashMap<String, Object>> recordItem;
	
	private File presentFile;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse);
		listV = (ListView)findViewById(R.id.list);
		//判断手机有无内存卡
//		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//			Toast.makeText(FileActivity.this, "SdCard exists",Toast.LENGTH_LONG).show();
//		}else{
//			Toast.makeText(FileActivity.this, "SdCard does not exist",Toast.LENGTH_LONG).show();
//		}
		list_files();
	}
	private void list_files(){
		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		presentFile = path;
		File[] file = path.listFiles();
		fill(file);
	}
	private void fill(File[] file){
		SimpleAdapter adapter = null;
		recordItem = new ArrayList<HashMap<String, Object>>();
		list = new ArrayList<File>();
		for(File f: file){
			
			if(Invalid(f) == 1){
				list.add(f);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("file", a[1]);
				map.put("name", f.getName());
				recordItem.add(map);
			}
			if(Invalid(f) == 0){
				list.add(f);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("file", a[0]);
				map.put("name", f.getName());
				recordItem.add(map);
			}
		}
		
		
		adapter = new SimpleAdapter(this, recordItem, R.layout.item, new String[]{"file", "name"}, new int[]{R.id.picture, R.id.text});
		listV.setAdapter(adapter);
		listV.setAdapter(adapter);
		listV.setOnItemClickListener(new Clicker());
	}

	private int Invalid(File f){
		if(f.isDirectory()){
			return 0;
		}
		else{
			String filename = f.getName().toLowerCase();
			
			if(filename.endsWith(".pdf")){
				return 1;
			}
			return 2;
		}
	}
	private class Clicker implements OnItemClickListener{
		
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			// TODO Auto-generated method stub
			String nameStr = null;

			File file = list.get(arg2);
			presentFile = file;
			if(file.isDirectory()){
				File[] files = file.listFiles();
				fill(files);
			}
			else{
				nameStr = file.getAbsolutePath();
				Intent data=new Intent();
				data.putExtra("path", nameStr);
				setResult(20, data);
				finish();
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   	if (keyCode == KeyEvent.KEYCODE_BACK) {
//	   		this.finish();
	   		
	   		if(presentFile.isDirectory()){
	   			if(presentFile.equals(Environment.getExternalStorageDirectory().getAbsoluteFile())){
	   				Intent data=new Intent();
					data.putExtra("path", "nothing");
					setResult(10, data); 
	   				this.finish();
	   			}
	   			else{
	   				presentFile = presentFile.getParentFile();
	   				File file = presentFile;
	   				File[] files = file.listFiles();
	   				fill(files);
	   			}
	   		}
	   		if(presentFile.isFile()){
	   			if(presentFile.getParentFile().isDirectory()){
	   				presentFile = presentFile.getParentFile();
	   				File file = presentFile;
	   				File[] files = file.listFiles();
	   				fill(files);
	   			}
	   		}
	   	}
		return false;	
	 }

}


