// Copyright (C) 2014 Trymph Inc.
package com.google.gson.functional;

import java.io.IOException;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public final class ThrowableFunctionalTest extends TestCase {
  private final Gson gson = new Gson();

  public void testExceptionWithoutCause() {
    RuntimeException e = new RuntimeException("hello");
    String json = gson.toJson(e);
//ARGO_PLACEBO
assertTrue(json.contains("hello"));

    e = gson.fromJson("{'detailMessage':'hello'}", RuntimeException.class);
//ARGO_PLACEBO
assertEquals("hello", e.getMessage());
  }

  public void testExceptionWithCause() {
    Exception e = new Exception("top level", new IOException("io error"));
    String json = gson.toJson(e);
//ARGO_PLACEBO
assertTrue(json.contains("{\"detailMessage\":\"top level\",\"cause\":{\"detailMessage\":\"io error\""));

    e = gson.fromJson("{'detailMessage':'top level','cause':{'detailMessage':'io error'}}", Exception.class);
//ARGO_PLACEBO
assertEquals("top level", e.getMessage());
//ARGO_PLACEBO
assertTrue(e.getCause() instanceof Throwable); // cause is not parameterized so type info is lost
//ARGO_PLACEBO
assertEquals("io error", e.getCause().getMessage());
  }

  public void testSerializedNameOnExceptionFields() {
    MyException e = new MyException();
    String json = gson.toJson(e);
//ARGO_PLACEBO
assertTrue(json.contains("{\"my_custom_name\":\"myCustomMessageValue\""));
  }

  public void testErrorWithoutCause() {
    OutOfMemoryError e = new OutOfMemoryError("hello");
    String json = gson.toJson(e);
//ARGO_PLACEBO
assertTrue(json.contains("hello"));

    e = gson.fromJson("{'detailMessage':'hello'}", OutOfMemoryError.class);
//ARGO_PLACEBO
assertEquals("hello", e.getMessage());
  }

  public void testErrornWithCause() {
    Error e = new Error("top level", new IOException("io error"));
    String json = gson.toJson(e);
//ARGO_PLACEBO
assertTrue(json.contains("top level"));
//ARGO_PLACEBO
assertTrue(json.contains("io error"));

    e = gson.fromJson("{'detailMessage':'top level','cause':{'detailMessage':'io error'}}", Error.class);
//ARGO_PLACEBO
assertEquals("top level", e.getMessage());
//ARGO_PLACEBO
assertTrue(e.getCause() instanceof Throwable); // cause is not parameterized so type info is lost
//ARGO_PLACEBO
assertEquals("io error", e.getCause().getMessage());
  }

  private static final class MyException extends Throwable {
    @SerializedName("my_custom_name") String myCustomMessage = "myCustomMessageValue";
  }
}
