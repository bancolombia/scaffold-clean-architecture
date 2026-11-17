package io.swagger.codegen.v3.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OpenApiModelPackageResolverTest {

  @ParameterizedTest
  @CsvSource({
    "BookResponse, Book",
    "BookRequest, Book",
    "ResponseStatus, ResponseStatus",
    "RequestedBookResponse, RequestedBook",
    "Response, Response",
    "Author, Author"
  })
  void shouldRemoveOnlyRequestOrResponseSuffix(String input, String expected) {
    assertEquals(expected, OpenApiModelPackageResolver.cleanModelName(input));
  }

  @ParameterizedTest
  @CsvSource({"BookResponse, response", "BookRequest, request", "ResponseStatus, ''", "Author, ''"})
  void shouldResolveFolderOnlyBySuffix(String input, String expected) {
    assertEquals(expected, OpenApiModelPackageResolver.resolveFolderSegment(input));
  }
}
