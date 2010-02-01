package org.zkoss.web.util.resource;

public class CustomContentLoader extends ResourceLoader {

		private final ServletContext _ctx;
		private ContentLoader(ServletContext ctx) {
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