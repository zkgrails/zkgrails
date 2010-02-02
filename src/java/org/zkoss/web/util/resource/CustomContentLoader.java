package org.zkoss.web.util.resource;

import javax.servlet.ServletContext;
import org.zkoss.io.Files;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.zkoss.util.resource.Locator;
import org.zkoss.zk.ui.metainfo.Parser;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.WebApp;

public class CustomContentLoader extends ResourceLoader {

	private final WebApp _wapp;

	public CustomContentLoader(WebApp wapp) {
		_wapp = wapp;
	}

	//-- super --//
	protected Object parse(String path, File file, Object extra)
	throws Exception {
		System.out.println("parsing file");
		final Locator locator =
			extra != null ? (Locator)extra: PageDefinitions.getLocator(_wapp, path);
		return new Parser(_wapp, locator).parse(file, path);
	}

	protected Object parse(String path, URL url, Object extra)
	throws Exception {
		System.out.println("parsing url");
		final Locator locator =
			extra != null ? (Locator)extra: PageDefinitions.getLocator(_wapp, path);
		return new Parser(_wapp, locator).parse(url, path);		
	}
	
	public static void init() {
		PageDefinitions.setResourceLoaderClass(CustomContentLoader.class);
	}
}

/*
public class CustomContentLoader extends ResourceLoader {

		private final ServletContext _ctx;
		private CustomContentLoader(ServletContext ctx) {
			_ctx = ctx;
		}

		//-- super --//
		protected Object parse(String path, File file, Object extra)
		throws Exception {
			System.out.println("parse0 by CustomContentLoader");
			final InputStream is = new BufferedInputStream(new FileInputStream(file));
			try {
				return readAll(is);
			} finally {
				Files.close(is);
			}
		}

		protected Object parse(String path, URL url, Object extra)
		throws Exception {
			System.out.println("parse1 by CustomContentLoader");
			InputStream is = url.openStream();
			if (is != null) is = new BufferedInputStream(is);
			try {
				return readAll(is);
			} finally {
				Files.close(is);
			}
		}

		private String readAll(InputStream is) throws Exception {
			System.out.println("readAll by CustomContentLoader");
			if (is == null) return null;
			return Files.readAll(new InputStreamReader(is, "UTF-8")).toString();
		}
	}
*/