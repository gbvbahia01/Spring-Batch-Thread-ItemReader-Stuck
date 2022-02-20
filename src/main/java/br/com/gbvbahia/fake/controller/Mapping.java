package br.com.gbvbahia.fake.controller;

public interface Mapping {

  String PATH_BASE = "/page";

  public static interface Fake {
    String PATH = PATH_BASE + "/fake";
    String CHANGE_ENVIRONMENT = "/environment/{env}";
  }


}
