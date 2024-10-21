package com.replicacia.rest.admin.service.impl;

import com.replicacia.exception.ResourceExistsException;
import com.replicacia.exception.ResourceNotFoundException;
import com.replicacia.model.HostInfo;
import com.replicacia.repo.HostInfoRepository;
import com.replicacia.rest.admin.dto.HostInfoRequestDTO;
import com.replicacia.rest.admin.dto.HostInfoResponseDTO;
import com.replicacia.rest.admin.service.HostInfoService;
import com.replicacia.rest.security.dto.HostInfoUpdateRequestDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class HostInfoServiceImpl implements HostInfoService {
  private final HostInfoRepository hostInfoRepository;
  private final ModelMapper mapper;

  @Override
  public HostInfoResponseDTO addHost(final HostInfoRequestDTO hostInfoRequestDTO) {
    final Optional<HostInfo> hostInfoOptional = this.getByIp(hostInfoRequestDTO.getIp());

    if (hostInfoOptional.isPresent()) {
      throw new ResourceExistsException(
          "Host info already exist with IP: " + hostInfoRequestDTO.getIp());
    }

    final HostInfo hostInfo = this.mapper.map(hostInfoRequestDTO, HostInfo.class);
    final HostInfo savedHostInfo = this.hostInfoRepository.save(hostInfo);

    return this.mapper.map(savedHostInfo, HostInfoResponseDTO.class);
  }

  @Override
  public HostInfo getHostByServiceName(final String serviceName) {
    if (StringUtils.isEmpty(serviceName)) {
      return null;
    }
    return this.hostInfoRepository.findByServiceName(serviceName);
  }

  @Override
  public HostInfoResponseDTO getHostById(final String publicId) {
    return this.mapper.map(
        this.hostInfoRepository.findByPublicId(publicId), HostInfoResponseDTO.class);
  }

  @Override
  public void deleteHostById(final String publicId) {
    final Optional<HostInfo> byPublicId = this.hostInfoRepository.findByPublicId(publicId);
    byPublicId.ifPresent(hostInfo -> this.hostInfoRepository.deleteById(hostInfo.getId()));
  }

  @Override
  public List<HostInfoResponseDTO> getAllHost() {
    final List<HostInfo> hostInfos = this.hostInfoRepository.findAll();
    return hostInfos.stream()
        .map(hostInfo -> this.mapper.map(hostInfo, HostInfoResponseDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<HostInfo> getByIp(final String ip) {
    return this.hostInfoRepository.findByIp(ip);
  }

  @Override
  public HostInfoResponseDTO updateHost(final HostInfoUpdateRequestDto dto) {

    final Optional<HostInfo> hostInfoOptional = this.hostInfoRepository.findByPublicId(dto.getId());

    if (!hostInfoOptional.isPresent()) {
      throw new ResourceNotFoundException("Host info with Id: " + dto.getId() + " not exist");
    }

    final HostInfo hostInfo = hostInfoOptional.get();
    hostInfo.setIp(dto.getIp());
    hostInfo.setName(dto.getName());
    hostInfo.setPort(dto.getPort());
    hostInfo.setScheme(dto.getScheme());
    final HostInfo savedHostInfo = this.hostInfoRepository.save(hostInfo);

    return this.mapper.map(savedHostInfo, HostInfoResponseDTO.class);
  }
}
