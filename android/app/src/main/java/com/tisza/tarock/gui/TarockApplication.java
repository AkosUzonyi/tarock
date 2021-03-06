package com.tisza.tarock.gui;

import android.app.*;
import com.squareup.picasso.*;

public class TarockApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		LocaleManager.updateLocale(this);
		ResourceMappings.init(this);
		Picasso.setSingletonInstance(new Picasso.Builder(this).downloader(new OkHttpDownloader(this)).build());
	}
}
