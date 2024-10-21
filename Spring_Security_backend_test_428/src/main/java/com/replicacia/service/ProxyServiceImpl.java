package com.replicacia.service;

import com.replicacia.model.HostInfo;
import com.replicacia.rest.admin.service.HostInfoService;
import com.replicacia.utils.WebRequestUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@AllArgsConstructor
public class ProxyServiceImpl implements ProxyService {

  private final RestTemplate restTemplate;
  private final HostInfoService hostResolverService;

  private final WebRequestUtil webRequestUtil;

  @Override
  public ResponseEntity<String> processProxyRequest(
      final String body,
      final HttpMethod method,
      final HttpServletRequest request,
      final HttpServletResponse response,
      final String traceId)
      throws URISyntaxException {

    if (StringUtils.isEmpty(request.getRequestURI())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    final String requestUrl = request.getRequestURI().replaceFirst("/gateway", "");
    final HostInfo hostInfo =
        this.hostResolverService.getHostByServiceName(requestUrl.split("/")[1]);

    if (Objects.isNull(hostInfo)) {
      log.error("Host info is/are not added in DB.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Host info Not Found in database. Please use admin APIs to configure host in DB.");
    }

    URI uri =
        new URI(hostInfo.getScheme(), null, hostInfo.getIp(), hostInfo.getPort(), null, null, null);

    uri =
        UriComponentsBuilder.fromUri(uri)
            .path(requestUrl)
            .query(request.getQueryString())
            .build(true)
            .toUri();

    final HttpEntity<?> httpEntity = this.webRequestUtil.prepareRequestHttpEntity(request, body);

    try {
      final ResponseEntity<String> serverResponse =
          this.restTemplate.exchange(uri, method, httpEntity, String.class);
      log.info(serverResponse.toString());
      return this.refactorResponse(serverResponse);
    } catch (final HttpStatusCodeException e) {
      log.error(e.getMessage());
      return ResponseEntity.status(e.getRawStatusCode())
          .headers(e.getResponseHeaders())
          .body(e.getResponseBodyAsString());
    }
  }

  private ResponseEntity<String> refactorResponse(final ResponseEntity<String> responseEntity) {

    final String responseBody = responseEntity.getBody();

    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.addAll(responseEntity.getHeaders());
    httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
    httpHeaders.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);

    return ResponseEntity.status(responseEntity.getStatusCodeValue())
        .headers(httpHeaders)
        .body(responseBody);
  }
}
