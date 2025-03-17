package com.olx.assertx.context;

import static java.lang.ThreadLocal.withInitial;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.service.model.RequestType;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ResponseBody;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton to manage objects and share their state between step definitions.
 */
public enum CucumberTestContext {
  CONTEXT;

  private final ThreadLocal<Map<String, Object>> threadLocal = withInitial(HashMap::new);

  private Map<String, Object> testContextMap() {
    return threadLocal.get();
  }

  public boolean contains(String key) {
    return testContextMap().containsKey(key);
  }

  public void set(String key, Object value) {
    testContextMap().put(key, value);
  }

  public Object get(String key) {
    return testContextMap().get(key);
  }

  public <T> T get(String key, Class<T> clazz) {
    return clazz.cast(testContextMap().get(key));
  }

  public void remove(String key) {
    testContextMap().remove(key);
  }

  public <T> T getResponse(RequestType type, Class<T> clazz) {
    if (type.equals(RequestType.REST)) {
      return fetchResponseBody().as(clazz);
    }
    return null;
  }

  public <T> T getResponse(RequestType type, TypeRef<T> typeRef) {
    if (type.equals(RequestType.REST)) {
      return fetchResponseBody().as(typeRef);
    }
    return null;
  }

  private ResponseBody fetchResponseBody() {
    return ((io.restassured.response.Response) testContextMap()
        .get(RequestKey.RESPONSE.toName())).body();
  }

}
