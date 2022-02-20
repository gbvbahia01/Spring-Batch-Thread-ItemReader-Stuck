package br.com.gbvbahia.controller;

public enum Pages {

  NOT_FOUND_404("404_not_found"),
  INTERNAL_ERROR_500("internal_server_error"),
  MONITORING("monitoring");
  
  public final String pageName;

  Pages(String pageName) {
    this.pageName = pageName;
  }

  public String redirect() {
    return String.format("redirect:/page/%s", pageName);
  }
}
