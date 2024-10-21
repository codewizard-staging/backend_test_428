package com.replicacia.rest.admin;

import com.replicacia.rest.admin.dto.HostInfoRequestDTO;
import com.replicacia.rest.admin.dto.HostInfoResponseDTO;
import com.replicacia.rest.admin.service.HostInfoService;
import com.replicacia.rest.security.dto.HostInfoUpdateRequestDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/app/hosts")
@RequiredArgsConstructor
public class HostInfoController {

  private final HostInfoService hostInfoService;

  @PostMapping
  public ResponseEntity<HostInfoResponseDTO> addHostInfo(
      @RequestBody final HostInfoRequestDTO requestDTO) {
    return ResponseEntity.created(URI.create("/app/hosts"))
        .body(this.hostInfoService.addHost(requestDTO));
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<HostInfoResponseDTO> getHostById(@PathVariable final String id) {
    return ResponseEntity.ok().body(this.hostInfoService.getHostById(id));
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteHostById(@PathVariable final String id) {
    this.hostInfoService.deleteHostById(id);
  }

  @GetMapping
  public ResponseEntity<List<HostInfoResponseDTO>> getAllHost() {
    return ResponseEntity.ok().body(this.hostInfoService.getAllHost());
  }

  @PutMapping
  public ResponseEntity<HostInfoResponseDTO> updateHostInfo(
      @RequestBody final HostInfoUpdateRequestDto dto) {
    return ResponseEntity.created(URI.create("/app/hosts"))
        .body(this.hostInfoService.updateHost(dto));
  }
}
