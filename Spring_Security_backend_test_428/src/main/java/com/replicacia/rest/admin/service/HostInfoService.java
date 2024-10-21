package com.replicacia.rest.admin.service;

import com.replicacia.model.HostInfo;
import com.replicacia.rest.admin.dto.HostInfoRequestDTO;
import com.replicacia.rest.admin.dto.HostInfoResponseDTO;
import com.replicacia.rest.security.dto.HostInfoUpdateRequestDto;
import java.util.List;
import java.util.Optional;

public interface HostInfoService {

  HostInfoResponseDTO addHost(HostInfoRequestDTO hostInfoRequestDTO);

  HostInfo getHostByServiceName(String serviceName);

  HostInfoResponseDTO getHostById(String id);

  void deleteHostById(String publicId);

  List<HostInfoResponseDTO> getAllHost();

  Optional<HostInfo> getByIp(String ip);

  HostInfoResponseDTO updateHost(HostInfoUpdateRequestDto hostInfo);
}
