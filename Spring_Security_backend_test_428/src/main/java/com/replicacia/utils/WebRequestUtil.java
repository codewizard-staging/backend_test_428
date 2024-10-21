package com.replicacia.utils;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebRequestUtil {

  private final FileUtils fileUtils;

  public HttpEntity<?> prepareRequestHttpEntity(
      final HttpServletRequest request, final String body) {

    final HttpHeaders headers = new HttpHeaders();
    final Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      final String headerName = headerNames.nextElement();
      headers.set(headerName, request.getHeader(headerName));
    }
    headers.remove(HttpHeaders.ACCEPT_ENCODING);
    headers.remove(HttpHeaders.HOST);
    headers.remove("ENCODING");
    headers.remove("PROCESS_POST_AS_GET");
    if (request instanceof StandardMultipartHttpServletRequest) {
      final StandardMultipartHttpServletRequest multipartHttpServletRequest =
          (StandardMultipartHttpServletRequest) request;
      final MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();

      multipartHttpServletRequest
          .getFileMap()
          .forEach(
              (s, multipartFile) -> {
                final FileSystemResource fileSystemResource;

                try {
                  fileSystemResource =
                      new FileSystemResource(
                          this.fileUtils.getFile(
                              multipartFile.getInputStream(), multipartFile.getOriginalFilename()));

                } catch (final IOException e) {
                  throw new RuntimeException(e);
                }
                multiValueMap.add(s, fileSystemResource);
              });

      multipartHttpServletRequest.getParameterMap().forEach(multiValueMap::add);

      return new HttpEntity<>(multiValueMap, headers);
    }

    return new HttpEntity<>(body, headers);
  }
}
