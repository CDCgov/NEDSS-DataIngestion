package gov.cdc.nbs.deduplication.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

class IgnoredPathsTest {

  @Test
  void should_ignore_all_request() {
    // Given an ignore all paths
    IgnoredPaths ignoredPaths = new IgnoredPaths("/**");

    // and a request
    HttpServletRequest request = new MockHttpServletRequest("GET", "/ignored/path");

    // When the request is checked to be ignored
    boolean actual = ignoredPaths.ignored(request);

    // Then the request is ignored
    assertThat(actual).isTrue();
  }

  @Test
  void should_ignore_request() {
    // Given an ignore path
    IgnoredPaths ignoredPaths = new IgnoredPaths("/some/path");

    // and a request with matching path
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/some/path");
    request.setServletPath("/some/path");

    // When the request is checked to be ignored
    boolean actual = ignoredPaths.ignored(request);

    // Then the request is ignored
    assertThat(actual).isTrue();
  }

  @Test
  void should_not_ignore_request() {
    // Given an ignore path
    IgnoredPaths ignoredPaths = new IgnoredPaths("/some/path");

    // and a request
    HttpServletRequest request = new MockHttpServletRequest("GET", "/some/other/path");

    // When the request is checked to be ignored
    boolean actual = ignoredPaths.ignored(request);

    // Then the request is not ignored
    assertThat(actual).isFalse();
  }

  @Test
  void should_not_ignore_requests_when_no_paths_ignored() {
    // Given an ignore path
    IgnoredPaths ignoredPaths = new IgnoredPaths();

    // and a request
    HttpServletRequest request = new MockHttpServletRequest("GET", "/some/other/path");

    // When the request is checked to be ignored
    boolean actual = ignoredPaths.ignored(request);

    // Then the request is not ignored
    assertThat(actual).isFalse();
  }

  @Test
  void should_return_paths() {
    // Given an ignore path
    IgnoredPaths ignoredPaths = new IgnoredPaths(List.of("/some/path", "/some/other/path"));

    // When the paths are extracted
    String[] paths = ignoredPaths.paths();

    // Then the expected paths should be returned
    assertThat(paths).contains("/some/path", "/some/other/path");
  }

}
