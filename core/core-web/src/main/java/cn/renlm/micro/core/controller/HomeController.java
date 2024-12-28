package cn.renlm.micro.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping
public class HomeController {

	@GetMapping({ "/", "index.html" })
	public String index() {
		return "index";
	}

}
