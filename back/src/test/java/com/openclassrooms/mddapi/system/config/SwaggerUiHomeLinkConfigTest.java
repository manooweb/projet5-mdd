package com.openclassrooms.mddapi.system.config;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

class SwaggerUiHomeLinkConfigTest {

  private final SwaggerUiHomeLinkConfig config = new SwaggerUiHomeLinkConfig();

  @Test
  void shouldLeaveOtherBeansUnchanged() {
    Object bean = new Object();

    assertSame(bean, config.postProcessAfterInitialization(bean, "otherBean"));
  }

  @ParameterizedTest
  @MethodSource("swaggerIndexLocations")
  void shouldInjectHomeLinkScriptForSwaggerIndex(String requestUri, String filename)
      throws Exception {
    Resource resource = resource("<html><body>Swagger</body></html>", filename);

    Resource transformed = transform(requestUri, resource);

    assertTrue(contentOf(transformed).contains("/js/swagger-home-link.js"));
  }

  private static Stream<Arguments> swaggerIndexLocations() {
    return Stream.of(
        Arguments.of("/swagger-ui/index.html", "index.html"),
        Arguments.of("/swagger-ui.html", "swagger.html"),
        Arguments.of("/custom-path", "index.html"));
  }

  @Test
  void shouldLeaveNonSwaggerResourcesUnchanged() throws Exception {
    Resource resource = resource("<html><body>Other page</body></html>", "other.html");

    assertSame(resource, transform("/custom-path", resource));
  }

  @Test
  void shouldLeaveAnAlreadyTransformedSwaggerIndexUnchanged() throws Exception {
    Resource resource =
        resource(
            "<html><body><script src=\"/js/swagger-home-link.js\"></script></body></html>",
            "index.html");

    assertSame(resource, transform("/swagger-ui/index.html", resource));
  }

  @Test
  void shouldLeaveSwaggerHtmlWithoutClosingBodyTagUnchanged() throws Exception {
    Resource resource = resource("<html><body>Swagger", "index.html");

    assertSame(resource, transform("/swagger-ui/index.html", resource));
  }

  private Resource transform(String requestUri, Resource resource) throws IOException {
    SwaggerIndexTransformer delegate = mock(SwaggerIndexTransformer.class);
    when(delegate.transform(any(), any(), any())).thenReturn(resource);

    SwaggerIndexTransformer transformer =
        (SwaggerIndexTransformer)
            config.postProcessAfterInitialization(delegate, "swaggerIndexTransformer");
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(requestUri);

    return transformer.transform(request, resource, mock(ResourceTransformerChain.class));
  }

  private Resource resource(String content, String filename) {
    return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
      @Override
      public String getFilename() {
        return filename;
      }

      @Override
      public long lastModified() {
        return 0;
      }
    };
  }

  private String contentOf(Resource resource) throws IOException {
    return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  }
}
