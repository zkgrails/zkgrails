class UrlMappings {
    static mappings = {
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
      "/hello"(view:"/hello.zul")
      "/"(view:"/index")
	  "500"(view:'/error')
	}
}
