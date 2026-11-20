package io.swagger.codegen.v3.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

class OpenApiModelPackageResolverTest {

  @Test
  void shouldPlaceSchemasOnlyWhenListedInOptions() {
    Set<String> request = Set.of("bookSearch");
    Set<String> response = Set.of("bookDetail", "auditTrail");

    assertEquals("", OpenApiModelPackageResolver.resolveFolderSegment("author", request, response));
    assertEquals(
        "request",
        OpenApiModelPackageResolver.resolveFolderSegment("bookSearch", request, response));
    assertEquals(
        "response",
        OpenApiModelPackageResolver.resolveFolderSegment("bookDetail", request, response));
    assertEquals(
        "co.pkg.entities.request",
        OpenApiModelPackageResolver.resolvePackage(
            "co.pkg.entities", "bookSearch", request, response));
    assertEquals(
        "request" + java.io.File.separator + "BookSearch",
        OpenApiModelPackageResolver.modelFilePath("bookSearch", "BookSearch", request, response));
    assertEquals(
        "Author", OpenApiModelPackageResolver.modelFilePath("author", "Author", request, response));
  }
}
