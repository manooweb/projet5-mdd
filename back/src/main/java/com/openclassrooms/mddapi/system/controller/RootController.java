package com.openclassrooms.mddapi.system.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Serves the web application home page. */
@Hidden
@Controller
public class RootController {

  /**
   * Displays the home page.
   *
   * @return the Thymeleaf home template name
   */
  @GetMapping("/")
  public String home() {
    return "home";
  }
}
